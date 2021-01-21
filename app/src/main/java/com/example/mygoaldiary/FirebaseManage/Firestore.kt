package com.example.mygoaldiary.FirebaseManage

import android.app.Activity
import android.content.Context
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Firestore(private val ctx: Context, private val act: Activity) : FirebaseSuperClass(){

    private val firebase = FirebaseFirestore.getInstance()
    private var createAlert = ShowAlert(ctx)
    private val auth = FirebaseAuth.getInstance()

    /**********************************************************************************************/
    fun addData(collectionName: String, documentName: String, data: HashMap<String, Any>, successFun: () -> Unit, failFunction: () -> Unit){
        firebase.collection(collectionName).document(documentName).set(data).addOnSuccessListener {
            successFun()
        }.addOnFailureListener {
            failFunction()
        }
    }

    fun addData(
            collectionName1: String, documentName1: String, collectionName2: String, documentName2: String,
            data: HashMap<String, Any>, successFun: () -> Unit, failFunction: () -> Unit
    ){
        firebase.collection(collectionName1).document(documentName1).collection(collectionName2).document(documentName2).set(data).addOnSuccessListener {
            successFun()
        }.addOnFailureListener {
            failFunction()
        }
    }

    fun addData(collectionName: String, documentName: String, data: HashMap<String, Any>, successMessage: Int, failMessage: Int){
        firebase.collection(collectionName).document(documentName).set(data).addOnSuccessListener {
            loadingDialog!!.dismissDialog()
            createAlert.successAlert(ctx.getString(R.string.success), ctx.getString(successMessage), true)
            println(successMessage)
        }.addOnFailureListener {
            loadingDialog!!.dismissDialog()
            createAlert.errorAlert(ctx.getString(R.string.error), ctx.getString(failMessage), true)
            println(failMessage)
        }
    }
    /**********************************************************************************************/
}