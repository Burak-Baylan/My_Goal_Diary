package com.example.mygoaldiary.FirebaseManage

import android.app.Activity
import android.content.Context
import com.example.mygoaldiary.LoginScreen

open class FirebaseSuperClass {
    companion object {
        open var context: Context? = null
        open var activity: Activity? = null
    }

    fun fireStoreManage(): Firestore {
        return Firestore(context!!, activity!!)
    }

    fun userAuthManage(): FirebaseAuthClass {
        return FirebaseAuthClass(context!!, activity!!)
    }
}