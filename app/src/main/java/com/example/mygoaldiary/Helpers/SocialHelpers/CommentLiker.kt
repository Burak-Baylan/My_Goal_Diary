package com.example.mygoaldiary.Helpers.SocialHelpers

import android.graphics.Color
import android.widget.ImageView
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class CommentLiker {

    private lateinit var currentUser : FirebaseUser
    private lateinit var commentId : String
    private lateinit var postId : String
    private lateinit var likeButton : ImageView

    private var firebase = FirebaseFirestore.getInstance()

    companion object{
        var isLock = false
    }
    private lateinit var reference: DocumentReference
    fun askLike(currentUser : FirebaseUser, postId : String, commentId : String, likeButton : ImageView){
        if (!isLock) {
            isLock = true
            this.currentUser = currentUser
            this.commentId = commentId
            this.postId = postId
            this.likeButton = likeButton
            reference = firebase.collection("Posts").document(postId).collection("Comments").document(commentId)
            checkIfExists()
        }else{
            println("Kitli")
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
        reference.collection("Likes").document(currentUserId).set(data).addOnSuccessListener {
            getCurrentLikeCount(reference, true)
            println("Liked")
        }.addOnFailureListener {
            println("Like error")
            isLock = false
        }
    }

    private fun getCurrentLikeCount(reference: DocumentReference, getOrPutLike : Boolean) { // putLike = true, getLike = false
        reference.collection("Likes").get().addOnSuccessListener {

            isLock = if (!it.isEmpty){
                putNewLikeCount(reference, it.count(), getOrPutLike)

                val colorString : String =
                        if (getOrPutLike) "#32A852"
                        else "#D1D1D1"

                likeButton.setColorFilter(Color.parseColor(colorString))

                false
            }else{
                println("Current like data null")
                false
            }
        }.addOnFailureListener {
            println("Get current like error")
            isLock = false
        }
    }

    private fun getBackLike() {
        reference.collection("Likes").document(currentUserId).delete().addOnSuccessListener {
            println("Get back like")
            getCurrentLikeCount(reference, false) // Decrase
            likeButton.setColorFilter(Color.parseColor("#D1D1D1"))
        }.addOnFailureListener {
            println("Get back like error")
            isLock = false
        }
    }

    private fun putNewLikeCount(reference: DocumentReference, likeCount : Int, getOrPutLike: Boolean) {
        var likeCountHere = likeCount
        val colorString : String

        if (getOrPutLike) { // Put (Incrase)
            likeCountHere += 1
            colorString = "#32A852"
        }else{ // Get back (Decrase)
            likeCountHere -= 1
            colorString = "#D1D1D1"
        }

        reference.update("likeCount", likeCountHere).addOnSuccessListener {
            likeButton.setColorFilter(Color.parseColor(colorString))
            isLock = false
        }.addOnFailureListener {
            println("Like güncellerken hata oldu: ${it.localizedMessage!!}")
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

}