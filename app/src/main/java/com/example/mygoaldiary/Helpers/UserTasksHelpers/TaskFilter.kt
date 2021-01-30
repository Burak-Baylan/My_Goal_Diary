package com.example.mygoaldiary.Helpers.UserTasksHelpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.CheckBox
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
        //getFilterView()
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

        val currentFilter = sharedPref.getInt("taskFilter", TaskFilterVariables.GET_BOTH.value)

        when (currentFilter) {
            TaskFilterVariables.GET_JUST_SQL.value -> {
                //falseAllCheckBox(justSqlRb, justCloudRb, seeBothRb)
                justSqlRb.isChecked = true
            }
            TaskFilterVariables.GET_JUST_CLOUD.value -> {
                //falseAllCheckBox(justSqlRb, justCloudRb, seeBothRb)
                justCloudRb.isChecked = true
            }
            TaskFilterVariables.GET_BOTH.value -> {
                //falseAllCheckBox(justSqlRb, justCloudRb, seeBothRb)
                seeBothRb.isChecked = true
            }
        }

        justSqlRb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                saveFilter(TaskFilterVariables.GET_JUST_SQL.value)
            }
        }

        justCloudRb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                saveFilter(TaskFilterVariables.GET_JUST_CLOUD.value)
            }
        }

        seeBothRb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                saveFilter(TaskFilterVariables.GET_BOTH.value)
            }
        }

        alertDialog = getAlertDialog(context, activity)
        alertDialog.show()
    }

    private fun falseAllCheckBox(vararg radioButtons : RadioButton){
        for (i in radioButtons){
            i.isChecked = false
        }
    }

    private fun saveFilter(saveValue : Int){
        sharedPref.edit().putInt("taskFilter", saveValue).apply()
        GetTasks(mActivity).get()
        alertDialog.cancel()
    }

    private fun createShared(){
        sharedPref = mActivity.getSharedPreferences("com.example.mygoaldiary.Helpers.UserTasksHelpers", Context.MODE_PRIVATE)
    }

}