package com.example.mygoaldiary.Helpers.UserTasksHelpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.widget.LinearLayout
import com.example.mygoaldiary.Details
import com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments.UserProjects
import com.example.mygoaldiary.Models.TaskModel

open class GetTasks (private val mActivity : Activity): UserProjects(){

    private lateinit var sharedPref : SharedPreferences

    @SuppressLint("Recycle", "SetTextI18n")
    fun get() {
        createShared()
        mSql = sqlManage.createSqlVariable("HomePage").apply {
            // TASKS
            sqlManage.tableCreator(this, "'${Details.projectId}'", "id INTEGER PRIMARY KEY, taskUuid VARCHAR, title VARCHAR, isDone VARCHAR, isHybridTask VARCHAR, yearDate VARCHAR, time VARCHAR")
        }
        layout.orientation = LinearLayout.VERTICAL
        try {
            tasksDone = 0
            totalTasks = 0
            val items = putItemsFromModel()
            PutTasks().putViews(items)
            binding.tasksDone.text = "$tasksDone/$totalTasks"
            binding.tasksScrollView.removeAllViews()
            binding.tasksScrollView.addView(layout)
        }
        catch (e: Exception){
            println("New Error: ${e.localizedMessage}")
        }
    }

    private val mItems = mutableListOf<TaskModel>()

    @SuppressLint("Recycle")
    private fun putItemsFromModel(): MutableList<TaskModel> {
        val cursor = mSql?.rawQuery("SELECT * FROM '${Details.projectId}'", null)

        val filterHere = getCurrentFilter()

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
        }
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