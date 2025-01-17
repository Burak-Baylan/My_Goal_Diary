package com.example.mygoaldiary.Helpers

import android.view.View
import android.widget.ImageView
import com.example.mygoaldiary.R

class ShowOrHide : MyHelpers() {

    companion object {
        fun showOrHide(controlBool: Boolean, hideOrShowResourceImageView: ImageView, vararg showOrHideTextView: View): Boolean {
            return if (controlBool) {
                for (i in showOrHideTextView) {
                    i.visibility = View.GONE
                }
                hideOrShowResourceImageView.setImageResource(R.drawable.ic_down_arrow)
                false
            } else {
                for (i in showOrHideTextView) {
                    i.visibility = View.VISIBLE
                }
                hideOrShowResourceImageView.setImageResource(R.drawable.ic_up_arrow)
                true
            }
        }
    }
}