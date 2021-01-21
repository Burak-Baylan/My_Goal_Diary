package com.example.mygoaldiary.Helpers

import android.app.Activity

class MyHelpers {

    companion object {
        fun internetControl(activity: Activity): Boolean {
            return InternetController.internetControl(activity)
        }

        fun wordShortener(): WordShortener {
            return WordShortener()
        }
    }

}