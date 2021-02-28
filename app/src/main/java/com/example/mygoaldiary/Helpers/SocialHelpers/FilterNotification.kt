package com.example.mygoaldiary.Helpers.SocialHelpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.RadioButton
import com.example.mygoaldiary.Helpers.UserTasksHelpers.GetTasks
import com.example.mygoaldiary.Helpers.UserTasksHelpers.TaskFilterVariables
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.R
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class FilterNotification {

    companion object {
        private lateinit var mContext: Context
        private lateinit var mActivity: Activity
        private lateinit var filterView: View
    }

    private lateinit var postId : String

    private lateinit var alertDialog : AlertDialog

    private fun getAlertDialog (): AlertDialog {
        val builder = createBuilder()
        alertDialog = builder.create()
        if (alertDialog.window != null){ // Transparent
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        return alertDialog
    }

    private fun createBuilder () : AlertDialog.Builder {
        val builder : AlertDialog.Builder = AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
        builder.setView(filterView)
        builder.setCancelable(true)
        return builder
    }

    @SuppressLint("InflateParams")
    fun getFilterView() : View {
        filterView = mActivity.layoutInflater.inflate(R.layout.layout_notifications_filter_selector, null)
        return filterView
    }

    private lateinit var openNotificationsRb : RadioButton
    private lateinit var closeNotificationsRb : RadioButton
    private lateinit var loadingDialog : LoadingDialog

    fun selectFilter(context : Context, activity : Activity, postId : String){

        this.postId = postId

        mActivity = activity
        mContext = context
        loadingDialog = LoadingDialog(mActivity)

        loadingDialog.startLoadingDialog()

        val filterView = getFilterView()
        openNotificationsRb = filterView.findViewById(R.id.openNotifications)
        closeNotificationsRb = filterView.findViewById(R.id.closeNotifications)

        getCurrentStatus()

    }

    private fun getCurrentStatus() {
        firebase.collection("Posts").document(postId).get().addOnSuccessListener {
            if (it.exists() && it != null){
                when(it["pushNotify"] as Boolean){
                    true -> openNotificationsRb.isChecked = true
                    false -> closeNotificationsRb.isChecked = true
                }
                selector()
            }
        }.addOnFailureListener {
            loadingDialog.dismissDialog()
        }
    }

    private fun selector (){
        openNotificationsRb.setOnCheckedChangeListener { _, isChecked ->
            selectFilter(isChecked, true)
        }
        closeNotificationsRb.setOnCheckedChangeListener { _, isChecked ->
            selectFilter(isChecked, false)
        }

        loadingDialog.dismissDialog()

        alertDialog = getAlertDialog()
        alertDialog.show()
    }

    private fun selectFilter (isChecked : Boolean, value : Boolean){
        if (isChecked){
            saveFilter(value)
        }
    }

    private fun saveFilter(saveValue : Boolean){
        firebase.collection("Posts").document(postId).update("pushNotify", saveValue).addOnSuccessListener {
            println("FilterNotification saved")
        }.addOnFailureListener {
            println("FilterNotification: ${it.localizedMessage!!}")
        }
    }

    private var firebase = FirebaseFirestore.getInstance()
}