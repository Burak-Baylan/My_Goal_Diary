package com.example.mygoaldiary.Creators

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.TextView
import com.example.mygoaldiary.R
import com.google.firebase.Timestamp
import java.util.*

class MyDatePickerDialog {
    private lateinit var mContext : Context
    private lateinit var mActivity : Activity
    private lateinit var mTextView : TextView

    companion object {
        private val mDatePicker = MyDatePickerDialog()
        lateinit var putHere : TextView
        private lateinit var mDialog : AlertDialog

        @SuppressLint("InflateParams")
        fun createDatePicker(context: Context, activity: Activity, runThisFunc: (() -> Unit)?) {
            mDatePicker.mContext = context
            mDatePicker.mActivity = activity
            mDatePicker.mTextView = putHere
            mDatePicker.create(runThisFunc)
        }

        var targetedTimeStamp = Timestamp.now()

        fun show() = mDialog.show()
    }

    @SuppressLint("SetTextI18n")
    fun create (runThisFunc: (() -> Unit)?){

        val view = mActivity.layoutInflater.inflate(R.layout.layout_date_picker, null)

        val mDatePicker = view.findViewById<DatePicker>(R.id.datePicker)
        mDatePicker.minDate = System.currentTimeMillis() - 1000 // Set mDatePicker minimum date to current date

        val mAlertDialog = AlertDialog.Builder(mContext)
        mAlertDialog.setView(view)

        view.findViewById<TextView>(R.id.datePickerSaveButton).setOnClickListener {
            pickDate(mDatePicker)
            mDialog.dismiss()
            runThisFunc?.let {
                it()
            }
        }

        view.findViewById<TextView>(R.id.datePickerCancelButton).setOnClickListener {
            mDialog.cancel()
        }

        mDialog = mAlertDialog.create()
        mDialog.window!!.setBackgroundDrawableResource(R.drawable.backgorund_alert_dialog)
    }

    private val combinedCal: Calendar = GregorianCalendar(TimeZone.getTimeZone("GMT"))
    private fun pickDate(mDatePicker: DatePicker) {
        with(mDatePicker){
            combinedCal.set(this.year, this.month, this.dayOfMonth)
        }

        val timeStamp = Timestamp(Date(combinedCal.timeInMillis))
        targetedTimeStamp = timeStamp

        println("İlk: ${timeStamp.toDate().toLocaleString()}")
        println("İki: ${Timestamp.now().toDate()}")
        println("İlk: $timeStamp")
        println("İki: ${Timestamp.now()}")

        val date = timeStamp.toDate()
        mTextView.text = "${date.year + 1900}/${put0(date.month)}/${put0(date.date)}" //"$day/${month+1}/$year"
    }

    private fun put0(value : Int) : String{
        return if (value in 1..9)
            "0$value"
        else
            value.toString()
    }
}