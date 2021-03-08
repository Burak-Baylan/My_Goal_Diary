package com.example.mygoaldiary.Helpers.SocialHelpers

import android.app.Activity
import android.content.Context
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostDelete {

    companion object {

        private lateinit var context : Context
        private lateinit var postId : String
        private lateinit var showAlert: ShowAlert
        private lateinit var loadingDialog : LoadingDialog
        private lateinit var activity : Activity

        fun delete(context : Context, activity : Activity, postId : String) {
            this.context = context
            this.postId = postId
            this.activity = activity
            showAlert = ShowAlert(context)
            loadingDialog = LoadingDialog(activity)
            showAlert.warningAlert(activity.getString(R.string.warning), activity.getString(R.string.deletePostCouldntGetBack), true).apply {
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
                showAlert.successAlert(activity.getString(R.string.success), activity.getString(R.string.postDeleted), true)
            }.addOnFailureListener {
                loadingDialog.dismissDialog()
                showAlert.errorAlert(activity.getString(R.string.error), activity.getString(R.string.postCouldntDeleted), true)
            }
        }
    }
}