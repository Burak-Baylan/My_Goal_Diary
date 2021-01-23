package com.example.mygoaldiary.Helpers

import java.text.SimpleDateFormat
import java.util.*

class GetCurrentDate {

    companion object {
        fun getDate(): String {
            val yearDateSdf = SimpleDateFormat("yyyy-MM-dd")
            return yearDateSdf.format(Date())
        }

        fun getTime(): String {
            val timeSdf = SimpleDateFormat("HH:mm:ss")
            return timeSdf.format(Date())
        }
    }
}