package com.example.mygoaldiary.FirebaseManage

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.example.mygoaldiary.ComponentCreator.ShowAlert
import com.example.mygoaldiary.LoadingDialog
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthClass (private val ctx : Context, private val act : Activity): FirebaseSuperClass() {

    private lateinit var showAlert : ShowAlert
    private var loadingDialog = LoadingDialog(act)

    private val auth = FirebaseAuth.getInstance()
    fun addNewUser (userName : String, userEmail : String, userPassword : String){
        showAlert = ShowAlert(ctx)
        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnSuccessListener {
            val currentId = auth.currentUser.apply {
            }!!.uid
            val hashData = hashMapOf(
                    "userName" to userName,
                    "userEmail" to userEmail,
                    "userPassword" to userPassword,
                    "userId" to currentId
            )
            fireStoreManage().addData("Users", currentId, hashData, "Register Success", "Register Failed")
        }.addOnFailureListener {
            loadingDialog.dismissDialog()
            showAlert.errorAlert("Error", it.localizedMessage!!, true)
        }
    }

    fun successFunc(){
        Toast.makeText(ctx, "successFunc", Toast.LENGTH_SHORT).show()
        loadingDialog.dismissDialog()
    }

    fun failFunc(){
        Toast.makeText(ctx, "failFunc", Toast.LENGTH_SHORT).show()
        loadingDialog.dismissDialog()
    }

    fun login(userEmail : String, userPassword : String){
        showAlert = ShowAlert(ctx)
        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnSuccessListener {
            return@addOnSuccessListener
        }.addOnFailureListener {
            loadingDialog.dismissDialog()
            showAlert.errorAlert("Error", it.localizedMessage!!, true)
        }
    }
}