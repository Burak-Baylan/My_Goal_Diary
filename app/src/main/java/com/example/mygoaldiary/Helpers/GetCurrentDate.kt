package com.example.mygoaldiary.Helpers

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class GetCurrentDate {
    companion object {

        private lateinit var sdf : SimpleDateFormat

        @SuppressLint("SimpleDateFormat")
        fun getDate(): String {
            sdf = SimpleDateFormat("dd/MM/yyyy")
            return sdf.format(Date())
        }

        @SuppressLint("SimpleDateFormat")
        fun getTime(): String {
            sdf = SimpleDateFormat("HH:mm:ss")
            return sdf.format(Date())
        }
        
        fun getDateAndTime() : String{
            return "${getDate()} ${getTime()}"
        }
    }
}