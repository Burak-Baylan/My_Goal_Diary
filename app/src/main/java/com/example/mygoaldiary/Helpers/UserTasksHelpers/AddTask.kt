package com.example.mygoaldiary.Helpers.UserTasksHelpers

import com.example.mygoaldiary.Details
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.Helpers.MyHelpers
import com.example.mygoaldiary.R
import com.google.firebase.firestore.FirebaseFirestore
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil

class AddTask  : TasksHelper() {

    private val date = GetCurrentDate.getDate()
    private val time = GetCurrentDate.getTime()
    private val taskUuidString = MyHelpers.getUuid()
    private lateinit var mTitle : String

    private val projectUuid = Details.projectUuid

    private var isHybridTask =
            if (binding.saveTaskInternetTooCheckBox.isChecked) "true"
            else "false"

    fun add(title : String) {
        loadingDialog.startLoadingDialog()
        firebase = FirebaseFirestore.getInstance()

        mTitle = title

        if (isHybridTask == "true"){
            if (MyHelpers.internetControl(mActivity)){// Save
                putInFirebase()
            }else{// Don't save
                showAlert.errorAlert(mContext.getString(R.string.error), mContext.getString(R.string.internetRequiredToCloudSave), true)
                loadingDialog.dismissDialog()
            }
        }else{
            saveTaskToSql(taskUuidString)
        }
    }

    private fun putInFirebase() {
        val currentUser = firebaseSuperClass.userAuthManage().getCurrentUser()
        val userId = currentUser!!.uid

        val putController = hashMapOf(
                "task" to "task"
        )

        val putMap = hashMapOf(
                "title" to mTitle,
                "isDone" to "false",
                "isHybridTask" to isHybridTask,
                "yearDate" to date,
                "time" to time,
                "taskUuid" to taskUuidString
        )

        val documentReference = firebase.collection("Users").document(userId).collection("Projects").document(projectUuid)

        documentReference.set(putController).addOnSuccessListener {
            documentReference.collection("Tasks").document(taskUuidString).set(putMap).addOnSuccessListener {
                saveTaskToSql(taskUuidString)
            }.addOnFailureListener {
                documentReference.delete().addOnSuccessListener {
                    showAlert.errorAlert(mContext.getString(R.string.error), mContext.getString(R.string.addTaskFail), true)
                    loadingDialog.dismissDialog()
                }
            }
        }.addOnFailureListener {
            /** Hata **/
            showAlert.errorAlert(getString(R.string.error), it.localizedMessage!!, true)
            loadingDialog.dismissDialog()
        }
    }

    private fun saveTaskToSql(taskUuidString : String){
        val getReason = sqlManage.adder(mSql, "'${Details.projectId}'", "taskUuid, title, isDone, isHybridTask, yearDate, time", "'${taskUuidString}', '$mTitle', 'false', '$isHybridTask', '$date', '$time'")
        if (getReason) {
            refreshAllViewsFromTasksLayout(mContext, mActivity)
            binding.newTaskEditText.text.clear()
            UIUtil.hideKeyboard(mActivity)
        }else{
            showAlert.errorAlert("Error", "The task couldn't be added. Please try again.", true)
        }
        loadingDialog.dismissDialog()
    }
}