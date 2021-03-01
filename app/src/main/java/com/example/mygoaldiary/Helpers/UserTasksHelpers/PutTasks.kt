package com.example.mygoaldiary.Helpers.UserTasksHelpers

import android.annotation.SuppressLint
import android.view.View
import android.widget.*
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.Views.HomeMenuFragments.UserProjects.UserProjects
import com.example.mygoaldiary.Models.TaskModel
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.UpdateLastInteraction
import com.example.mygoaldiary.Views.Details

class PutTasks : UserProjects(){

    private val checkBoxArray = mutableListOf<Boolean>()

    @SuppressLint("InflateParams")
    fun putViews(mItems : MutableList<TaskModel>) {
        layout.removeAllViews()
        for ((position, taskModel) in mItems.withIndex()){
            val viewHere = mInflater.inflate(R.layout.user_tasks_row, null)
            val taskLayout = viewHere.findViewById<LinearLayout>(R.id.taskLayout)
            val taskNameTv = viewHere.findViewById<TextView>(R.id.taskNameText)
            val myCb = viewHere.findViewById<CheckBox>(R.id.isTaskDoneCb)
            taskNameTv.text = taskModel.title

            taskLayout.setOnLongClickListener {
                taskLongClickListener(taskModel)
                true
            }

            taskLayout.setOnClickListener {
                val get = taskOverOrNot2(checkBoxArray[position], taskNameTv, taskModel)
                myCb.isChecked = get
                checkBoxArray[position] = get
            }

            val cbBool = false
            checkBoxArray.add(cbBool)

            myCb.setOnClickListener { checkBoxArray[position] = taskOverOrNot2(checkBoxArray[position], taskNameTv, taskModel) }
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

    private lateinit var currentDateAndTime : String
    private fun taskOverOrNot2(isChecked : Boolean, textView : TextView, taskModel : TaskModel) : Boolean{

        val isHybrid = taskModel.isHybrid

        fun save () : Boolean{
            val returnThis = if (!isChecked){
                tasksDone++
                taskNameTvCustomizer("true", textView)
                sqlManage.manager(mSql, "UPDATE '${Details.projectUuid}' SET isDone = 'true' WHERE id = ${taskModel.id}")
                true
            }else{
                tasksDone--
                sqlManage.manager(mSql, "UPDATE '${Details.projectUuid}' SET isDone = 'false' WHERE id = ${taskModel.id}")
                taskNameTvCustomizer("false", textView)
                false
            }
            UpdateLastInteraction.update()
            return returnThis
        }

        val returnBool : Boolean

        if (isHybrid == "true"){
            returnBool = save()
            showAlert.warningAlert("Update from cloud", "Do you want to update from the cloud at the same time?", false).apply {
                this.setOnClickListener {
                    ShowAlert.mAlertDialog.dismiss()
                    updateFromFb(taskModel, returnBool)
                }
            }
        }else{
            returnBool = save()
        }

        binding.tasksDone.text = "$tasksDone/$totalTasks"
        //binding.showLastInteractionDateTv.text = GetCurrentDate.getDateAndTime()
        return returnBool
    }

    private fun updateFromFb(taskModel: TaskModel, returnBool: Boolean) {
        if (currentUser != null) {
            loadingDialog.startLoadingDialog()
            firebase.collection("Users")
                    .document(currentUser.uid)
                    .collection("Projects")
                    .document(Details.projectUuid)
                    .collection("Tasks")
                    .document(taskModel.taskUuid)
                    .update("isDone", "$returnBool")
                    .addOnSuccessListener {
                        showAlert.successAlert("Success", "Update success.", true)
                        loadingDialog.dismissDialog()
                    }.addOnFailureListener {
                        showAlert.errorAlert("Error", "Update fail.", true)
                        loadingDialog.dismissDialog()
                    }
        }else{
            showAlert.infoAlert("Please", "Please", true)
        }
    }
}