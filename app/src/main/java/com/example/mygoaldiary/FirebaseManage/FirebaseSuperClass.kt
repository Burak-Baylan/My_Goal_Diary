package com.example.mygoaldiary.FirebaseManage

import android.app.Activity
import android.content.Context
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.LoginScreen

open class FirebaseSuperClass{

    companion object {
        var context: Context? = null
        var activity: Activity? = null
        var loadingDialog : LoadingDialog? = null
    }

    constructor(context : Context, activity : Activity){
        FirebaseSuperClass.context = context
        FirebaseSuperClass.activity = activity
        loadingDialog = LoadingDialog(activity)
    }

    constructor()

    fun fireStoreManage(): Firestore {
        return Firestore(context!!, activity!!)
    }

    fun userAuthManage(): FirebaseAuthClass {
        return FirebaseAuthClass(context!!, activity!!)
    }
}