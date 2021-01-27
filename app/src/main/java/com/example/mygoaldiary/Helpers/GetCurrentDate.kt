package com.example.mygoaldiary.Helpers

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class GetCurrentDate {

    companion object {
        @SuppressLint("SimpleDateFormat")
        fun getDate(): String {
            val yearDateSdf = SimpleDateFormat("yyyy-MM-dd")
            return yearDateSdf.format(Date())
        }

        @SuppressLint("SimpleDateFormat")
        fun getTime(): String {
            val timeSdf = SimpleDateFormat("HH:mm:ss")
            return timeSdf.format(Date())
        }
    }
}