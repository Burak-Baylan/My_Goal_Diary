package com.example.mygoaldiary.Notification

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PushNotification {

    companion object {

        private val firebase = FirebaseFirestore.getInstance()
        private lateinit var title : String
        private lateinit var message : String
        private lateinit var postOwnerId : String
        private lateinit var postId : String
        private lateinit var comment : String
        private lateinit var likeReference: DocumentReference

        //*******//
        fun commentPush(title: String, message: String, postOwnerId : String, postId : String, commentId : String){

            this.title = title
            this.message = message
            this.postOwnerId = postOwnerId

            likeReference = firebase.collection("Posts").document(postId).collection("Comments").document(commentId)

            userNotifyControl()

        }

        fun normalPush(title: String, message: String, postOwnerId : String, postId : String, comment : String){

            this.title = title
            this.message = message
            this.postOwnerId = postOwnerId
            this.postId = postId
            this.comment = comment
            likeReference = firebase.collection("Posts").document(postId)

            userNotifyControl()
        }

        private fun userNotifyControl() {
            firebase.collection("Users").document(postOwnerId).get().addOnSuccessListener {
                if (it.exists() && it != null){
                    val pushNotify = it["pushNotify"] as Boolean
                    if (pushNotify) {
                        val recipientToken = it["notifyToken"] as String?
                        if (recipientToken != null) {
                            postNotifyControl(recipientToken)
                        }
                    }// Else do nothing
                }
            }
        }

        private fun postNotifyControl(recipientToken : String){

            likeReference.get().addOnSuccessListener {
                if (it.exists() && it != null){
                    val pushNotify = it["pushNotify"] as Boolean
                    if (pushNotify) {
                        PushNotificationData(
                            NotificationData(title, message, postId, postOwnerId, comment),
                            recipientToken
                        ).also { pushNotification ->
                            sendNotification(pushNotification)
                        }
                    }// Else do nothing
                }
            }
        }

        private fun sendNotification(notification: PushNotificationData) =
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.api.postNotification(notification)

                    if (response.isSuccessful) {
                        println("Response ${Gson().toJson(response)}")
                    } else {
                        println("NOTY ERROR: ${response.errorBody().toString()}")
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage!!)
                }
            }
    }
}