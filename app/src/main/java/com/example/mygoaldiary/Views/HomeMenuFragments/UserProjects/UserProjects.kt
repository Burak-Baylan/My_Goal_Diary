package com.example.mygoaldiary.Views.HomeMenuFragments.UserProjects

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mygoaldiary.Creators.DeleteAlertDialog
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Customizers.TextCustomizer.Companion.setDefaultFlag
import com.example.mygoaldiary.Customizers.TextCustomizer.Companion.strikeThrough
import com.example.mygoaldiary.Views.Details
import com.example.mygoaldiary.Views.Details.Companion.key
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass
import com.example.mygoaldiary.Helpers.*
import com.example.mygoaldiary.Helpers.UserTasksHelpers.*
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.Models.TaskModel
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.databinding.FragmentUserProjectsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

open class UserProjects : Fragment() {

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    val currentUser = FirebaseAuth.getInstance().currentUser

    companion object{
        lateinit var firebaseSuperClass : FirebaseSuperClass
        lateinit var layout : LinearLayout
        lateinit var showAlert : ShowAlert
        lateinit var loadingDialog: LoadingDialog
        lateinit var mInflater : LayoutInflater
        lateinit var mContext : Context
        lateinit var mActivity : Activity
        lateinit var sqlManage : ManageSQL
        var _binding : FragmentUserProjectsBinding? = null
        val binding get() = _binding!!
        var firebase = FirebaseFirestore.getInstance()
        var mSql: SQLiteDatabase? = null
        var totalTasks = 0
        var tasksDone = 0
        var lastInteractionTvtIsVisible = false
        var deadlineTvIsVisible = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserProjectsBinding.inflate(inflater, container, false)
        val view = binding.root

        initializeViews()
        UserProjectsClickListeners().listen()

        with(binding) {
            this.showLastInteractionDateTv.text = Details.lastInteraction
            this.showDeadlineTv.text = Details.targetedDeadline
            this.titleTextViewUserProject.text = key

            if (Details.projectIsHybrid == "false"){
                this.infoForCantUploadCloud.visibility = View.VISIBLE
                this.uploadToCloudIc.visibility = View.VISIBLE
                this.getInfoForDeadline.visibility = View.VISIBLE
                this.saveTaskInternetTooCheckBox.strikeThrough()
                this.targetedDeadlineTv.strikeThrough()
                this.saveTaskInternetTooCheckBox.isEnabled = false
                this.showAndHideTargetedDeadline.isEnabled = false
            }else {
                if (!InternetController.internetControl(requireActivity())) this.internetOffIc.visibility = View.VISIBLE
                this.getInfoForDeadline.visibility = View.GONE
                this.targetedDeadlineTv.setDefaultFlag()
                this.saveTaskInternetTooCheckBox.isEnabled = true
                this.showAndHideTargetedDeadline.isEnabled = true
            }
        }
        userControl()
        GetTasks(requireActivity()).getAndPut()
        return view
    }

    private fun userControl() {
        if (currentUser != null){
            binding.saveTaskInternetTooCheckBox.visibility = View.VISIBLE
            //binding.uploadToCloudIc.visibility = View.VISIBLE
            binding.infoUserNull.visibility = View.GONE
        } else {
            binding.saveTaskInternetTooCheckBox.visibility = View.GONE
            //binding.uploadToCloudIc.visibility = View.GONE
            binding.infoForCantUploadCloud.visibility = View.GONE
            binding.infoUserNull.visibility = View.VISIBLE
        }
    }

    private fun initializeViews() {
        mContext = requireContext()
        mActivity = requireActivity()
        layout = LinearLayout(requireContext())
        showAlert = ShowAlert(requireContext())
        loadingDialog = LoadingDialog(requireActivity())
        sqlManage = ManageSQL(context, activity)
        firebaseSuperClass = FirebaseSuperClass(requireContext(), requireActivity())
        mInflater = LayoutInflater.from(requireContext())
    }

    protected fun refreshAllViewsFromTasksLayout(context : Context, activity : Activity){
        layout.removeAllViews()
        mContext = context
        mActivity = activity
        GetTasks(mActivity).getAndPut()
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

    protected fun taskLongClickListener(taskModel: TaskModel) {
        val shortTitle = ShortenWord.shorten(taskModel.title, 15, 0, 15, "...")
        val deleteProjectView = DeleteAlertDialog.apply {
            create(mContext, mActivity)
            titleText = mActivity.getString(R.string.youDeletingAProject)
            messageText = "${mActivity.getString(R.string.ifYouDeleteThis)} \"$shortTitle\" ${mActivity.getString(R.string.task)}, ${mActivity.getString(R.string.youCannotGetItBack)}"
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
}