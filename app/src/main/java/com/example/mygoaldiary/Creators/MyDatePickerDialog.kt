package com.example.mygoaldiary.Creators

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.DatePicker
import android.widget.TextView
import com.example.mygoaldiary.R

class MyDatePickerDialog {

    private lateinit var mContext : Context
    private lateinit var mActivity : Activity
    private lateinit var mTextView : TextView

    companion object {
        private val mDatePicker = MyDatePickerDialog()
        lateinit var putHere : TextView
        private lateinit var mDialog : AlertDialog

        @SuppressLint("InflateParams")
        fun createDatePicker(context : Context, activity : Activity) {
            mDatePicker.mContext = context
            mDatePicker.mActivity = activity
            mDatePicker.mTextView = putHere
            mDatePicker.create()
        }

        fun show(){
            mDialog.show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun create (){

        val view = mActivity.layoutInflater.inflate(R.layout.layout_date_picker, null)

        val mDatePicker = view.findViewById<DatePicker>(R.id.datePicker)

        val mAlertDialog = AlertDialog.Builder(mContext)
        mAlertDialog.setView(view)

        view.findViewById<TextView>(R.id.datePickerSaveButton).setOnClickListener {
            val day = mDatePicker.dayOfMonth
            val month = mDatePicker.month
            val year = mDatePicker.year

            mTextView.text = "$day/${month+1}/$year"

            mDialog.dismiss()
        }

        view.findViewById<TextView>(R.id.datePickerCancelButton).setOnClickListener {
            mDialog.cancel()
        }

        mDialog = mAlertDialog.create()
        mDialog.window!!.setBackgroundDrawableResource(R.drawable.backgorund_alert_dialog)
    }
}