package com.example.mygoaldiary.Creators

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.mygoaldiary.R

class DeleteAlertDialog {

    private lateinit var mContext : Context
    private lateinit var mActivity : Activity


    @SuppressLint("InflateParams")
    private fun getView() : View{
        return mActivity.layoutInflater.inflate(R.layout.layout_want_to_delete, null)
    }

    companion object{

        private var mDeleteAlert = DeleteAlertDialog()

        lateinit var view : View

        var cancelable = true
        lateinit var alertDialog : AlertDialog

        var titleText = "You Deleting a Project"
        var messageText = "If you delete this \"Ödevlerim\" project, you cannot get it back. Are you sure you want to delete?"

        fun create(context : Context, activity: Activity){
            mDeleteAlert.mContext = context
            mDeleteAlert.mActivity = activity
            view = mDeleteAlert.getView()
        }

        fun show(): View {
            mDeleteAlert.alertCreator().show()
            return view
        }

        lateinit var yesButton : Button
        lateinit var noButton : Button

    }

    private fun putTexts(){
        view.findViewById<TextView>(R.id.deleteWarningTitleText).text = titleText
        view.findViewById<TextView>(R.id.deleteWarningMessageText).text = messageText
    }

    private fun createBuilder() : AlertDialog.Builder {
        val builder : AlertDialog.Builder = AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
        builder.setView(view)
        builder.setCancelable(cancelable)
        return builder
    }

    private fun alertCreator(): AlertDialog {
        val builder = createBuilder()
        val alertDialogHere = builder.create()
        if (alertDialogHere.window != null){ // Transparent
            alertDialogHere.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog = alertDialogHere

        view.findViewById<Button>(R.id.deleteWarningNoButton).setOnClickListener {
            alertDialogHere.dismiss()
        }
        putTexts()
        return alertDialogHere
    }

}