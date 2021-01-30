package com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass
import com.example.mygoaldiary.Helpers.*
import com.example.mygoaldiary.Helpers.UserTasksHelpers.*
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.Models.TaskModel
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.databinding.FragmentUserProjectsBinding
import com.google.firebase.firestore.FirebaseFirestore

open class UserProjects : Fragment() {

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private var lastInteractionTvtIsVisible = false
    private var deadlineTvIsVisible = false

    companion object{
        lateinit var firebaseSuperClass : FirebaseSuperClass
        var firebase = FirebaseFirestore.getInstance()
        lateinit var loadingDialog: LoadingDialog
        var _binding : FragmentUserProjectsBinding? = null
        val binding get() = _binding!!

        lateinit var layout : LinearLayout
        lateinit var showAlert : ShowAlert

        lateinit var sqlManage : ManageSQL
        var mSql: SQLiteDatabase? = null

        lateinit var mInflater : LayoutInflater

        lateinit var mContext : Context
        lateinit var mActivity : Activity

        var totalTasks = 0
        var tasksDone = 0

        private lateinit var taskHelper : TasksHelper.Companion
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserProjectsBinding.inflate(inflater, container, false)
        val view = binding.root
        taskHelper = TasksHelper.apply {
            this.mContext = requireContext()
            this.mActivity = requireActivity()
        }

        initializeView()

        binding.goBackButtonUserProject.setOnClickListener { requireActivity().finish() }
        binding.titleTextViewUserProject.text = key
        binding.showAndHideLastInteraction.setOnClickListener { lastInteractionTvtIsVisible = MyHelpers.showOrHide().showOrHide(lastInteractionTvtIsVisible, binding.showAndHideLastInteraction,R.drawable.ic_down_arrow, R.drawable.ic_up_arrow, binding.showLastInteractionDateTv) }
        binding.showAndHideTargetedDeadline.setOnClickListener { deadlineTvIsVisible = MyHelpers.showOrHide().showOrHide(deadlineTvIsVisible, binding.showAndHideTargetedDeadline, R.drawable.ic_down_arrow, R.drawable.ic_up_arrow, binding.editDeadline, binding.showDeadlineTv) }
        binding.taskDoneButton.setOnClickListener { addTask(binding.newTaskEditText.text.toString()) }

        binding.filterIc.setOnClickListener { TaskFilter().selectFilter(requireContext(), requireActivity()) }

        binding.editDeadline.setOnClickListener {
            MyDatePickerDialog.apply {
                putHere = binding.showDeadlineTv
                createDatePicker(requireContext(), requireActivity())
            }.show()
        }

        binding.newTaskEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.taskDoneButton.visibility =
                        if (count > 0) View.VISIBLE
                        else View.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.tasksRefreshLayout.setColorSchemeColors(Color.parseColor("#FFFFFF"))
        binding.tasksRefreshLayout.setProgressBackgroundColorSchemeColor(Color.parseColor("#F05454"))
        binding.tasksRefreshLayout.setOnRefreshListener {
            refreshAllViewsFromTasksLayout(requireContext(), requireActivity())
            binding.tasksRefreshLayout.isRefreshing = false
        }

        val currentUser = firebaseSuperClass.userAuthManage().getCurrentUser()
        binding.saveTaskInternetTooCheckBox.visibility =
                if (currentUser != null) View.VISIBLE
                else View.GONE

        GetTasks(requireActivity()).get()
        return view
    }

    private fun initializeView() {
        mContext = requireContext()
        mActivity = requireActivity()
        layout = LinearLayout(requireContext())
        showAlert = ShowAlert(requireContext())
        loadingDialog = LoadingDialog(requireActivity())
        sqlManage = ManageSQL(context, activity)
        firebaseSuperClass = FirebaseSuperClass(requireContext(), requireActivity())
        mInflater = LayoutInflater.from(requireContext())
    }

    private fun addTask(title : String) {
        taskHelper.addTask().add(title)
    }

    protected fun refreshAllViewsFromTasksLayout(context : Context, activity : Activity){
        layout.removeAllViews()
        mContext = context
        mActivity = activity
        GetTasks(mActivity).get()
    }

    protected fun taskNameTvCustomizer(trueOrFalse : String, taskNameTv : TextView){
        if (trueOrFalse == "true"){
            taskNameTv.setTextColor(Color.parseColor("#8B8B8B"))
            taskNameTv.strikeThrough()
        }else if (trueOrFalse == "false"){
            taskNameTv.setTextColor(Color.parseColor("#000000"))
            taskNameTv.setDefaultFlag()
        }
    }

    @SuppressLint("SetTextI18n")
    protected fun taskOverOrNot(isChecked : Boolean, textView : TextView, taskModel : TaskModel) : Boolean{
        val returnBool = if (!isChecked){
            tasksDone++
            taskNameTvCustomizer("true", textView)
            sqlManage.manager(mSql, "UPDATE '${Details.projectId}' SET isDone = 'true' WHERE id = ${taskModel.id}")
            true
        }else{
            tasksDone--
            sqlManage.manager(mSql, "UPDATE '${Details.projectId}' SET isDone = 'false' WHERE id = ${taskModel.id}")
            taskNameTvCustomizer("false", textView)
            false
        }
        binding.tasksDone.text = "$tasksDone/$totalTasks"
        return returnBool
    }

    protected fun taskLongClickListener(taskModel: TaskModel) {
        val newTitle = ShortenWord.shorten(taskModel.title, 15, 0, 15, "...")
        val deleteProjectView = DeleteAlertDialog.apply {
            create(mContext, mActivity)
            titleText = "You Deleting a Task"
            messageText = "If you delete this \"$newTitle\" task, you cannot get it back. Are you sure you want to delete?"
            this.view.findViewById<CheckBox>(R.id.deleteInternetTooCheckBox).visibility = if (taskModel.isHybrid == "false"){
                View.GONE
            }else{
                View.VISIBLE
            }
        }.show()
        val ctxHere = mContext
        deleteProjectView.findViewById<Button>(R.id.deleteWarningYesButton).setOnClickListener {
            DeleteTask(ctxHere, mActivity).delete(deleteProjectView, taskModel.taskUuid, taskModel.isHybrid)
        }
    }

    fun internetErrorFun(ctx : Context){
        showAlert = ShowAlert(ctx)
        showAlert.errorAlert("error", ctx.getString(R.string.project_id), true)
    }
}