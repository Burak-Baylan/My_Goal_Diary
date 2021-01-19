package com.example.mygoaldiary.Helpers

import android.annotation.SuppressLint
import android.widget.TextView

class WordShortener{

    companion object {

        /** With TextView **/
        @JvmStatic
        fun shorten(word: String, higherThan: Int, startIndex: Int, endIndex: Int, textView: TextView) {
            textView.text = shortenHere(word, higherThan, startIndex, endIndex)
        }

        // Put at end.
        @SuppressLint("SetTextI18n")
        @JvmStatic
        fun shorten(word: String, putAtEnd: String, higherThan: Int, startIndex: Int, endIndex: Int, textView: TextView) {
            val getText = shortenHere(word, higherThan, startIndex, endIndex)

            textView.text = if (getText.length >= higherThan){
                getText + putAtEnd
            }else{
                getText
            }
        }
        /*******************/

        /** With Return **/
        @JvmStatic
        fun shorten(word: String, higherThan: Int, startIndex: Int, endIndex: Int): String {
            return shortenHere(word, higherThan, startIndex, endIndex)
        }

        // Put at end.
        @JvmStatic
        fun shorten(word: String, putAtEnd: String, higherThan: Int, startIndex: Int, endIndex: Int): String {
            return shortenHere(word, higherThan, startIndex, endIndex) + putAtEnd
        }

        /*******************/
        @JvmStatic
        private fun shortenHere(word: String, higherThan: Int, startIndex: Int, endIndex: Int): String {
            var returnWord = word
            if (word.length >= higherThan) {
                println("1. if içinde word: $word ")
                if (word.length >= endIndex) {
                    println("2. if içinde")
                    returnWord = word.substring(startIndex, endIndex)
                }
            }
            return returnWord
        }
    }
}