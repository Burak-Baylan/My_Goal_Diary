package com.example.mygoaldiary.Customizers

import android.graphics.Paint
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView

class TextCustomizer{

    companion object {
        fun String.underlinedTextCreator(): SpannableString {
            val spannableText = SpannableString(this)
            spannableText.setSpan(UnderlineSpan(), 0, this.length, 0)
            return spannableText
        }

        fun TextView.strikeThrough(){
            this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        fun TextView.setDefaultFlag(){
            this.paintFlags = 1
        }

    }
}