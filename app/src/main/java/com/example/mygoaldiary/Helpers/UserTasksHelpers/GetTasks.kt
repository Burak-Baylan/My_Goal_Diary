package com.example.mygoaldiary.Helpers.UserTasksHelpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.LinearLayout
import com.example.mygoaldiary.ConstantValues
import com.example.mygoaldiary.Views.Details
import com.example.mygoaldiary.Views.HomeMenuFragments.UserProjects.UserProjects
import com.example.mygoaldiary.Models.TaskModel

open class GetTasks (private val mActivity : Activity): UserProjects(){

    private lateinit var sharedPref : SharedPreferences

    @SuppressLint("Recycle", "SetTextI18n")
    private fun get() : MutableList<TaskModel>? {
        layout.orientation = LinearLayout.VERTICAL
        createShared()
        mSql = sqlManage.createSqlVariable("HomePage").apply {
            // TASKS
            sqlManage.tableCreator(this, "'${Details.projectUuid}'", ConstantValues.TASK_VARIABLES_STRING)
        }
        return try {
            putItemsFromModel()
        }
        catch (e: Exception){
            println("GetTasks 'putItems' error: ${e.localizedMessage}")
            null
        }
    }

    fun justGet(): MutableList<TaskModel>? {
        return get()
    }

    fun getAndPut(){
        tasksDone = 0
        totalTasks = 0
        get()?.let {
            PutTasks(mActivity).putViews(it)
            binding.tasksDone.text = "$tasksDone/$totalTasks"
            binding.tasksScrollView.removeAllViews()
            binding.tasksScrollView.addView(layout)
            return
        }
    }

    private val mItems = mutableListOf<TaskModel>()

    @SuppressLint("Recycle")
    private fun putItemsFromModel(): MutableList<TaskModel> {
        val cursor = mSql?.rawQuery("SELECT * FROM '${Details.projectUuid}'", null)

        val filterHere = getCurrentFilter()
        var hybridCounter = 0

        while (cursor!!.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex("id"))
            val taskUuid = cursor.getString(cursor.getColumnIndex("taskUuid"))
            val title = cursor.getString(cursor.getColumnIndex("title"))
            val isDone = cursor.getString(cursor.getColumnIndex("isDone"))
            val isHybrid = cursor.getString(cursor.getColumnIndex("isHybridTask"))
            val yearDate = cursor.getString(cursor.getColumnIndex("yearDate"))
            val time = cursor.getString(cursor.getColumnIndex("time"))

            if (filterHere == 0 && isHybrid == "false"){// JUST_SQL
                mItems.add(TaskModel(id, taskUuid, title, isDone, isHybrid, yearDate, time))
            }else if (filterHere == 1 && isHybrid == "true"){// JUST_CLOUD
                mItems.add(TaskModel(id, taskUuid, title, isDone, isHybrid, yearDate, time))
            }else if (filterHere == 2){// BOTH
                mItems.add(TaskModel(id, taskUuid, title, isDone, isHybrid, yearDate, time))
            }

            if (isHybrid == "false") hybridCounter++
        }
        if (currentUser != null) binding.uploadToCloudIc.visibility = View.VISIBLE
        else binding.uploadToCloudIc.visibility = View.GONE
        mItems.reverse()
        return mItems
    }

    private fun createShared(){
        sharedPref = mActivity.getSharedPreferences("com.example.mygoaldiary.Helpers.UserTasksHelpers", Context.MODE_PRIVATE)
    }

    private fun getCurrentFilter() : Int{
        return sharedPref.getInt("taskFilter", TaskFilterVariables.GET_BOTH.value)
    }
}