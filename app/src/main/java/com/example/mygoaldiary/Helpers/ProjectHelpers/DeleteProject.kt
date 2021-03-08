package com.example.mygoaldiary.Helpers.ProjectHelpers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.widget.CheckBox
import com.example.mygoaldiary.ConstantValues
import com.example.mygoaldiary.Creators.DeleteAlertDialog
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass
import com.example.mygoaldiary.Views.BottomNavFragments.Home
import com.example.mygoaldiary.Helpers.InternetController
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import com.google.firebase.firestore.FirebaseFirestore

class DeleteProject(private val mContext: Context, private val mActivity : Activity) : Home(){

    private lateinit var alertDialogHere: AlertDialog
    private val showAlert = ShowAlert(mContext)
    private val loadingDialog = LoadingDialog(mActivity)
    private val firebaseSuperClass = FirebaseSuperClass(mContext, mActivity)
    private val firebase = FirebaseFirestore.getInstance()
    private val sqlManage = ManageSQL(mContext, mActivity)
    var mSql: SQLiteDatabase? = null

    fun delete(deleteProjectView: View, projectUuid : String, isHybrid : String){
        mSql = sqlManage.createSqlVariable("HomePage")
        loadingDialog.startLoadingDialog()
        alertDialogHere = DeleteAlertDialog.alertDialog
        val isChecked = deleteProjectView.findViewById<CheckBox>(R.id.deleteInternetTooCheckBox).isChecked

        if (isChecked) {
            if (isHybrid == "true") {
                if (InternetController.internetControl(mActivity)) {
                    deleteFromFirebase(projectUuid)
                }else{
                    showAlert.errorAlert(mContext.getString(R.string.error), mContext.getString(R.string.internetRequiredToDeleteTask), true)
                    loadingDialog.dismissDialog()
                }
            }else{
                deleteFromSQL(projectUuid)
            }
        } else{
            deleteFromSQL(projectUuid)
        }
    }

    private fun deleteFromFirebase(projectUuid: String) {
        firebaseSuperClass.userAuthManage().getCurrentUser()?.let {
            firebase.collection("Users").document(it.uid).collection("Projects").document(projectUuid).delete().addOnSuccessListener {
                deleteSuccess(projectUuid)
            }.addOnFailureListener {
                deleteFail()
            }
        }
    }

    private fun deleteSuccess(projectUuid: String?){
        deleteFromSQL(projectUuid)
    }

    private fun deleteFail(){
        alertDialogHere.cancel()
        loadingDialog.dismissDialog()
    }

    private fun deleteFromSQL(projectUuid: String?) {
        sqlManage.tableCreator(mSql, "'$projectUuid'", ConstantValues.PROJECT_VARIABLES_STRING)
        val get = sqlManage.manager(mSql, "DELETE FROM allUserProjectDeneme3 WHERE  projectUuid = '$projectUuid'")
        val get2 = sqlManage.manager(mSql, "DELETE FROM '$projectUuid'")
        if (get && get2){
            alertDialogHere.cancel()
            refresh()
        }else{
            alertDialogHere.cancel()
            showAlert.errorAlert(mContext.getString(R.string.error), mContext.getString(R.string.deleteTaskFail), true)
        }
        loadingDialog.dismissDialog()
    }
}