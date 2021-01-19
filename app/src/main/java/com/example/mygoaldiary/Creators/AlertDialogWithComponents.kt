package com.example.mygoaldiary.Creators

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.example.mygoaldiary.R

class AlertDialogWithComponents (val context : Context, val activity : Activity){

    private val paramsCreator = ParamsCreator()

    fun showAlertDialog (vararg components : View){
        createAlertDialog(components).show()
    }

    fun getAlertDialog (vararg components : View) : AlertDialog{
        return createAlertDialog(components)
    }

    private fun createAlertDialog(components : Array<out View>) : AlertDialog{
        val alert = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val mLinearLayout = LinearLayout(context)

        mLinearLayout.orientation = LinearLayout.VERTICAL
        mLinearLayout.gravity = Gravity.CENTER_HORIZONTAL
        mLinearLayout.layoutParams = paramsCreator.linearLayoutLayoutParamsCreator(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 15, 15, 30, null
        )

        for (i in components){
            mLinearLayout.addView(i)
        }

        alert.setView(mLinearLayout)

        val dialog = alert.create()
        dialog.window!!.setBackgroundDrawableResource(R.drawable.background_login_button_green);

        return dialog
    }


    fun createAlertWithView(view: Int){

    }

}