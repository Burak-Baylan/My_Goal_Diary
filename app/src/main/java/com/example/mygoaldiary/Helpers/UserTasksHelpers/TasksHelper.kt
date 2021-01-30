package com.example.mygoaldiary.Helpers.UserTasksHelpers

import android.app.Activity
import android.content.Context
import com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments.UserProjects

open class TasksHelper : UserProjects(){

    companion object {
       lateinit var mContext: Context
       lateinit var mActivity: Activity

        fun addTask(): AddTask {
            return AddTask()
        }

    }
}