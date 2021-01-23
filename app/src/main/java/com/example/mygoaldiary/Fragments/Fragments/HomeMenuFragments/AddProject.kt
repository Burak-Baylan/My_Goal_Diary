package com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.mygoaldiary.Creators.AddProjectSheet
import com.example.mygoaldiary.Creators.ParamsCreator
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Customizers.TextCustomizer.Companion.underlinedTextCreator
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.databinding.FragmentAddProjectBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AddProject : Fragment() {

    private var colorArray = arrayOf(
        R.color.darkRed,
        R.color.lightRed,
        R.color.darkOrange,
        R.color.lightOrange,
        R.color.yellow,
        R.color.lightGreen,
        R.color.green,
        R.color.darkGreen,
        R.color.seaGreen,
        R.color.cyan,
        R.color.lightBlue,
        R.color.blue,
        R.color.navyBlue,
        R.color.darkBlue,
        R.color.purple,
        R.color.pink,
        R.color.lightPink,
        R.color.black,
        R.color.darkGray,
        R.color.gray
    )

    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser
    private lateinit var loadingDialog : LoadingDialog

    private var _binding : FragmentAddProjectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddProjectBinding.inflate(inflater, container, false)
        val view = binding.root

        sqlManage = ManageSQL(context, activity)
        mSql = sqlManage.createSqlVariable("HomePage")

        val gridLayout : androidx.gridlayout.widget.GridLayout = view.findViewById(R.id.mGridLayout)

        alertCreator = ShowAlert(requireContext())

        binding.goBackButtonAddProject.setOnClickListener {
            requireActivity().finish()
        }

        loadingDialog = LoadingDialog(requireActivity())

        binding.nextButtonFromAddProject.setOnClickListener {
            done()
        }
        projectNameEditTextChangeListener(binding.projectNameEditText, binding.nextButtonFromAddProject)

        binding.showColorImageView.setImageResource(R.color.darkRed)

        colorCreator(gridLayout, binding.showColorImageView)

        return view
    }

    private fun projectNameEditTextChangeListener(editText : EditText, doneButton : TextView){
        editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                doneButton.isEnabled = count > 0
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private val imageViewArray : MutableList<CircleImageView> = mutableListOf()
    private val paramsCreator = ParamsCreator()

    private var selectedColor = R.color.darkRed

    private fun colorCreator(layout : androidx.gridlayout.widget.GridLayout, showColorImageView : CircleImageView){
        var marginTopInt : Int
        var marginBottomInt : Int

        for ((counter, i) in (0..19).withIndex()){
            val colorImageView = CircleImageView(context)
            colorImageView.setImageResource(colorArray[i])
            val colorNow = colorArray[i]

            imageViewArray.add(colorImageView)
            colorImageView.setOnClickListener {
                allForegroundCleaner(imageViewArray)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    colorImageView.foreground = ContextCompat.getDrawable(requireContext(), R.drawable.ic_checkmark_white)
                }
                selectedColor = colorNow
                showColorImageView.setImageResource(colorNow)
            }

            marginTopInt = 0
            marginBottomInt = if (counter < 14){
                if (counter < 5) marginTopInt = 25
                50
            } else 25
            colorImageView.layoutParams = paramsCreator.linearLayoutLayoutParamsCreator(
                    80, 80, 25, 25, marginBottomInt, marginTopInt
            )
            layout.addView(colorImageView)
        }
    }

    private fun allForegroundCleaner(imageViewArray: MutableList<CircleImageView>){
        for(i in 0 until imageViewArray.size){
            val circleImageViewHere = imageViewArray[i]
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                circleImageViewHere.foreground = ContextCompat.getDrawable(requireContext(), R.color.transparent)
            }
        }
    }


    private var saveInternetTooIsChecked = false
    private var projectName = ""


    private lateinit var alertCreator : ShowAlert
    private lateinit var sqlManage : ManageSQL
    private var mSql: SQLiteDatabase? = null

    @SuppressLint("InflateParams")
    private fun done(){
        projectName = binding.projectNameEditText.text.toString()

        sqlManage.tableCreator(mSql, "allUserProjectDeneme3", "id INTEGER PRIMARY KEY, title TEXT, projectColor INTEGER, yearDate TEXT, time TEXT, lastInteraction, targetedDeadline")

        val addProjectSheet = AddProjectSheet(requireContext(), requireActivity()).createSheet(projectName, selectedColor)

        addProjectSheet.findViewById<TextView>(R.id.saveInternetTooCheckBox).setOnClickListener {
            saveInternetTooIsChecked = !saveInternetTooIsChecked
        }

        addProjectSheet.findViewById<Button>(R.id.projectSaveButton).setOnClickListener {
            saveProject()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveProject() {
        val uuid = UUID.randomUUID()

        val yearDateSdf = SimpleDateFormat("yyyy-MM-dd")
        val yearDateStfString = yearDateSdf.format(Date())

        val timeSdf = SimpleDateFormat("HH:mm:ss")
        val timeDateStfString = timeSdf.format(Date())

        if (saveInternetTooIsChecked){ // Save internet too.
            if (currentUser != null){// Loged in.
                loadingDialog.startLoadingDialog()
                val hashData : HashMap<String, Any> = hashMapOf(
                        "userId" to currentUser!!.uid,
                        "userName" to currentUser!!.displayName!!,
                        "projectId" to projectName,
                        "projectColor" to selectedColor,
                        "yearDate" to yearDateStfString,
                        "timeDate" to timeDateStfString
                )
                FirebaseSuperClass(requireContext(), requireActivity()).fireStoreManage()
                        .addData("Users", currentUser!!.uid, "Projects",uuid.toString(), hashData, {
                            projectSaveSuccessFun(mSql, yearDateStfString, timeDateStfString)}, {projectSaveFailFun() })
            }
            else{// Not logged in.

            }
        }else{ // Just save SQL.
            saveProjectFromSql(mSql, yearDateStfString, timeDateStfString)
        }
    }

    private fun projectSaveSuccessFun(mSql: SQLiteDatabase?, yearDateStfString: String, timeDateStfString: String) {
        saveProjectFromSql(mSql, yearDateStfString, timeDateStfString)
        loadingDialog.dismissDialog()
    }

    private fun projectSaveFailFun(){
        alertCreator.errorAlert(getString(R.string.error), getString(R.string.errorOccurred), true)
        loadingDialog.dismissDialog()
    }

    private fun saveProjectFromSql(mSql: SQLiteDatabase?, yearDateStfString: String, timeDateStfString: String){
        // Add Project From User Projects
        val getReason = sqlManage.adder(
                mSql!!, "allUserProjectDeneme3", "title, projectColor, yearDate, time",
                "'$projectName', $selectedColor, '$yearDateStfString', '$timeDateStfString'"
        )
        if (getReason){
            requireActivity().finish()
        }
        else{
            alertCreator.errorAlert(getString(R.string.error), getString(R.string.errorOccurred), true)
        }
    }
}