package com.example.mygoaldiary.Creators

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.mygoaldiary.R

class ShowAlert (private val context : Context){

    private var mCancelable = false
    companion object {
        lateinit var mAlertDialog: AlertDialog
    }

    /**********************************************************************************************/
    fun infoAlert(title : String, message : String, cancelable : Boolean){
        this.mCancelable = cancelable
        val view = alertCreator(R.layout.alert_info_layout)
        view.findViewById<TextView>(R.id.infoTitleText).setText(title)
        view.findViewById<TextView>(R.id.infoMessageText).setText(message)
        view.findViewById<Button>(R.id.infoOkayButton).setOnClickListener {
            mAlertDialog.dismiss()
        }
        mAlertDialog.show()
    }
    /**********************************************************************************************/

    fun successAlert (title : String, message : String, cancelable : Boolean) : Button{
        this.mCancelable = cancelable
        val view = alertCreator(R.layout.alert_success_layout)
        val successOkayButton = view.findViewById<Button>(R.id.successOkayButton)
        view.findViewById<TextView>(R.id.successTitleText).setText(title)
        view.findViewById<TextView>(R.id.successMessageText).setText(message)
        successOkayButton.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mAlertDialog.show()
        return successOkayButton
    }

    fun warningAlert (title : String, message : String, cancelable : Boolean) : Button{
        this.mCancelable = cancelable
        val view = alertCreator(R.layout.alert_warning_layout)
        view.findViewById<TextView>(R.id.warningTitleText).setText(title)
        view.findViewById<TextView>(R.id.warningMessageText).setText(message)
        view.findViewById<Button>(R.id.warningNoButton).setOnClickListener {
            mAlertDialog.dismiss()
        }
        mAlertDialog.show()
        return view.findViewById(R.id.warningYesButton)
    }

    /**********************************************************************************************/
    fun errorAlert (title : String, message : String, cancelable : Boolean){
        this.mCancelable = cancelable
        val view = alertCreator(R.layout.alert_error_layout)
        view.findViewById<TextView>(R.id.errorTitleText).setText(title)
        view.findViewById<TextView>(R.id.errorMessageText).setText(message)
        view.findViewById<Button>(R.id.errorOkayButton).setOnClickListener {
            mAlertDialog.dismiss()
        }
        mAlertDialog.show()
    }
    /**********************************************************************************************/

    private fun alertCreator(alertLayout : Int) : View {
        val builder : AlertDialog.Builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val view = LayoutInflater.from(context).inflate(alertLayout, null)
        builder.setView(view)
        builder.setCancelable(mCancelable)
        val alertDialog = builder.create()
        if (alertDialog.window != null){
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        mAlertDialog = alertDialog
        return view
    }
}