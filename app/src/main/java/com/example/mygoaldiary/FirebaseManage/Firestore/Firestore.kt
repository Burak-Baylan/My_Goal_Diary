package com.example.mygoaldiary.FirebaseManage.Firestore

import android.app.Activity
import android.content.Context
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass
import com.example.mygoaldiary.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

open class Firestore : FirebaseSuperClass{

    constructor(ctx: Context, act: Activity){
        mCtx = ctx
        mAct = act
        createAlert = ShowAlert(mCtx)
    }
    constructor(){

    }

    companion object {
        lateinit var mCtx : Context
        lateinit var mAct : Activity
        val firebase = FirebaseFirestore.getInstance()
        lateinit var createAlert : ShowAlert
        val auth = FirebaseAuth.getInstance()
    }

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

    fun addData(
            collectionName1: String, documentName1: String, collectionName2: String,
            data: HashMap<String, Any>, successFun: () -> Unit, failFunction: () -> Unit
    ){
        firebase.collection(collectionName1).document(documentName1).collection(collectionName2).document().set(data).addOnSuccessListener {
            successFun()
        }.addOnFailureListener {
            failFunction()
        }
    }

    fun addData(collectionName: String, documentName: String, data: HashMap<String, Any>, successMessage: Int, failMessage: Int){
        firebase.collection(collectionName).document(documentName).set(data).addOnSuccessListener {
            loadingDialog!!.dismissDialog()
            createAlert.successAlert(mCtx.getString(R.string.success), mCtx.getString(successMessage), true)
            println(successMessage)
        }.addOnFailureListener {
            loadingDialog!!.dismissDialog()
            createAlert.errorAlert(mCtx.getString(R.string.error), mCtx.getString(failMessage), true)
            println(failMessage)
        }
    }
    /**********************************************************************************************/

    fun deleteData(): Delete.Companion {
        return Delete
    }
}