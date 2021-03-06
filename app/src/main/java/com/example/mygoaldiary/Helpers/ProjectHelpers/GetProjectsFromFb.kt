package com.example.mygoaldiary.Helpers.ProjectHelpers

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.mygoaldiary.ConstantValues
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.RecyclerView.HomeRecyclerViewAdapter
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.Views.BottomNavFragments.Home
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class GetProjectsFromFb (private var context : Context, private var activity : Activity) {
    private var mSql : SQLiteDatabase? = null
    private var showAlert = ShowAlert(context)
    private var firebase = FirebaseFirestore.getInstance()
    private var currentUser = FirebaseAuth.getInstance().currentUser!!
    private var sqlManage : ManageSQL = ManageSQL(context, activity)
    private var saveProjectToSql : SaveProjectToSql = SaveProjectToSql(context, activity)
    private val loadingDialog = LoadingDialog(activity)

    init {
        mSql = sqlManage.createSqlVariable("HomePage")
    }

    private lateinit var deadline : Timestamp
    private lateinit var lastInteraction : String
    private var projectColor : Long = 0
    private lateinit var projectUuid : String
    private lateinit var projectName : String
    private lateinit var timeDate : String
    private lateinit var userId : String
    private lateinit var userName : String
    private lateinit var yearDate : String

    fun get(){
        loadingDialog.startLoadingDialog()
        deleteTasks()
        deleteAllProjectRecords()
        firebase.collection("Users").document(currentUser.uid).collection("Projects").get().addOnSuccessListener { projectIt ->
            if (projectIt != null && !projectIt.isEmpty){
                for ((i, docs) in projectIt.documents.withIndex()){
                    getProjectItems(docs)
                    saveProjectToSql(yearDate, timeDate, projectUuid, deadline, projectName)
                    getTasks(docs, projectUuid, yearDate)
                    if (i == projectIt.documents.size-1){
                        GetProjects.getProjects(sqlManage, HomeRecyclerViewAdapter.homeItems, Home.homeRecyclerView)
                        loadingDialog.dismissDialog()
                        showAlert.successAlert("Success", "Projects received.", true)
                    }
                }
            }else{
                /** KAYITLI PROJE BULUNAMADI **/
                loadingDialog.dismissDialog()
                showAlert.errorAlert("Error", "Couldn't found saved project", true)
            }
        }.addOnFailureListener {
            showAlert.errorAlert("Error", it.localizedMessage!!, true)
            loadingDialog.dismissDialog()
        }
    }

    private lateinit var isDone : String
    private lateinit var isHybridTask : String
    private lateinit var taskUuid : String
    private lateinit var taskTime : String
    private lateinit var taskTitle : String
    private lateinit var taskYearDate : String

    private fun getTasks(docs: DocumentSnapshot, projectUuid: String, yearDate: String) {
        docs.reference.collection("Tasks").get().addOnSuccessListener { taskIt ->
            if (taskIt != null){
                for (docs2 in taskIt.documents){
                    getTaskItems(docs2)
                    saveTaskToSql(taskUuid, projectUuid, yearDate, taskTitle, isHybridTask, taskTime)
                }
            }else{
                loadingDialog.dismissDialog()
            }
        }.addOnFailureListener {
            loadingDialog.dismissDialog()
        }
    }

    private fun saveProjectToSql(yearDate: String, timeDate: String, projectUuid: String, deadline : Timestamp, projectName : String) {
        val date = deadline.toDate()
        saveProjectToSql.currentDate = GetCurrentDate.getDate()
        saveProjectToSql.deadline = "${date.year + 1900}/${put0(date.month)}/${put0(date.date)}"
        saveProjectToSql.projectName = projectName
        saveProjectToSql.saveInternetToo = true
        saveProjectToSql.finishActivityWhenSaveProject = false
        saveProjectToSql.projectColor = projectColor.toString()
        saveProjectToSql.save(yearDate, timeDate, projectUuid)
    }
    private fun put0(value : Int) : String{
        return if (value in 1..9)
            "0$value"
        else
            value.toString()
    }

    private fun saveTaskToSql(taskUuidString : String, projectUuid : String, yearDate : String, taskTitle : String, isHybridTask : String, taskTime : String){
        val id = "'$projectUuid'"
        sqlManage.tableCreator(mSql, id, ConstantValues.TASK_VARIABLES_STRING)
        sqlManage.adder(mSql, id, ConstantValues.TASK_VARIABLES_NAME_STRING, "'${taskUuidString}', '$taskTitle', '$isDone', '$isHybridTask', '$yearDate', '$taskTime'")
    }

    private fun deleteAllProjectRecords(){
        mSql?.execSQL("DELETE FROM allUserProjectDeneme3")
    }

    private fun getTaskItems(docs: DocumentSnapshot) {
        isDone = docs["isDone"] as String
        isHybridTask = docs["isHybridTask"] as String
        taskUuid = docs["taskUuid"] as String
        taskTime = docs["time"] as String
        taskTitle = docs["title"] as String
        taskYearDate = docs["yearDate"] as String
    }

    private fun getProjectItems(docs: DocumentSnapshot) {
        deadline = docs["deadline"] as Timestamp
        lastInteraction = docs["lastInteraction"] as String
        projectColor = docs["projectColor"] as Long
        projectUuid = docs["projectId"] as String
        projectName = docs["projectName"] as String
        timeDate = docs["timeDate"] as String
        userId = docs["userId"] as String
        userName = docs["userName"] as String
        yearDate = docs["yearDate"] as String
    }

    private fun deleteTasks() {
        try {
            mSql = sqlManage.createSqlVariable("HomePage")
            val cursor = mSql?.rawQuery("SELECT * FROM allUserProjectDeneme3", null)
            while (cursor!!.moveToNext()) {
                val projectUuid = cursor.getString(cursor.getColumnIndex("projectUuid"))
                mSql?.execSQL("DELETE FROM '$projectUuid'")
            }
        } catch (e: Exception) {
            println("GetProjectsFromFB: ${e.localizedMessage!!}")
        }
    }
}