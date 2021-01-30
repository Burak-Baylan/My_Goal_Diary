package com.example.mygoaldiary.Helpers

import android.app.Activity
import java.util.*

open class MyHelpers {

    companion object {
        fun internetControl(activity: Activity): Boolean {
            return InternetController.internetControl(activity)
        }

        fun shortenWord(): ShortenWord {
            return ShortenWord()
        }

        fun showOrHide(): ShowOrHide.Companion {
            return ShowOrHide
        }

        fun getDate() : GetCurrentDate.Companion{
            return GetCurrentDate
        }

        fun getUuid() : String{
            return UUID.randomUUID().toString()
        }

        fun filter(): SearchFilter.Companion {
            return SearchFilter
        }
    }

}