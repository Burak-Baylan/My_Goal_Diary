package com.example.mygoaldiary.Customizers

import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import com.example.mygoaldiary.R
class TextCustomizer{

    companion object {
        fun String.underlinedTextCreator(): SpannableString {
            val spannableText = SpannableString(this)
            spannableText.setSpan(UnderlineSpan(), 0, this.length, 0)
            return spannableText
        }
    }
}