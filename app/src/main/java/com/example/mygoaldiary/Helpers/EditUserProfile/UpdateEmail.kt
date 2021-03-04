package com.example.mygoaldiary.Helpers.EditUserProfile

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.processNextEventInCurrentThread

class UpdateEmail : UpdateUsername(){

    private lateinit var view : View
    private lateinit var newEmailEditText : EditText
    private lateinit var updateEmailBtn : Button

    private lateinit var newEmail : String
    private lateinit var oldEmail : String

    private lateinit var activity : Activity

    private var auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser!!

    override fun show(context : Context, activity : Activity){
        view = createView(context, R.layout.layout_update_email)
        this.activity = activity
        loadingDialog = LoadingDialog(activity)
        showAlert = ShowAlert(context)
        initializeViews()
        listener()
    }

    private fun initializeViews(){
        newEmailEditText = view.findViewById(R.id.newEmailEditText)
        updateEmailBtn = view.findViewById(R.id.saveEmailButton)

        view.findViewById<ImageView>(R.id.goBackButtonEditEmail).setOnClickListener {
            alertDialog.dismiss()
        }
    }

    override fun listener(){
        updateEmailBtn.setOnClickListener {
            newEmail = newEmailEditText.text.toString()
            if(newEmail.isNotEmpty()) {
                alertDialog.dismiss()
                oldEmail = currentUser.email!!
                loadingDialog.startLoadingDialog()
                firebase.collection("Users").document(currentUser.uid).update("userEmail", newEmail).addOnSuccessListener {
                    updateEmail()
                }.addOnFailureListener {
                    loadingDialog.dismissDialog()
                    showAlert.errorAlert("Error", it.localizedMessage!!, true)
                }
            }
        }
    }

    private fun updateEmail() {
        currentUser.updateEmail(newEmail).addOnSuccessListener {
            loadingDialog.dismissDialog()
            showAlert.successAlert("Success", "Email updated.", false).apply {
                this.setOnClickListener {
                    activity.finish()
                }
            }
        }.addOnFailureListener { error ->
            firebase.collection("Users").document(currentUser.uid).update("userEmail", oldEmail).addOnCompleteListener {
                loadingDialog.dismissDialog()
                showAlert.errorAlert("Error", error.localizedMessage!!, true)
            }
        }
    }
}