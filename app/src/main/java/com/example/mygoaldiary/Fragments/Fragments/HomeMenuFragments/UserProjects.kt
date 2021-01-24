package com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments

import android.app.AlertDialog
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mygoaldiary.Creators.DeleteAlertDialog
import com.example.mygoaldiary.Creators.MyDatePickerDialog
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Customizers.TextCustomizer.Companion.setDefaultFlag
import com.example.mygoaldiary.Customizers.TextCustomizer.Companion.strikeThrough
import com.example.mygoaldiary.Details
import com.example.mygoaldiary.Details.Companion.key
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.Helpers.WordShortener
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

            val checkBoxArray = mutableListOf<Boolean>()
            for ((position, taskModel) in mItems.withIndex()){
                val viewHere = layoutInflater.inflate(R.layout.layout_for_project_tasks, null)
                val taskNameTv = viewHere.findViewById<TextView>(R.id.taskNameText)
                taskNameTv.text = taskModel.title

                val taskLayout = viewHere.findViewById<LinearLayout>(R.id.taskLayout)
                val myCb = viewHere.findViewById<CheckBox>(R.id.isTaskDoneCb)

                taskLayout.setOnLongClickListener {

                    val newTitle = WordShortener.shorten(taskModel.title, 15, 0, 15, "...")

                    val deleteProjectView = DeleteAlertDialog.apply {
                        create(requireContext(), requireActivity())
                        titleText = "You Deleting a Task"
                        messageText = "If you delete this \"$newTitle\" task, you cannot get it back. Are you sure you want to delete?"
                    }.show()
                    val alertDialogHere = DeleteAlertDialog.alertDialog

                    deleteProjectView.findViewById<Button>(R.id.deleteWarningYesButton).setOnClickListener {
                        val isChecked = deleteProjectView.findViewById<CheckBox>(R.id.deleteInternetTooCheckBox).isChecked

                        if (isChecked){

                        } else{
                            val get = sqlManage.manager(mSql, "DELETE FROM '${Details.projectId}' WHERE id = ${taskModel.id}")
                            if (get){
                                println("Silme başarılı")
                                alertDialogHere.cancel()
                                refreshAllViewsFromTasksLayout()
                            }else{
                                println("Silme başarısız")
                            }
                        }
                    }

                    true
                }

                val cbBool = false
                checkBoxArray.add(cbBool)

                taskLayout.setOnClickListener {
                    val get = taskOverOrNot(checkBoxArray[position], taskNameTv, taskModel)
                    myCb.isChecked = get
                    checkBoxArray[position] = get
                }

                myCb.setOnClickListener {
                    checkBoxArray[position] = taskOverOrNot(checkBoxArray[position], taskNameTv, taskModel)
                }

                if (taskModel.isDone == "true"){
                    myCb.isChecked = true
                    checkBoxArray[position] = true
                    taskNameTv.setTextColor(Color.parseColor("#8B8B8B"))
                    taskNameTv.strikeThrough()
                }else if (taskModel.isDone == "false"){
                    myCb.isChecked = false
                    checkBoxArray[position] = false
                    taskNameTv.setTextColor(Color.parseColor("#000000"))
                    taskNameTv.setDefaultFlag()
                }

                layout.addView(viewHere)
            }
            binding.tasksScrollView.addView(layout)
        }
        catch (e: Exception){
            e.localizedMessage!!
        }
    }

    private fun taskOverOrNot(isChecked : Boolean, textView : TextView, taskModel : TaskModel) : Boolean{
        return if (!isChecked){
            textView.strikeThrough()
            sqlManage.manager(mSql, "UPDATE '${Details.projectId}' SET isDone = 'true' WHERE id = ${taskModel.id}")
            textView.setTextColor(Color.parseColor("#8B8B8B"))
            true
        }else{
            textView.setDefaultFlag()
            sqlManage.manager(mSql, "UPDATE '${Details.projectId}' SET isDone = 'false' WHERE id = ${taskModel.id}")
            textView.setTextColor(Color.parseColor("#000000"))
            false
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