package com.example.mygoaldiary.Helpers

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager

open class InternetController (val activity : Activity){

    /*companion object {
        var internet: Boolean = InternetController().internetControl(activity)
    }*/

    private fun internetControl(activity: Activity):Boolean{
        val connectivityManager= activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return networkInfo!=null && networkInfo.isConnected
    }

}