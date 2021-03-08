package com.example.mygoaldiary.Helpers.EditUserProfile

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.InternetController
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.R
import com.example.mygoaldiary.Views.LoginScreen
import com.example.mygoaldiary.Views.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdatePassword : UpdateUsername(){

    private val auth = FirebaseAuth.getInstance()

    private lateinit var context : Context
    private lateinit var activity : Activity

    fun sendMail(context : Context, activity : Activity){
        this.context = context
        this.activity = activity
        showAlert = ShowAlert(context)
        loadingDialog = LoadingDialog(activity)
        askForMail()
    }

    private fun askForMail() {
        showAlert.warningAlert(activity.getString(R.string.warning), "${activity.getString(R.string.doYouWantSendUpdatePasswordMail)}\n(${activity.getString(R.string.ifYouAcceptSendUpdatePasswordMail)})", true).apply{
            this.setOnClickListener {
                if (InternetController.internetControl(activity)) {
                    ShowAlert.mAlertDialog.dismiss()
                    loadingDialog.startLoadingDialog()
                    mailSender()
                }else{
                    showAlert.errorAlert(activity.getString(R.string.error), activity.getString(R.string.internetRequired), true)
                }
            }
        }
    }

    private fun mailSender() {
        auth.sendPasswordResetEmail(currentUser!!.email!!).addOnSuccessListener {
            logout()
        }.addOnFailureListener {
            loadingDialog.dismissDialog()
            showAlert.successAlert(activity.getString(R.string.error), it.localizedMessage!!, true)
        }
    }

    private fun logout() {
        // Kullanıcı hesabından çıkarken 'token'ı null'a çevirmemizin nedeni. Hesabında değilken veya başka hesaptayken önceki hesabından bildirim almamasını istememiz.
        firebase = FirebaseFirestore.getInstance()
        if (InternetController.internetControl(activity)) {
            firebase.collection("Users").document(currentUser!!.uid).update("notifyToken", null).addOnSuccessListener {
                out()
            }.addOnFailureListener {
                out()
            }
        }else{
            loadingDialog.dismissDialog()
            showAlert.errorAlert(activity.getString(R.string.error), activity.getString(R.string.internetRequiredToExit), true)
        }
    }

    private fun out(){
        auth.signOut()
        loadingDialog.dismissDialog()
        showAlert.successAlert(activity.getString(R.string.success), activity.getString(R.string.emailSentCheckEmail), true)
        val i = Intent(context, LoginScreen::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(i)
    }
}