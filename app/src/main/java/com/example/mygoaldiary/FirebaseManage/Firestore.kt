package com.example.mygoaldiary.FirebaseManage

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import kotlin.coroutines.CoroutineContext

class Firestore (private val ctx : Context, private val act : Activity) : FirebaseSuperClass(){

    private val firebase = FirebaseFirestore.getInstance()

    /**********************************************************************************************/
    fun addData (collectionName : String, documentName : String, data : HashMap<String, String>, successFun : () -> Unit, failFunction : () -> Unit){
        firebase.collection(collectionName).document(documentName).set(data).addOnSuccessListener {
            successFun()
        }.addOnFailureListener {
            failFunction()
        }
    }

    fun addData (collectionName : String, documentName : String, data : HashMap<String, String>, successMessage : String, failMessage : String){
        firebase.collection(collectionName).document(documentName).set(data).addOnSuccessListener {
            println(successMessage)
        }.addOnFailureListener {
            println(failMessage)
        }
    }
    /**********************************************************************************************/
}