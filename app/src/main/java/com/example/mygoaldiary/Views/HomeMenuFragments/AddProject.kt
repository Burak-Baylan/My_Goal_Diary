package com.example.mygoaldiary.Views.HomeMenuFragments

import android.annotation.SuppressLint
import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.mygoaldiary.ConstantValues
import com.example.mygoaldiary.Creators.*
import com.example.mygoaldiary.Creators.BottomSheets.AddProjectSheet
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.Helpers.InternetController
import com.example.mygoaldiary.Helpers.MyHelpers
import com.example.mygoaldiary.Helpers.ProjectHelpers.SaveProjectToSql
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.databinding.FragmentAddProjectBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.HashMap

open class AddProject : Fragment() {

    companion object{
        var selectedColor = R.color.darkRed
    }

    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser
    private lateinit var loadingDialog : LoadingDialog

    private var _binding : FragmentAddProjectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddProjectBinding.inflate(inflater, container, false)

        sqlManage = ManageSQL(context, activity)
        mSql = sqlManage.createSqlVariable("HomePage")
        showAlert = ShowAlert(requireContext())
        alertCreator = ShowAlert(requireContext())
        loadingDialog = LoadingDialog(requireActivity())
        saveProjectToSql = SaveProjectToSql(requireContext(), requireActivity())

        with(binding) {
            this.goBackButtonAddProject.setOnClickListener { requireActivity().finish() }
            this.showColorImageView.setImageResource(R.color.darkRed)
            this.nextButtonFromAddProject.setOnClickListener { next() }
            this.projectNameEditText.requestFocus()
        }

        projectNameEditTextChangeListener(binding.projectNameEditText, binding.nextButtonFromAddProject)
        AddProjectColorCreator(requireContext()).colorCreator(binding.mGridLayout, binding.showColorImageView)

        return binding.root
    }

    private fun projectNameEditTextChangeListener(editText : EditText, doneButton : TextView){
        editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    doneButton.isEnabled = if (s.isNotEmpty()){
                        doneButton.setTextColor(Color.parseColor("#32a852"))
                        true
                    }else{
                        doneButton.setTextColor(Color.parseColor("#AEAEAE"))
                        false
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private var saveInternetTooIsChecked = false
    private var projectName : String? = null

    private lateinit var alertCreator : ShowAlert
    private lateinit var sqlManage : ManageSQL
    private var mSql: SQLiteDatabase? = null

    private lateinit var myDeadlineTv : TextView
    private lateinit var addProjectSheetView : View

    private lateinit var backupActivity : Activity

    @SuppressLint("InflateParams")
    private fun next(){
        projectName = binding.projectNameEditText.text.toString()

        addProjectSheetView = AddProjectSheet(requireContext(), requireActivity()).createSheet(projectName!!, selectedColor)

        val cannotSelectDeadlineIv = addProjectSheetView.findViewById<ImageView>(R.id.cannotSelectDeadline)
        val selectDeadlineLayout = addProjectSheetView.findViewById<LinearLayout>(R.id.selectDeadlineLayout)

        myDeadlineTv = addProjectSheetView.findViewById(R.id.selectedDeadlineTv)

        mSql!!.execSQL("CREATE TABLE IF NOT EXISTS allUserProjectDeneme3 (${ConstantValues.PROJECT_VARIABLES_STRING})")

        addProjectSheetView.findViewById<TextView>(R.id.saveInternetTooCheckBox).setOnClickListener{
            saveInternetTooIsChecked = !saveInternetTooIsChecked
            if (saveInternetTooIsChecked) cannotSelectDeadlineIv.visibility = View.GONE
            else cannotSelectDeadlineIv.visibility = View.VISIBLE
        }

        addProjectSheetView.findViewById<Button>(R.id.projectSaveButton).setOnClickListener { saveProject() }

        myDeadlineTv.text = currentDate
        backupActivity = requireActivity()
        selectDeadlineLayout.setOnClickListener {
            if (saveInternetTooIsChecked) {
                MyDatePickerDialog.apply {
                    putHere = myDeadlineTv
                    createDatePicker(requireContext(), requireActivity(), null)
                }.show()
            }else{
                showAlert.infoAlert(backupActivity.getString(R.string.info), backupActivity.getString(R.string.selectDeadlineAlsoUploadCloud), true)
            }
        }
    }

    private val currentDate = GetCurrentDate.getDate()
    private lateinit var showAlert : ShowAlert
    private lateinit var saveProjectToSql : SaveProjectToSql

    @SuppressLint("SimpleDateFormat")
    private fun saveProject() {
        val projectUuidString = MyHelpers.getUuid()
        val yearDateStfString = GetCurrentDate.getDate()
        val timeDateStfString = GetCurrentDate.getTime()

        if (saveInternetTooIsChecked){ // Save internet too.

            if (!InternetController.internetControl(requireActivity())){
                alertCreator.errorAlert("Error", "Internet yok", true)
                return
            }

            if (currentUser != null){ // Logged in.
                loadingDialog.startLoadingDialog()
                val hashData : HashMap<String, Any> = hashMapOf(
                        "userId" to currentUser!!.uid,
                        "userName" to currentUser!!.displayName!!,
                        "projectName" to projectName!!,
                        "projectColor" to selectedColor,
                        "yearDate" to yearDateStfString,
                        "timeDate" to timeDateStfString,
                        "projectId" to projectUuidString,
                        "lastInteraction" to "$currentDate ${GetCurrentDate.getTime()}",
                        "deadline" to MyDatePickerDialog.targetedTimeStamp
                )

                FirebaseFirestore.getInstance().collection("Users").document(currentUser!!.uid).collection("Projects").document(projectUuidString).set(hashData).addOnSuccessListener {
                    saveProjectToSql(yearDateStfString, timeDateStfString, projectUuidString)
                    loadingDialog.dismissDialog()
                }.addOnFailureListener {
                    alertCreator.errorAlert(getString(R.string.error), it.localizedMessage!!, true)
                    loadingDialog.dismissDialog()
                }
            } else{ // Not logged in.
                alertCreator.errorAlert(getString(R.string.error), getString(R.string.loggedInToSaveProject), true)
            }
        }else{ // Just save SQL.
            saveProjectToSql(yearDateStfString, timeDateStfString, projectUuidString)
        }
    }

    private fun saveProjectToSql(yearDateStfString: String, timeDateStfString: String, projectUuidString : String){
        saveProjectToSql.currentDate = currentDate
        saveProjectToSql.deadline = myDeadlineTv.text.toString()
        saveProjectToSql.projectName = projectName!!
        saveProjectToSql.saveInternetToo = saveInternetTooIsChecked
        saveProjectToSql.save(yearDateStfString, timeDateStfString, projectUuidString)
    }
}