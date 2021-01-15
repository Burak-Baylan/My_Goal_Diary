package com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.mygoaldiary.ComponentCreator.ParamsCreator
import com.example.mygoaldiary.ComponentCreator.ShowAlert
import com.example.mygoaldiary.Customizers.TextCustomizer
import com.example.mygoaldiary.Details
import com.example.mygoaldiary.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        val view = inflater.inflate(R.layout.fragment_add_project, container, false)
        val gridLayout : androidx.gridlayout.widget.GridLayout = view.findViewById(R.id.mGridLayout)

        projectNameEditText = view.findViewById(R.id.projectNameEditText)

        view.findViewById<ImageView>(R.id.goBackButtonAddProject).setOnClickListener {
            activity!!.finish()
        }

        val doneButton : TextView = view.findViewById(R.id.doneButtonFromAddProject)
        doneButton.setOnClickListener {
            done()
        }
        projectNameEditTextChangeListener(view.findViewById(R.id.projectNameEditText), doneButton)

        val showColorImageView = view.findViewById<CircleImageView>(R.id.showColorImageView)
        showColorImageView.setImageResource(R.color.darkRed)
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            showColorImageView.foreground = ContextCompat.getDrawable(context!!, R.drawable.ic_checkmark_white)
        }*/

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
                println("Renk: $colorNow")
                selectedColor = colorNow
                showColorImageView.setImageResource(colorNow)
            }
            colorImageView.layoutParams = paramsCreator.linearLayoutLayoutParamsCreator(
                    80, 80, 25, 25, 50, null
            )
            layout.addView(colorImageView)
        }
    }

    private fun allForegroundCleaner(imageViewArray: MutableList<CircleImageView>) {
        for(i in 0 until imageViewArray.size){
            val circleImageViewHere = imageViewArray[i]
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                circleImageViewHere.foreground = ContextCompat.getDrawable(context!!, R.color.transparent)
            }
        }
    }

    private lateinit var alertCreator : ShowAlert

    private fun done(){

        var saveInternetTooIsChecked = false

        alertCreator = ShowAlert(context!!)
//        alertCreator.errorAlert("Onemli Error", "Baya baya baya baya baya baya baya önemli bir mesaj", true)

        val bottomSheetDialog = BottomSheetDialog(context!!, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(context!!).inflate(R.layout.sheet_dialog_layout, null)

        bottomSheetView.findViewById<CircleImageView>(R.id.showColorFromSheet).setImageResource(selectedColor)
        bottomSheetView.findViewById<TextView>(R.id.projectNameFromSheet).text = projectNameEditText.text.toString()

        val learnDetailTextView = bottomSheetView.findViewById<TextView>(R.id.learnDetailsTv)
        learnDetailTextView.text = textCustomizer.underlinedTextCreator("Learn Details")

        learnDetailTextView.setOnClickListener {
            alertCreator = ShowAlert(context!!).apply {
                alertCreator.infoAlert("Learn Details", R.string.learnDetailsAboutUploadInternet, true)
            }
        }

        bottomSheetView.findViewById<TextView>(R.id.saveInternetTooCheckBox).setOnClickListener {
            if (!saveInternetTooIsChecked){
                saveInternetTooIsChecked = true
            }
            else if (saveInternetTooIsChecked){
                saveInternetTooIsChecked = false
            }
        }

        bottomSheetView.findViewById<Button>(R.id.projectSaveButton).setOnClickListener {
            if (saveInternetTooIsChecked){ // Save internet too.

            }else{ // Just save SQL.

            }
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

    }
}