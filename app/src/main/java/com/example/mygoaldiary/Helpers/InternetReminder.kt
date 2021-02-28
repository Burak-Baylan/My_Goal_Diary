package com.example.mygoaldiary.Helpers

import android.app.Activity
import android.content.Context
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.R

class InternetReminder(){

    companion object {
        private lateinit var mAct : Activity
        private lateinit var mCtx : Context

        fun remind(activity: Activity, context: Context, message : String) : Boolean {
            this.mAct = activity
            this.mCtx = context
            if (!controlCurrentInternet()) {
                showMessage(message)
                return false
            }
            return true
        }

        private fun showMessage(message : String) {
            val showAlert = ShowAlert(mCtx)
            showAlert.errorAlert(mAct.getString(R.string.error), message, true)
        }

        private fun controlCurrentInternet() = InternetController.internetControl(mAct)

    }
}