package com.example.mygoaldiary.Helpers.UserTasksHelpers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.CheckBox
import com.example.mygoaldiary.Creators.DeleteAlertDialog
import com.example.mygoaldiary.Views.Details
import com.example.mygoaldiary.Views.HomeMenuFragments.UserProjects.UserProjects
import com.example.mygoaldiary.Helpers.InternetController
import com.example.mygoaldiary.R

class DeleteTask (private val mContext:  Context, private val mActivity : Activity): UserProjects(){

    private lateinit var alertDialogHere: AlertDialog

    fun delete(deleteProjectView: View, taskUuid : String?, isHybrid : String){

        loadingDialog.startLoadingDialog()
        alertDialogHere = DeleteAlertDialog.alertDialog
        val isChecked = deleteProjectView.findViewById<CheckBox>(R.id.deleteInternetTooCheckBox).isChecked

        if (isChecked) {

            if (currentUser == null){
                loadingDialog.dismissDialog()
                showAlert.errorAlert("Error", "You must be logged in to delete a task from the cloud.", true)
                return
            }

            if (isHybrid == "true") {
                if (InternetController.internetControl(mActivity)) {
                    deleteFromFirebase(taskUuid)
                }else{
                    showAlert.errorAlert(mContext.getString(R.string.error), mContext.getString(R.string.internetRequiredToDeleteTask), true)
                    loadingDialog.dismissDialog()
                }
            }else{
                deleteFromSQL(taskUuid)
            }
        } else{
            deleteFromSQL(taskUuid)
        }
    }

    private fun deleteFromFirebase(taskUuid: String?) {
        firebaseSuperClass.userAuthManage().getCurrentUser()?.let {
            firebase.collection("Users").document(it.uid).collection("Projects").document(Details.projectUuid).collection("Tasks").document("$taskUuid").delete().addOnSuccessListener {
                deleteSuccess(taskUuid)
            }.addOnFailureListener {
                deleteFail()
            }
        }
    }

    private fun deleteSuccess(taskUuid: String?){
        deleteFromSQL(taskUuid)
    }

    private fun deleteFail(){
        alertDialogHere.cancel()
        loadingDialog.dismissDialog()
    }

    private fun deleteFromSQL(taskUuid: String?) {
        val get = sqlManage.manager(mSql, "DELETE FROM '${Details.projectUuid}' WHERE  taskUuid = '$taskUuid'")
        if (get){
            alertDialogHere.cancel()
            refreshAllViewsFromTasksLayout(mContext, mActivity)
        }else{
            alertDialogHere.cancel()
            showAlert.errorAlert(mContext.getString(R.string.error), mContext.getString(R.string.deleteTaskFail), true)
        }
        loadingDialog.dismissDialog()
    }
}