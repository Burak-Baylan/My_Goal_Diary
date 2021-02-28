package com.example.mygoaldiary.Helpers.ProjectHelpers

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoaldiary.ConstantValues
import com.example.mygoaldiary.Models.ModelHome
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.Views.BottomNavFragments.Home

abstract class GetProjects : Home(){

    companion object {
        @SuppressLint("Recycle")
        fun getProjects(sqlManage: ManageSQL, items: ArrayList<ModelHome>, recyclerView: RecyclerView) {

            recyclerView.removeAllViews()
            items.clear()

            //items.add(ModelHome("Reports", "Reports", R.drawable.ic_notes_for_reports, "", "", "#000000", Typeface.NORMAL, 50, null, null, null, null))
            //items.add(ModelHome("Diary", "Diary", R.drawable.ic_diary, "", "", "#000000", Typeface.NORMAL, 40, null, null, null, null))

            val mSql = sqlManage.createSqlVariable("HomePage").apply {
                sqlManage.tableCreator(this, "allUserProjectDeneme3", ConstantValues.PROJECT_VARIABLES_STRING)
            }

            try {
                val cursor = mSql?.rawQuery("SELECT * FROM allUserProjectDeneme3", null)
                while (cursor!!.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndex("id"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val projectColor = cursor.getInt(cursor.getColumnIndex("projectColor"))
                    val yearDate = cursor.getString(cursor.getColumnIndex("yearDate"))
                    val time = cursor.getString(cursor.getColumnIndex("time"))
                    val projectUuid = cursor.getString(cursor.getColumnIndex("projectUuid"))
                    val isHybrid = cursor.getString(cursor.getColumnIndex("isHybrid"))
                    val lastInteraction = cursor.getString(cursor.getColumnIndex("lastInteraction"))
                    val targetedDeadline = cursor.getString(cursor.getColumnIndex("targetedDeadline"))
                    items.add(
                            ModelHome(id, title, projectColor, yearDate, time, "#000000", Typeface.NORMAL, 50, isHybrid, projectUuid, lastInteraction, targetedDeadline)
                    )
                }
            } catch (e: Exception) {
                println("Home project get error: ${e.localizedMessage!!}")
            }
            items.add(ModelHome("Add Project", "Add Project", R.drawable.ic_add, "", "", "#F05454", Typeface.BOLD, 50, null, null, null, null))
        }
    }
}
