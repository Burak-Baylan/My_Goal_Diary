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
import com.example.mygoaldiary.Views.EditUserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

open class UpdateUsername : EditUserProfile() {

    private lateinit var view : View

    private lateinit var saveUsernameButton : Button
    private lateinit var newUsernameEditText : EditText

    private lateinit var activity : Activity

    private lateinit var newUsername : String
    protected var firebase = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser!!
    protected lateinit var showAlert : ShowAlert
    private lateinit var oldUsername : String
    protected lateinit var loadingDialog : LoadingDialog
    protected lateinit var alertDialog : AlertDialog

    open fun show(context : Context, activity : Activity){
        view = createView(context, R.layout.layout_update_username)
        this.activity = activity
        showAlert = ShowAlert(context)
        loadingDialog = LoadingDialog(activity)
        initializeViews()
        listener()
    }

    protected fun createView(context : Context, layout : Int) : View {
        val builder : AlertDialog.Builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val view = LayoutInflater.from(context).inflate(layout, null)
        builder.setView(view)
        builder.setCancelable(true)
        alertDialog = builder.create()
        if (alertDialog.window != null){
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()
        return view
    }

    private fun initializeViews(){
        saveUsernameButton = view.findViewById(R.id.saveUsernameButton)
        newUsernameEditText = view.findViewById(R.id.newUsernameEditText)

        view.findViewById<ImageView>(R.id.goBackButtonEditUsername).setOnClickListener {
            alertDialog.dismiss()
        }
    }

    protected open fun listener() {
        saveUsernameButton.setOnClickListener {
            newUsername = newUsernameEditText.text.toString()
            if (newUsername.isNotEmpty()){
                alertDialog.dismiss()
                loadingDialog.startLoadingDialog()
                oldUsername = currentUser.displayName!!
                firebase.collection("Users").document(currentUser.uid).update("userName", newUsername).addOnSuccessListener {
                    updateAuth()
                }.addOnFailureListener {
                    loadingDialog.dismissDialog()
                    showAlert.errorAlert("Error", it.localizedMessage!!, true)
                }
            }
        }
    }

    private fun updateAuth(){
        val profileUpdate = userProfileChangeRequest {
            displayName = newUsername
        }
        currentUser.updateProfile(profileUpdate).addOnSuccessListener {
            loadingDialog.dismissDialog()
            showAlert.successAlert("Success", "Username updated.", false).apply {
                this.setOnClickListener {
                    activity.finish()
                }
            }
        }.addOnFailureListener { error ->
            firebase.collection("Users").document(currentUser.uid).update("userName", oldUsername).addOnCompleteListener {
                loadingDialog.dismissDialog()
                showAlert.errorAlert("Error", error.localizedMessage!!, true)
            }
        }
    }
}