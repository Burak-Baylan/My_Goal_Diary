package com.example.mygoaldiary.Helpers.UserTasksHelpers

import android.app.Activity
import android.content.Context
import android.view.View
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Customizers.TextCustomizer.Companion.setDefaultFlag
import com.example.mygoaldiary.Helpers.InternetController
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.Models.TaskModel
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.Views.Details
import com.example.mygoaldiary.Views.HomeMenuFragments.UserProjects.UserProjects
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MoveFromLocalToCloud (private val contextHere : Context, private val activity : Activity) : UserProjects(){
    private val auth = FirebaseAuth.getInstance()
    private val showAlert = ShowAlert(mContext)

    private val loadingDialog = LoadingDialog(mActivity)

    fun startMovingTasks() {

        if (!InternetController.internetControl(mActivity)){
            showAlert.errorAlert(activity.getString(R.string.error), activity.getString(R.string.projectMovingInternetError), true)
            return
        }

        loadingDialog.startLoadingDialog()
        if (currentUser != null) {
            moveProject()
        } else { // Show error message
            loadingDialog.dismissDialog()
            showAlert.errorAlert(getString(R.string.error), getString(R.string.youMustBeLoggedInToMoveProject), true)
        }
    }
    private fun createProjectHashData() : HashMap<String, Any> =
        hashMapOf(
                "userId" to currentUser!!.uid,
                "userName" to currentUser!!.displayName!!,
                "projectName" to Details.key,
                "projectColor" to Details.projectColor!!.toLong(),
                "yearDate" to Details.date!!,
                "timeDate" to Details.time!!,
                "projectId" to Details.projectUuid,
                "lastInteraction" to Details.lastInteraction!!,
                "deadline" to Timestamp.now()
        )

    private val firebase = FirebaseFirestore.getInstance()
    private val firebaseUserProjectReference = firebase.collection("Users").document(currentUser!!.uid).collection("Projects").document(Details.projectUuid)
    private fun moveProject() {
        firebaseUserProjectReference.set(createProjectHashData()).addOnSuccessListener {
            moveTasks()
        }.addOnFailureListener {
            showAlert.errorAlert("Başarısız1", "Aktarma Başarısız.1", true)
            loadingDialog.dismissDialog()
        }
    }

    var itemsSize = 0
    var counter = 0
    var manageSQL = ManageSQL(mContext, mActivity)
    private fun moveTasks(){
        val taskMovingFailArray = ArrayList<String>()
        GetTasks(mActivity).justGet()?.let {
            itemsSize = 0
            for (i in it) {// Get items model size
                itemsSize++
            }

            counter = 0
            if (it.isEmpty()){
                saveProjectFromSql()
                loadingDialog.dismissDialog()
            }
            for (model in it) {
                firebaseUserProjectReference.collection("Tasks").document(model.taskUuid).set(createTaskHashData(model)).addOnSuccessListener {
                    saveTaskFromSql(model)
                    counter++
                    if (counter == itemsSize){
                        ShowAlert(mContext).successAlert(mActivity.getString(R.string.moveSuccess), "${mActivity.getString(R.string.numberOfCouldntUploadedTask)}: ${taskMovingFailArray.size}", true)
                        saveProjectFromSql()
                        loadingDialog.dismissDialog()
                    }
                }.addOnFailureListener {
                    taskMovingFailArray.add(model.title)
                    loadingDialog.dismissDialog()
                }
            }
        }
    }

    private fun saveTaskFromSql(model : TaskModel) =
            manageSQL.manager(mSql, "UPDATE '${Details.projectUuid}' SET isHybridTask = 'true' WHERE id = ${model.id}")

    private fun saveProjectFromSql() {
        manageSQL.manager(mSql, "UPDATE allUserProjectDeneme3 SET isHybrid = 'true' WHERE id = ${Details.projectId}")
        refreshAllViewsFromTasksLayout(contextHere, activity)
        with(binding){
            this.saveTaskInternetTooCheckBox.setDefaultFlag()
            this.targetedDeadlineTv.setDefaultFlag()
            this.saveTaskInternetTooCheckBox.isEnabled = true
            this.showAndHideTargetedDeadline.isEnabled = true
            this.infoForCantUploadCloud.visibility = View.GONE
            this.getInfoForDeadline.visibility = View.GONE
        }
    }

    private fun createTaskHashData(model : TaskModel): HashMap<String, String> {
        return hashMapOf(
                "title" to model.title,
                "isDone" to model.isDone,
                "isHybridTask" to "true",
                "yearDate" to model.yearDate,
                "time" to model.time,
                "taskUuid" to model.taskUuid
        )
    }
}