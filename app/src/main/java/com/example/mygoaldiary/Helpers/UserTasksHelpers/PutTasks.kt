package com.example.mygoaldiary.Helpers.UserTasksHelpers

import android.annotation.SuppressLint
import android.view.View
import android.widget.*
import com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments.UserProjects
import com.example.mygoaldiary.Models.TaskModel
import com.example.mygoaldiary.R

class PutTasks : UserProjects(){

    private val checkBoxArray = mutableListOf<Boolean>()

    @SuppressLint("InflateParams")
    fun putViews(mItems : MutableList<TaskModel>) {
        for ((position, taskModel) in mItems.withIndex()){
            val viewHere = mInflater.inflate(R.layout.layout_for_project_tasks, null)
            val taskLayout = viewHere.findViewById<LinearLayout>(R.id.taskLayout)
            val taskNameTv = viewHere.findViewById<TextView>(R.id.taskNameText)
            val myCb = viewHere.findViewById<CheckBox>(R.id.isTaskDoneCb)
            taskNameTv.text = taskModel.title

            taskLayout.setOnLongClickListener {
                taskLongClickListener(taskModel)
                true
            }

            taskLayout.setOnClickListener {
                val get = taskOverOrNot(checkBoxArray[position], taskNameTv, taskModel)
                myCb.isChecked = get
                checkBoxArray[position] = get
            }

            val cbBool = false
            checkBoxArray.add(cbBool)

            myCb.setOnClickListener { checkBoxArray[position] = taskOverOrNot(checkBoxArray[position], taskNameTv, taskModel) }
            totalTasks++

            doneController(taskModel, position, myCb, taskNameTv)

            viewHere.findViewById<ImageView>(R.id.cloudImage).visibility = if (taskModel.isHybrid == "true"){
                View.VISIBLE
            }else{
                View.INVISIBLE
            }

            layout.addView(viewHere)
        }
    }

    private fun doneController(taskModel: TaskModel, position: Int, myCb : CheckBox, taskNameTv : TextView){
        if (taskModel.isDone == "true"){
            myCb.isChecked = true
            checkBoxArray[position] = true
            taskNameTvCustomizer("true", taskNameTv)
            tasksDone++
        }else if (taskModel.isDone == "false"){
            myCb.isChecked = false
            checkBoxArray[position] = false
            taskNameTvCustomizer("false", taskNameTv)
        }
    }
}