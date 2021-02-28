package com.example.mygoaldiary.Creators

import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout

class ParamsCreator {
    fun linearLayoutLayoutParamsCreator(
            width : Int, height : Int,
            startMargin : Int?, endMargin : Int?, marginBottom : Int?, marginTop : Int?
    ) : LinearLayout.LayoutParams{
        return LinearLayout.LayoutParams(width,height).apply {
            startMargin?.let {
                marginStart = it
            }
            endMargin?.let {
                marginEnd = it
            }
            marginBottom?.let{
                bottomMargin = it
            }
            marginTop?.let{
                topMargin = it
            }
        }
    }

    fun linearLayoutLayoutParamsCreator(width : Int, height : Int) : LinearLayout.LayoutParams{
        return LinearLayout.LayoutParams(width,height)
    }

    fun constraintLayoutLayoutParamsCreator(width : Int, height : Int): ConstraintLayout.LayoutParams {
        return ConstraintLayout.LayoutParams(width, height)
    }

}