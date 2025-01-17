package com.example.mygoaldiary.Views.HomeMenuFragments.UserProjects

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.mygoaldiary.Creators.MyDatePickerDialog
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.Helpers.InternetController
import com.example.mygoaldiary.Helpers.ShowOrHide
import com.example.mygoaldiary.Helpers.UserTasksHelpers.MoveFromLocalToCloud
import com.example.mygoaldiary.Helpers.UserTasksHelpers.TaskFilter
import com.example.mygoaldiary.Helpers.UserTasksHelpers.TasksHelper
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.UpdateDeadline
import com.example.mygoaldiary.SQL.UpdateLastInteraction
import com.example.mygoaldiary.Views.Details
import com.google.firebase.auth.FirebaseAuth

class UserProjectsClickListeners : UserProjects() {

    private var taskHelper : TasksHelper.Companion

    init {
        taskHelper = TasksHelper.apply {
            this.ctx = mContext
            this.act = mActivity
        }
    }

    fun listen(){
        with(binding) {
            this.goBackButtonUserProject.setOnClickListener { mActivity.finish() }
            this.taskDoneButton.setOnClickListener { taskHelper.addTask().add(this.newTaskEditText.text.toString()) }
            this.filterIc.setOnClickListener { TaskFilter().selectFilter(mContext, mActivity) }
            this.editDeadline.setOnClickListener { editDeadline() }
            this.showAndHideLastInteraction.setOnClickListener { lastInteractionTvtIsVisible = ShowOrHide.showOrHide(lastInteractionTvtIsVisible, this.showAndHideLastInteraction, this.showLastInteractionDateTv) }
            this.showAndHideTargetedDeadline.setOnClickListener { deadlineTvIsVisible = ShowOrHide.showOrHide(deadlineTvIsVisible, this.showAndHideTargetedDeadline, this.editDeadline, this.showDeadlineTv) }
            this.getInfoForDeadline.setOnClickListener { showAlert.infoAlert(mActivity.getString(R.string.info), mActivity.getString(R.string.ifYouWantToSelectDeadline), true) }
            this.infoForCantUploadCloud.setOnClickListener { showAlert.infoAlert(mActivity.getString(R.string.info), mActivity.getString(R.string.taskNotCloud), true) }
            this.infoUserNull.setOnClickListener { showAlert.infoAlert(mActivity.getString(R.string.info), mActivity.getString(R.string.youCanUploadYourProjectsAndTasksToTheCloud),true) }

            this.newTaskEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null) {
                        this@with.taskDoneButton.visibility =
                                if (s.isNotEmpty()) View.VISIBLE
                                else View.GONE
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            this.tasksRefreshLayout.setColorSchemeColors(Color.parseColor("#FFFFFF"))
            this.tasksRefreshLayout.setProgressBackgroundColorSchemeColor(Color.parseColor("#F05454"))
            this.tasksRefreshLayout.setOnRefreshListener {
                refreshAllViewsFromTasksLayout(mContext, mActivity)
                binding.tasksRefreshLayout.isRefreshing = false
            }

            this.uploadToCloudIc.setOnClickListener {
                showAlert.warningAlert(mActivity.getString(R.string.warning), mActivity.getString(R.string.youUploadingYourProjectToTheCloud), true).apply {
                    // This button is positive button
                    this.setOnClickListener {
                        ShowAlert.mAlertDialog.dismiss()
                        MoveFromLocalToCloud(mContext, mActivity).startMovingTasks()
                    }
                }
            }
        }
    }

    private fun editDeadline() {
        MyDatePickerDialog.apply {
            putHere = binding.showDeadlineTv
            createDatePicker(mContext, mActivity) { saveDate() }
        }.show()
    }

    private fun saveDate(){
        if (InternetController.internetControl(mActivity)) {
            FirebaseAuth.getInstance().currentUser?.let {
                loadingDialog.startLoadingDialog()
                firebase.collection("Users")
                        .document(it.uid)
                        .collection("Projects")
                        .document(Details.projectUuid)
                        .update("deadline", MyDatePickerDialog.targetedTimeStamp).addOnSuccessListener {
                            UpdateLastInteraction.update()
                            showAlert.successAlert(mActivity.getString(R.string.success), mActivity.getString(R.string.deadlineUpdateSuccess), true)
                            loadingDialog.dismissDialog()
                        }.addOnFailureListener { e ->
                            showAlert.errorAlert(mActivity.getString(R.string.error), e.localizedMessage!!, true)
                            loadingDialog.dismissDialog()
                        }
            }
        }else{
            showAlert.errorAlert(mActivity.getString(R.string.error), mActivity.getString(R.string.youMustConnectToUpdateDeadline), true)
            binding.showDeadlineTv.text = GetCurrentDate.getDate()
        }
    }
}