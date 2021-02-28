package com.example.mygoaldiary.Helpers.UserTasksHelpers

import android.app.Activity
import android.content.Context
import com.example.mygoaldiary.Views.HomeMenuFragments.UserProjects.UserProjects

open class TasksHelper : UserProjects(){

    companion object {
       lateinit var ctx: Context
       lateinit var act: Activity

        fun addTask(): AddTask {
            return AddTask()
        }

    }
}