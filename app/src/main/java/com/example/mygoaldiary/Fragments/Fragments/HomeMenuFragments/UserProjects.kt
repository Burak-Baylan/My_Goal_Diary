package com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments

import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mygoaldiary.Creators.MyDatePickerDialog
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Details
import com.example.mygoaldiary.Details.Companion.key
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.Models.TaskModel
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.databinding.FragmentUserProjectsBinding
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil


class UserProjects : Fragment() {


    private var _binding : FragmentUserProjectsBinding? = null
    private val binding get() = _binding!!

    private var lastInteractionTvtIsVisible = false
    private var deadlineTvIsVisible = false
    private lateinit var layout : LinearLayout
    private lateinit var showAlert : ShowAlert

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserProjectsBinding.inflate(inflater, container, false)
        val view = binding.root

        layout = LinearLayout(requireContext())
        showAlert = ShowAlert(requireContext())

        sqlManage = ManageSQL(context, activity)

        binding.goBackButtonUserProject.setOnClickListener { requireActivity().finish() }
        binding.titleTextViewUserProject.text = key
        binding.showAndHideLastInteraction.setOnClickListener { lastInteractionTvtIsVisible = showOrHide(lastInteractionTvtIsVisible, binding.showAndHideLastInteraction, binding.showLastInteractionDateTv) }
        binding.showAndHideTargetedDeadline.setOnClickListener { deadlineTvIsVisible = showOrHide(deadlineTvIsVisible, binding.showAndHideTargetedDeadline, binding.showDeadlineTv, binding.editDeadline) }
        binding.editUserProject.setOnClickListener {

        }
        binding.editDeadline.setOnClickListener {
            MyDatePickerDialog.apply {
                putHere = binding.showDeadlineTv
                createDatePicker(requireContext(), requireActivity())
            }.show()
        }

        binding.taskDoneButton.setOnClickListener {
            val title = binding.newTaskEditText.text.toString()
            addTask(title)
        }

        binding.newTaskEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.taskDoneButton.visibility = if (count > 0) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.tasksRefreshLayout.setColorSchemeColors(Color.parseColor("#FFFFFF"))
        binding.tasksRefreshLayout.setProgressBackgroundColorSchemeColor(Color.parseColor("#F05454"))

        binding.tasksRefreshLayout.setOnRefreshListener {
            refreshAllViewsFromTasksLayout()
            binding.tasksRefreshLayout.isRefreshing = false
        }

        getProjectDetail()

        return view
    }

    private fun addTask(title : String) {
        val date = GetCurrentDate.getDate()
        val time = GetCurrentDate.getTime()
        val getReason = sqlManage.adder(mSql, "'${Details.projectId}'", "title, isDone, yearDate, time", "'$title', 'false', '$date', '$time'")
        if (getReason) {
            refreshAllViewsFromTasksLayout()
            binding.newTaskEditText.text.clear()
            UIUtil.hideKeyboard(requireActivity())
        }else{
            showAlert.errorAlert("Error", "The task couldn't be added. Please try again.", true)
        }
    }

    private fun refreshAllViewsFromTasksLayout(){
        layout.removeAllViews()
        getProjectDetail()
    }

    private lateinit var sqlManage : ManageSQL
    private var mSql: SQLiteDatabase? = null
    private fun getProjectDetail() {

        mSql = sqlManage.createSqlVariable("HomePage").apply {
            // TASKS
            sqlManage.tableCreator(this, "'${Details.projectId}'", "id INTEGER PRIMARY KEY, title VARCHAR, isDone VARCHAR, yearDate VARCHAR, time VARCHAR")
        }

        try {
            layout.orientation = LinearLayout.VERTICAL
            val cursor = mSql?.rawQuery("SELECT * FROM '${Details.projectId}'", null)

            val mItems = mutableListOf<TaskModel>()

            while (cursor!!.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex("id"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val isDone = cursor.getString(cursor.getColumnIndex("isDone"))
                val yearDate = cursor.getString(cursor.getColumnIndex("yearDate"))
                val time = cursor.getString(cursor.getColumnIndex("time"))

                mItems.add(TaskModel(id, title, isDone, yearDate, time))

            }

            mItems.reverse()
            for (i in mItems){
                val viewHere = layoutInflater.inflate(R.layout.layout_for_project_tasks, null)
                viewHere.findViewById<TextView>(R.id.taskNameText).text = i.title

                viewHere.findViewById<LinearLayout>(R.id.taskLayout).setOnLongClickListener {
                    var allowButton = showAlert.warningAlert("${i.id}", "${i.title}, ${i.yearDate}, ${i.time}", true)
                    true
                }

                layout.addView(viewHere)
            }
            binding.tasksScrollView.addView(layout)
        }
        catch (e: Exception){
            e.localizedMessage!!
        }

    }

    private fun showOrHide(controlBool: Boolean, hideOrShowResourceImageView: ImageView, vararg showOrHideTextView: View) : Boolean{
        return if (controlBool){
            for (i in showOrHideTextView){
                i.visibility = View.GONE
            }
            hideOrShowResourceImageView.setImageResource(R.drawable.ic_down_arrow)
            false
        }else{
            for (i in showOrHideTextView){
                i.visibility = View.VISIBLE
            }
            hideOrShowResourceImageView.setImageResource(R.drawable.ic_up_arrow)
            true
        }
    }
}