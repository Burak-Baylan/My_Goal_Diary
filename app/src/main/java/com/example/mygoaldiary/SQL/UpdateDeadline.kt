package com.example.mygoaldiary.SQL

import android.app.Activity
import android.content.Context
import com.example.mygoaldiary.Views.Details

class UpdateDeadline (private val mContext : Context, private val mActivity : Activity) : UpdateLastInteraction(){
    fun update(date : String){
        sqlManage = ManageSQL(mContext, mActivity)
        sqlManage.manager(mSql, "UPDATE allUserProjectDeneme3 SET targetedDeadline = '$date' WHERE id = ${Details.projectId}")
        super.mUpdate()
    }
}