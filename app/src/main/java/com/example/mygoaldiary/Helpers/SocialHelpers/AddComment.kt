package com.example.mygoaldiary.Helpers.SocialHelpers

import android.app.Activity
import android.content.Context
import com.example.mygoaldiary.Creators.CommentSheet
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.Helpers.MyHelpers
import com.example.mygoaldiary.Helpers.ShortenWord
import com.example.mygoaldiary.Notification.PushNotification
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference

class AddComment (private val context : Context, private val activity : Activity): SuperSocial() {

    init {
        showAlert = ShowAlert(context)
    }

    private lateinit var uuid : String
    private lateinit var comment : String

    private lateinit var postId : String
    private lateinit var ownerId : String

    private lateinit var postComment : String

    val currentUser = FirebaseAuth.getInstance().currentUser!!

    fun add(postId : String, comment : String, ownerId : String, postComment : String){
        this.comment = comment
        this.postId = postId
        this.ownerId = ownerId
        this.postComment = postComment

        uuid = MyHelpers.getUuid()

        val reference = firebase.collection("Posts").document(postId).collection("Comments")

        send(createHashData(postId), reference)
    }

    private fun send(data: HashMap<String, Any>, reference: CollectionReference) {
        reference.document(uuid).set(data).addOnSuccessListener {
            CommentSheet.commentPostEditText.text.clear()
            CommentSheet(context, activity).getComments()

            val title = "\"${currentUser.displayName}\" Commented on Your Post."
            val message = ShortenWord.shorten(comment, 50, 0, 50, "...")

            PushNotification.normalPush(title, message, ownerId, postId, postComment)
        }.addOnFailureListener {
            showAlert.errorAlert("Error", it.localizedMessage!!, true)
        }
    }

    private fun createHashData(postId : String): HashMap<String, Any> {
        return hashMapOf(
                "ownerId" to currentUser.uid,
                "comment" to comment,
                "likeCount" to 0,
                "commentUuid" to uuid,
                "postId" to postId,
                "date" to GetCurrentDate.getDate(),
                "time" to GetCurrentDate.getTime(),
                "timeStamp" to Timestamp.now(),
                "pushNotify" to true
        )
    }
}