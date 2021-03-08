package com.example.mygoaldiary.SQL

import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.Views.Details
import com.example.mygoaldiary.Views.HomeMenuFragments.UserProjects.UserProjects
import java.lang.Exception

open class UpdateLastInteraction : UserProjects(){

    companion object{
        fun update(){
            UpdateLastInteraction().mUpdate()
        }
    }

    fun mUpdate(){
        sqlManage.manager(mSql, "UPDATE allUserProjectDeneme3 SET lastInteraction = '${GetCurrentDate.getDateAndTime()}' WHERE id = ${Details.projectId}")
        Details.lastInteraction = GetCurrentDate.getDateAndTime()
        textViewUpdater()
    }

    private fun textViewUpdater(){
        try {
            binding.showLastInteractionDateTv.text = GetCurrentDate.getDateAndTime()
        }catch (e : Exception){}
    }
}