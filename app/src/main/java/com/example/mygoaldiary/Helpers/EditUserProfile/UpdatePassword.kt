package com.example.mygoaldiary.Helpers.EditUserProfile

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.InternetController
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.Views.LoginScreen
import com.example.mygoaldiary.Views.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdatePassword : UpdateUsername(){

    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser!!

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
        showAlert.warningAlert("Warning", "Do you want to send password change mail to your email?\n(If you accept, you will be logged out and you will need to login again)", true).apply{
            this.setOnClickListener {
                ShowAlert.mAlertDialog.dismiss()
                loadingDialog.startLoadingDialog()
                mailSender()
            }
        }
    }

    private fun mailSender() {
        auth.sendPasswordResetEmail(currentUser.email!!).addOnSuccessListener {
            logout()
        }.addOnFailureListener {
            loadingDialog.dismissDialog()
            showAlert.successAlert("Error", it.localizedMessage!!, true)
        }
    }

    fun logout() {
        // Kullanıcı hesabından çıkarken 'token'ı null'a çevirmemizin nedeni. Hesabında değilken veya başka hesaptayken önceki hesabından bildirim almamasını istememiz.
        firebase = FirebaseFirestore.getInstance()
        if (InternetController.internetControl(activity)) {
            firebase.collection("Users").document(currentUser.uid).update("notifyToken", null).addOnSuccessListener {
                out()
            }.addOnFailureListener {
                out()
            }
        }else{
            loadingDialog.dismissDialog()
            showAlert.errorAlert("Error", "You must be connected to internet to exit your account.", true)
        }
    }

    private fun out(){
        auth.signOut()
        loadingDialog.dismissDialog()
        showAlert.successAlert("Success", "Mail sent, please check your mailbox.", true)
        val i = Intent(context, LoginScreen::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(i)
    }
}