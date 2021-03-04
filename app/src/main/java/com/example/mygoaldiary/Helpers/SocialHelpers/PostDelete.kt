package com.example.mygoaldiary.Helpers.SocialHelpers

import android.content.Context
import com.example.mygoaldiary.Creators.ShowAlert
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostDelete {

    companion object {

        private lateinit var context : Context
        private lateinit var postId : String
        private lateinit var showAlert: ShowAlert

        fun delete(context : Context, postId : String) {
            this.context = context
            this.postId = postId
            showAlert = ShowAlert(context)
            showAlert.warningAlert("Warning", "If you delete this post, you can't get it back. Are you sure?", true).apply {
                this.setOnClickListener {
                    mDelete()
                }
            }
        }

        private var firebase = FirebaseFirestore.getInstance()

        private fun mDelete() {
            firebase.collection("Posts").document(postId).delete().addOnSuccessListener {
                showAlert.successAlert("Success", "Post deleted.", true)
            }.addOnFailureListener {
                showAlert.errorAlert("Error", "Post couldn't deleted.", true)
            }
        }
    }
}