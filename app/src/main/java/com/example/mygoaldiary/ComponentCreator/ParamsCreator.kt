package com.example.mygoaldiary.ComponentCreator

import android.widget.LinearLayout

class ParamsCreator {
    fun linearLayoutLayoutParamsCreator(
            width : Int, height : Int,
            startMargin : Int?, endMargin : Int?, marginBottom : Int?, marginTop : Int?
    ) : LinearLayout.LayoutParams{
        return LinearLayout.LayoutParams(width,height).apply {
            if (startMargin != null) {
                marginStart = startMargin
            }
            if (endMargin != null) {
                marginEnd = endMargin
            }
            if (marginBottom != null) {
                bottomMargin = marginBottom
            }
            if (marginTop != null) {
                topMargin = marginTop
            }
        }
    }
}