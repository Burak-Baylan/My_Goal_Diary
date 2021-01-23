package com.example.mygoaldiary.Creators

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.mygoaldiary.Customizers.TextCustomizer.Companion.underlinedTextCreator
import com.example.mygoaldiary.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView

class AddProjectSheet(val context : Context, val activity : Activity){

    private lateinit var alertCreator : ShowAlert

    fun createSheet(projectName: String, selectedColor : Int) : View {
        alertCreator = ShowAlert(context)
        val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.sheet_dialog_layout, null)

        bottomSheetView.findViewById<CircleImageView>(R.id.showColorFromSheet).setImageResource(selectedColor)
        bottomSheetView.findViewById<TextView>(R.id.projectNameFromSheet).text = projectName

        val learnDetailTextView = bottomSheetView.findViewById<TextView>(R.id.learnDetailsTv)
        learnDetailTextView.text = (activity.getString(R.string.learnDetails)).underlinedTextCreator()

        learnDetailTextView.setOnClickListener {
            alertCreator.infoAlert(activity.getString(R.string.learnDetails), activity.getString(R.string.learnDetailsAboutUploadInternet), true)
        }

        BottomSheetDialog(context, R.style.BottomSheetDialogTheme).apply {
            setContentView(bottomSheetView)
            show()
        }

        return bottomSheetView
    }

}