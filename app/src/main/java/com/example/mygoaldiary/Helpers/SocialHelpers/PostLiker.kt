package com.example.mygoaldiary.Helpers.SocialHelpers

import android.graphics.Color
import android.widget.ImageView
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.Notification.FirebaseService
import com.example.mygoaldiary.Notification.PushNotification
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

class PostLiker : SuperSocial(){

    private lateinit var currentUser : FirebaseUser
    private lateinit var postId : String
    private lateinit var likeButton : ImageView
    private lateinit var postOwnerId : String

    private lateinit var comment : String

    companion object{
        var isLock = false
    }

    fun askLike(currentUser : FirebaseUser, postId : String, postOwnerId : String, comment : String, likeButton : ImageView){
        if (!isLock) {
            isLock = true
            this.currentUser = currentUser
            this.postId = postId
            this.postOwnerId = postOwnerId
            this.likeButton = likeButton
            this.comment = comment
            checkIfExists()
        }
    }

    private fun checkIfExists(){
        currentUserId = currentUser.uid
        val reference = firebase.collection("Posts").document(postId).collection("Likes").whereEqualTo("userId", currentUserId)
        reference.get().addOnSuccessListener {
            if (it.isEmpty) {
                like()
            }else{
                getBackLike()
            }
        }.addOnFailureListener {
            println("PostLiker: ${it.localizedMessage!!}")
        }
    }

    private lateinit var currentUserId : String
    private fun like(){
        val data = getLikeData()
        val reference = firebase.collection("Posts").document(postId)
        likeColor()
        reference.collection("Likes").document(currentUserId).set(data).addOnSuccessListener {
            println("Liked")
            addLikedPostFromUser()
        }.addOnFailureListener {
            println("Like error")
            unlikeColor()
            isLock = false
        }
    }

    private fun addLikedPostFromUser() {
        val likeData = hashMapOf(
                "likedPost" to postId,
                "liker" to currentUserId,
                "timeStamp" to Timestamp.now(),
                "date" to GetCurrentDate.getDate(),
                "time" to GetCurrentDate.getTime()
        )
        likeColor()
        firebase.collection("Users").document(currentUserId).collection("Likes").document(postId).set(likeData).addOnSuccessListener {
            sendNotify()
            isLock = false
        }.addOnFailureListener {
            unlikeColor()
            isLock = false
        }
    }

    private fun removeLikedPostFromUser() {
        unlikeColor()
        firebase.collection("Users").document(currentUserId).collection("Likes").document(postId).delete().addOnSuccessListener {
            isLock = false
        }.addOnFailureListener {
            likeColor()
            isLock = false
        }
    }

    private fun unlikeColor() = likeButton.setColorFilter(Color.parseColor("#D1D1D1"))
    private fun likeColor() = likeButton.setColorFilter(Color.parseColor("#32A852"))

    private fun getBackLike() {
        val reference = firebase.collection("Posts").document(postId)
        unlikeColor()
        reference.collection("Likes").document(currentUserId).delete().addOnSuccessListener {
            println("Get back like")
            removeLikedPostFromUser()
        }.addOnFailureListener {
            println("Get back like error")
            likeColor()
            isLock = false
        }
    }

    private fun getLikeData(): HashMap<String, Any> {
        return hashMapOf(
                "userId" to currentUserId,
                "date" to GetCurrentDate.getDate(),
                "time" to GetCurrentDate.getTime(),
                "timeStamp" to Timestamp.now()
        )
    }

    private fun sendNotify(){
        val title = "Your Post Liked"
        val message = "\"${currentUser.displayName}\" Liked your post."
        PushNotification.normalPush(title, message, postOwnerId, postId, comment)
    }
}