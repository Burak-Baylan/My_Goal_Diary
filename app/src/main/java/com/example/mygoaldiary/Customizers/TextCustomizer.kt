package com.example.mygoaldiary.Customizers

import android.text.SpannableString
import android.text.style.UnderlineSpan


class TextCustomizer {

    fun underlinedTextCreator(text: String): SpannableString {
        val spannableText = SpannableString(text)
        spannableText.setSpan(UnderlineSpan(), 0, text.length, 0)
        return spannableText
    }

}