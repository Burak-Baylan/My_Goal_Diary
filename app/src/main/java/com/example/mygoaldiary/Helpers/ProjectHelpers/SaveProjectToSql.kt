package com.example.mygoaldiary.Helpers.ProjectHelpers

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.mygoaldiary.ConstantValues
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.Views.HomeMenuFragments.AddProject
import java.lang.Exception

class SaveProjectToSql (private var context : Context, private var activity : Activity) {

    private var mSql : SQLiteDatabase? = null
    private var showAlert = ShowAlert(context)

    var projectName = ""
    var saveInternetToo = false
    var currentDate = ""
    var deadline = ""

    var finishActivityWhenSaveProject = true

    var projectColor : String? = null

    init {
        val sqlManage = ManageSQL(context, activity)
        mSql = sqlManage.createSqlVariable("HomePage")
    }

    fun save(yearDateStfString: String, timeDateStfString: String, projectUuid : String){
        val projectColorHere : Int = if (projectColor != null){
            projectColor!!.toInt()
        }else{
            AddProject.selectedColor
        }
        try {
            mSql?.execSQL("INSERT INTO allUserProjectDeneme3 (${ConstantValues.PROJECT_VARIABLES_NAME_STRING}) VALUES ('$projectUuid', '$projectName', $projectColorHere, '$yearDateStfString', '$timeDateStfString', '$saveInternetToo', '$currentDate ${GetCurrentDate.getTime()}', '$deadline')")
            if (finishActivityWhenSaveProject)
                activity.finish()
        }catch (e : Exception){
            showAlert.errorAlert(activity.getString(R.string.error), activity.getString(R.string.errorOccurred), true)
        }
    }
}