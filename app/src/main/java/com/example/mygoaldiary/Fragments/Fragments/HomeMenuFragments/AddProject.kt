package com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments

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
import com.example.mygoaldiary.Creators.ParamsCreator
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Customizers.TextCustomizer
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass
import com.example.mygoaldiary.FirebaseManage.Firestore
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import java.io.Serializable
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
        R.color.gray,
    )

    private lateinit var projectNameEditText : EditText
    private val textCustomizer = TextCustomizer()
    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_add_project, container, false)
        val gridLayout : androidx.gridlayout.widget.GridLayout = view.findViewById(R.id.mGridLayout)

        projectNameEditText = view.findViewById(R.id.projectNameEditText)

        view.findViewById<ImageView>(R.id.goBackButtonAddProject).setOnClickListener {
            activity!!.finish()
        }

        val doneButton : TextView = view.findViewById(R.id.nextButtonFromAddProject)
        doneButton.setOnClickListener {
            done()
        }
        projectNameEditTextChangeListener(view.findViewById(R.id.projectNameEditText), doneButton)

        val showColorImageView = view.findViewById<CircleImageView>(R.id.showColorImageView)
        showColorImageView.setImageResource(R.color.darkRed)

        colorCreator(gridLayout, showColorImageView)

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
                    colorImageView.foreground = ContextCompat.getDrawable(context!!, R.drawable.ic_checkmark_white)
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
                circleImageViewHere.foreground = ContextCompat.getDrawable(context!!, R.color.transparent)
            }
        }
    }

    private lateinit var alertCreator : ShowAlert
    private lateinit var sqlManage : ManageSQL

    private var saveInternetTooIsChecked = false
    private var projectName = ""

    private fun done(){

        alertCreator = ShowAlert(context!!)

        projectName = projectNameEditText.text.toString()

        sqlManage = ManageSQL(context, activity)
        val mSql = sqlManage.createSqlVariable("HomePage")
        sqlManage.tableCreator(mSql, "allUserProjectDeneme2", "title TEXT, projectColor INTEGER, yearDate TEXT, time TEXT")

        val bottomSheetDialog = BottomSheetDialog(context!!, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(context!!).inflate(R.layout.sheet_dialog_layout, null)

        bottomSheetView.findViewById<CircleImageView>(R.id.showColorFromSheet).setImageResource(selectedColor)
        bottomSheetView.findViewById<TextView>(R.id.projectNameFromSheet).text = projectName

        val learnDetailTextView = bottomSheetView.findViewById<TextView>(R.id.learnDetailsTv)
        learnDetailTextView.text = textCustomizer.underlinedTextCreator("Learn Details")

        learnDetailTextView.setOnClickListener {
            alertCreator = ShowAlert(context!!).apply {
                alertCreator.infoAlert(R.string.learnDetails, R.string.learnDetailsAboutUploadInternet, true)
            }
        }

        bottomSheetView.findViewById<TextView>(R.id.saveInternetTooCheckBox).setOnClickListener {
            saveInternetTooIsChecked = !saveInternetTooIsChecked
        }

        bottomSheetView.findViewById<Button>(R.id.projectSaveButton).setOnClickListener {

            val uuid = UUID.randomUUID()

            val yearDateSdf = SimpleDateFormat("yyyy-MM-dd")
            val yearDateStfString = yearDateSdf.format(Date())

            val timeSdf = SimpleDateFormat("HH:mm:ss")
            val timeDateStfString = timeSdf.format(Date())

            if (saveInternetTooIsChecked){ // Save internet too.

                if (currentUser != null){// Loged in.
                    val hashData : HashMap<String, Any> = hashMapOf(
                            "userId" to currentUser!!.uid,
                            "userName" to currentUser!!.displayName!!,
                            "projectId" to projectName,
                            "projectColor" to selectedColor,
                            "yearDate" to yearDateStfString,
                            "timeDate" to timeDateStfString
                    )
                    FirebaseSuperClass(context!!, activity!!).fireStoreManage()
                            .addData("Users", currentUser!!.uid, "Projects",uuid.toString(), hashData, {projectSaveSuccessFun(mSql, yearDateStfString, timeDateStfString)}, {projectSaveFailFun()})
                }
                else{// Not logged in.

                }

            }else{ // Just save SQL.
                val reason = sqlManage.adder(mSql, "allUserProjectDeneme2", "title, projectColor, yearDate, time", "'$projectName', $selectedColor, '$yearDateStfString', '$timeDateStfString'")
                if (reason){
                    activity!!.finish()
                }
                else{
                    alertCreator.errorAlert(R.string.error, R.string.errorOccurred, true)
                }
            }
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun projectSaveSuccessFun(mSql: SQLiteDatabase?, yearDateStfString: String, timeDateStfString: String) {
        val getReason = sqlManage.adder(
                mSql!!, "allUserProjectDeneme2", "title, projectColor, yearDate, time",
                "'$projectName', $selectedColor, '$yearDateStfString', '$timeDateStfString'"
        )
        if (getReason){
            activity!!.finish()
        }
        else{
            alertCreator.errorAlert(R.string.error, R.string.errorOccurred, true)
        }
    }

    private fun projectSaveFailFun(){
        alertCreator.errorAlert(R.string.error, R.string.errorOccurred, true)
    }

}