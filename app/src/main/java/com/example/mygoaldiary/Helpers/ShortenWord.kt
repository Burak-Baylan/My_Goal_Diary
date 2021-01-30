package com.example.mygoaldiary.Helpers

import android.widget.TextView

class ShortenWord{

    companion object {
        /** With TextView **/
        fun shorten(word: String, higherThan: Int, startIndex: Int, endIndex: Int, textView: TextView) {
            textView.text = ShortenWord().shortenHere(word, higherThan, startIndex, endIndex)
        }

        // Put at end.
        fun shorten(word: String, putAtEnd: String, higherThan: Int, startIndex: Int, endIndex: Int, textView: TextView) {
            val getText = ShortenWord().shortenHere(word, higherThan, startIndex, endIndex)
            textView.setText(if (getText.length >= higherThan) {
                getText + putAtEnd
            } else {
                getText
            })
        }
        /*******************/

        /** With Return **/
        fun shorten(word: String, higherThan: Int, startIndex: Int, endIndex: Int): String {
            return ShortenWord().shortenHere(word, higherThan, startIndex, endIndex)
        }

        // Put at end.
        fun shorten(word: String, higherThan: Int, startIndex: Int, endIndex: Int, putAtEnd: String): String {
            val getText = ShortenWord().shortenHere(word, higherThan, startIndex, endIndex)
            return if (getText.length >= higherThan) {
                getText + putAtEnd
            } else {
                getText
            }
        }
    }

        /*******************/
        private fun shortenHere(word: String, higherThan: Int, startIndex: Int, endIndex: Int): String {
            var returnWord = word
            if (word.length >= higherThan) {
                if (word.length >= endIndex) {
                    returnWord = word.substring(startIndex, endIndex)
                }
            }
            return returnWord
        }
}