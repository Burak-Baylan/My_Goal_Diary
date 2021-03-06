package com.example.mygoaldiary.Helpers.SocialHelpers

import android.app.Activity
import android.content.Context
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostDelete {

    companion object {

        private lateinit var context : Context
        private lateinit var postId : String
        private lateinit var showAlert: ShowAlert
        private lateinit var loadingDialog : LoadingDialog

        fun delete(context : Context, activity : Activity, postId : String) {
            this.context = context
            this.postId = postId
            showAlert = ShowAlert(context)
            loadingDialog = LoadingDialog(activity)
            showAlert.warningAlert("Warning", "If you delete this post, you can't get it back. Are you sure?", true).apply {
                this.setOnClickListener {
                    ShowAlert.mAlertDialog.dismiss()
                    loadingDialog.startLoadingDialog()
                    mDelete()
                }
            }
        }

        private var firebase = FirebaseFirestore.getInstance()

        private fun mDelete() {
            firebase.collection("Posts").document(postId).delete().addOnSuccessListener {
                loadingDialog.dismissDialog()
                showAlert.successAlert("Success", "Post deleted.", true)
            }.addOnFailureListener {
                loadingDialog.dismissDialog()
                showAlert.errorAlert("Error", "Post couldn't deleted.", true)
            }
        }
    }
}