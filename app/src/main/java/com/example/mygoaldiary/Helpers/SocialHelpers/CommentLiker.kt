package com.example.mygoaldiary.Helpers.SocialHelpers

import android.graphics.Color
import android.widget.ImageView
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.Helpers.ShortenWord
import com.example.mygoaldiary.Notification.PushNotification
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class CommentLiker {

    private lateinit var currentUser : FirebaseUser
    private lateinit var commentId : String
    private lateinit var commentUserId : String
    private lateinit var postId : String
    private lateinit var likeButton : ImageView

    private lateinit var commentString : String

    private var firebase = FirebaseFirestore.getInstance()

    companion object{
        var isLock = false
    }
    private lateinit var reference: DocumentReference
    fun askLike(currentUser : FirebaseUser, commentUser : String, postId : String, commentId : String, likeButton : ImageView, commentText : String){
        if (!isLock) {
            isLock = true
            this.commentUserId = commentUser
            this.currentUser = currentUser
            this.commentId = commentId
            this.postId = postId
            this.likeButton = likeButton
            this.commentString = commentText
            reference = firebase.collection("Posts").document(postId).collection("Comments").document(commentId)
            checkIfExists()
        }
    }

    private fun checkIfExists(){
        currentUserId = currentUser.uid
        reference.collection("Likes").whereEqualTo("ownerId", currentUserId).get().addOnSuccessListener {
            if (it.isEmpty) {
                like()
            }else{
                getBackLike()
            }
        }.addOnFailureListener {
            println("Fayıl")
        }
    }

    private lateinit var currentUserId : String
    private fun like(){
        val data = getLikeData()
        likeColor()
        reference.collection("Likes").document(currentUserId).set(data).addOnSuccessListener {
            sendNotify()
            isLock = false
        }.addOnFailureListener {
            unlikeColor()
            isLock = false
        }
    }

    private fun sendNotify() {
        val title = "Your Comment Liked"
        val message = "\"${currentUser.displayName}\" Liked your comment."

        PushNotification.commentPush(title, message, commentUserId, postId, commentId, commentString)
    }

    private fun getBackLike() {
        unlikeColor()
        reference.collection("Likes").document(currentUserId).delete().addOnSuccessListener {
            isLock = false
        }.addOnFailureListener {
            likeColor()
            isLock = false
        }
    }

    private fun getLikeData(): HashMap<String, Any> {
        return hashMapOf(
                "ownerId" to currentUserId,
                "date" to GetCurrentDate.getDate(),
                "time" to GetCurrentDate.getTime(),
                "timeStamp" to Timestamp.now()
        )
    }

    private fun unlikeColor() = likeButton.setColorFilter(Color.parseColor("#D1D1D1"))
    private fun likeColor() = likeButton.setColorFilter(Color.parseColor("#32A852"))

}