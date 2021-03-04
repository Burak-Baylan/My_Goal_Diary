package com.example.mygoaldiary.FirebaseManage

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Notification.FirebaseService
import com.example.mygoaldiary.Notification.SaveTokenToFirebase
import com.example.mygoaldiary.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.iid.FirebaseInstanceId

class FirebaseAuthClass (private val ctx : Context, private val act : Activity): FirebaseSuperClass() {

    private lateinit var showAlert : ShowAlert

    private val auth = FirebaseAuth.getInstance()
    fun addNewUser (userName : String, userEmail : String, userPassword : String){
        showAlert = ShowAlert(ctx)
        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnSuccessListener {
            val currentId = auth.currentUser!!.uid

            val profileUpdate = userProfileChangeRequest {
                displayName = userName
            }

            auth.currentUser?.let { firstTask ->
                firstTask.updateProfile(profileUpdate).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful){
                        val hashData : HashMap<String, Any> = hashMapOf(
                                "userName" to userName,
                                "userEmail" to userEmail,
                                "userPassword" to userPassword,
                                "userId" to currentId,
                                "pushNotify" to true,
                                "profileVisibility" to true
                        )
                        fireStoreManage().addData("Users", currentId, hashData, { authSuccessFunc() }, { authFailFunc() })
                    }
                    else{
                        authFailFunc()
                    }
                }
            }
        }.addOnFailureListener {
            loadingDialog!!.dismissDialog()
            showAlert.errorAlert(ctx.getString(R.string.error), it.localizedMessage!!, true)
        }
    }

    private fun authSuccessFunc(){
        Toast.makeText(ctx, "successFunc", Toast.LENGTH_SHORT).show()

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            SaveTokenToFirebase().save(it.token)
            FirebaseService.token = it.token
        }

        act.finish()
        loadingDialog!!.dismissDialog()
    }

    private fun authFailFunc(){
        auth.currentUser?.let {
            it.delete().addOnCompleteListener {
                showAlert = ShowAlert(context!!)
                showAlert.errorAlert(ctx.getString(R.string.error), ctx.getString(R.string.userCreateFail), true)
            }
        }
        loadingDialog!!.dismissDialog()
    }

    fun login(userEmail : String, userPassword : String, successFunc : () -> Unit){
        showAlert = ShowAlert(ctx)
        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnSuccessListener {
            successFunc()
        }.addOnFailureListener {
            loadingDialog!!.dismissDialog()
            showAlert.errorAlert(ctx.getString(R.string.error), it.localizedMessage!!, true)
        }
    }

    fun sendForgotPasswordEmail(email : String){
        showAlert = ShowAlert(ctx)
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            showAlert.successAlert(ctx.getString(R.string.success), ctx.getString(R.string.emailSentCheckEmail), true)
            loadingDialog!!.dismissDialog()
        }.addOnFailureListener {
            showAlert.errorAlert(ctx.getString(R.string.error), it.localizedMessage!!, true)
            loadingDialog!!.dismissDialog()
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}