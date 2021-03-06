package com.example.mygoaldiary.Helpers.SocialHelpers

import android.graphics.Color

class EditTextSizeListener {
    companion object {
        fun control(length: Int): Int {
            return when {
                length == 250 -> Color.parseColor("#F05454")
                length > 230 -> Color.parseColor("#E07d14")
                else -> Color.parseColor("#1BA1D1")
            }
        }
    }
}