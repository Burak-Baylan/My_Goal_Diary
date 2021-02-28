package com.example.mygoaldiary.Helpers.UserTasksHelpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.RadioButton
import com.example.mygoaldiary.R

class TaskFilter {

    companion object {
        private lateinit var mContext: Context
        private lateinit var mActivity: Activity
        private lateinit var filterView: View
        private lateinit var sharedPref : SharedPreferences
    }

    private lateinit var alertDialog : AlertDialog

    private fun getAlertDialog (context : Context, activity : Activity): AlertDialog {
        mContext = context
        mActivity = activity
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
        filterView = mActivity.layoutInflater.inflate(R.layout.layout_task_filter_selector, null)
        return filterView
    }

    fun selectFilter(context : Context, activity : Activity){
        mActivity = activity

        createShared()

        val filterView = getFilterView()
        val justSqlRb = filterView.findViewById<RadioButton>(R.id.justSeeSql)
        val justCloudRb = filterView.findViewById<RadioButton>(R.id.justSeeCloud)
        val seeBothRb = filterView.findViewById<RadioButton>(R.id.seeBoth)

        when (sharedPref.getInt("taskFilter", TaskFilterVariables.GET_BOTH.value)) {
            TaskFilterVariables.GET_JUST_SQL.value -> justSqlRb.isChecked = true
            TaskFilterVariables.GET_JUST_CLOUD.value -> justCloudRb.isChecked = true
            TaskFilterVariables.GET_BOTH.value -> seeBothRb.isChecked = true
        }

        justSqlRb.setOnCheckedChangeListener { _, isChecked ->
            selectFilter(isChecked, TaskFilterVariables.GET_JUST_SQL.value)
        }
        justCloudRb.setOnCheckedChangeListener { _, isChecked ->
            selectFilter(isChecked, TaskFilterVariables.GET_JUST_CLOUD.value)
        }
        seeBothRb.setOnCheckedChangeListener { _, isChecked ->
            selectFilter(isChecked, TaskFilterVariables.GET_BOTH.value)
        }

        alertDialog = getAlertDialog(context, activity)
        alertDialog.show()
    }

    private fun selectFilter (isChecked : Boolean, value : Int){
        if (isChecked){
            saveFilter(value)
        }
    }

    private fun saveFilter(saveValue : Int){
        sharedPref.edit().putInt("taskFilter", saveValue).apply()
        GetTasks(mActivity).getAndPut()
        alertDialog.cancel()
    }

    private fun createShared() {
        sharedPref = mActivity.getSharedPreferences("com.example.mygoaldiary.Helpers.UserTasksHelpers", Context.MODE_PRIVATE)
    }
}