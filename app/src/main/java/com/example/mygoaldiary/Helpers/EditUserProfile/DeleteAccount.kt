package com.example.mygoaldiary.Helpers.EditUserProfile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.R
import com.example.mygoaldiary.Views.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeleteAccount : UpdateUsername(){

    private lateinit var activity : Activity
    private lateinit var view : View

    private lateinit var currentPasswordEditText : EditText
    private lateinit var destroyAccountBtn : Button
    private lateinit var goBackBtn : ImageView

    private lateinit var currentPassword : String
    private var auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser!!


    fun delete(context : Context, activity : Activity){
        this.activity = activity
        showAlert = ShowAlert(context)
        loadingDialog = LoadingDialog(activity)
        view = createView(context, R.layout.layout_delete_account)
        initializeViews()
        listener()
    }

    override fun listener(){
        goBackBtn.setOnClickListener { alertDialog.dismiss() }
        destroyAccountBtn.setOnClickListener {
            destroyAccount()
        }
    }

    private fun destroyAccount() {
        currentPassword = currentPasswordEditText.text.toString()
        if (currentPassword.isNotEmpty()){
            loadingDialog.startLoadingDialog()
            getCurrentPasswordFromFB()
        }
    }

    private fun getCurrentPasswordFromFB() {
        firebase.collection("Users").document(currentUser.uid).get().addOnSuccessListener {
            if (it.exists() && it != null){
                if (currentPassword == it["userPassword"] as String){
                    lastControl()
                }else{
                    loadingDialog.dismissDialog()
                    showAlert.errorAlert("Error", "Password could not be matched.", true)
                }
            }
        }.addOnFailureListener {
            loadingDialog.dismissDialog()
            showAlert.errorAlert("Error", "Account couldn't destroyed. Please try again.", true)
        }
    }

    private fun lastControl() {
        loadingDialog.dismissDialog()
        showAlert.warningAlert("Warning", "Passwords matched. Are you sure you want to delete your account forever.", false).apply {
            this.setOnClickListener { // Yes Button
                loadingDialog.startLoadingDialog()
                ShowAlert.mAlertDialog.dismiss()
                alertDialog.dismiss()
                run()
            }
        }
    }

    private fun initializeViews(){
        currentPasswordEditText = view.findViewById(R.id.currentPasswordEditText)
        destroyAccountBtn = view.findViewById(R.id.destroyAccountBtn)
        goBackBtn = view.findViewById(R.id.goBackButtonDestroyAccount)
    }

    private fun run(){
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        currentUser.delete().addOnSuccessListener {
            loadingDialog.dismissDialog()
            FirebaseFirestore.getInstance().collection("Users").document(currentUser.uid).delete().addOnSuccessListener {
                Intent(activity, MainActivity::class.java).also {
                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    activity.startActivity(it)
                }
            }.addOnFailureListener {
                loadingDialog.dismissDialog()
                showAlert.errorAlert("Error", it.localizedMessage!!, true)
            }
        }.addOnFailureListener {
            loadingDialog.dismissDialog()
            showAlert.errorAlert("Error", it.localizedMessage!!, true)
        }
    }
}