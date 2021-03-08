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

    companion object {
        lateinit var mCtx : Context
        lateinit var mAct : Activity
        val firebase = FirebaseFirestore.getInstance()
        lateinit var createAlert : ShowAlert
        val auth = FirebaseAuth.getInstance()
    }

    fun addData(collectionName: String, documentName: String, data: HashMap<String, Any>, successFun: () -> Unit, failFunction: () -> Unit){
        firebase.collection(collectionName).document(documentName).set(data).addOnSuccessListener {
            successFun()
        }.addOnFailureListener {
            failFunction()
        }
    }
}