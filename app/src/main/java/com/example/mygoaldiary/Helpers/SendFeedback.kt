package com.example.mygoaldiary.Helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.EditUserProfile.UpdateUsername
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.R
import com.example.mygoaldiary.Views.ProfileActivity
import com.google.firebase.Timestamp

class SendFeedback : ProfileActivity(){

    private lateinit var view : View
    private lateinit var senButton : Button
    private lateinit var feedbackMessageEt : EditText
    private lateinit var goBackButton : ImageView
    private lateinit var activity : Activity
    private var feedbackMessage : String = ""

    private var forFeedbackAlertDialog = UpdateUsername()

    @SuppressLint("InflateParams")
    fun show(context : Context, activity : Activity){
        loadingDialog = LoadingDialog(activity)
        showAlert = ShowAlert(context)
        this.activity = activity
        view = forFeedbackAlertDialog.createView(context, R.layout.layout_feedback_sender)
        findViews()
        listener()
    }

    private fun findViews() {
        senButton = view.findViewById(R.id.sendFeedbackBtn)
        feedbackMessageEt = view.findViewById(R.id.feedbackEditText)
        goBackButton = view.findViewById(R.id.goBackButtonSendFeedback)

    }

    private fun listener() {
        senButton.setOnClickListener {
            feedbackMessage = feedbackMessageEt.text.toString()
            if (feedbackMessage.isNotEmpty()){
                loadingDialog.startLoadingDialog()
                send(feedbackMessage)
            }
        }
        goBackButton.setOnClickListener {
            forFeedbackAlertDialog.alertDialog.dismiss()
        }
    }

    private fun send(message : String){
        firebase.collection("Feedback").document(currentUser!!.uid).set(getSendData(message)).addOnSuccessListener {
            loadingDialog.dismissDialog()
            forFeedbackAlertDialog.alertDialog.dismiss()
            showAlert.successAlert(activity.getString(R.string.success), activity.getString(R.string.feedbackSent), true)
        }.addOnFailureListener {
            loadingDialog.dismissDialog()
            forFeedbackAlertDialog.alertDialog.dismiss()
            showAlert.errorAlert(activity.getString(R.string.success), activity.getString(R.string.feedbackCouldntSent), true)
        }
    }

    private fun getSendData(message: String): HashMap<String, Any> {
        return hashMapOf(
                "message" to message,
                "timeStamp" to Timestamp.now(),
                "userUid" to currentUser!!.uid
        )
    }
}