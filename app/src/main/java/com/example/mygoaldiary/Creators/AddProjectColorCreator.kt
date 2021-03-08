package com.example.mygoaldiary.Creators

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.mygoaldiary.Views.HomeMenuFragments.AddProject
import com.example.mygoaldiary.R
import de.hdodenhof.circleimageview.CircleImageView

class AddProjectColorCreator (private val contextHere : Context): AddProject(){

    private var colorArray = arrayOf(
        R.color.darkRed,
        R.color.lightRed,
        R.color.darkOrange,
        R.color.lightOrange,
        R.color.yellow,
        R.color.lightGreen,
        R.color.green,
        R.color.garipGreen,
        R.color.seaGreen,
        R.color.cyan,
        R.color.lightBlue,
        R.color.blueForAddProject,
        R.color.navyBlue,
        R.color.darkBlue,
        R.color.purple,
        R.color.pink,
        R.color.lightPink,
        R.color.garipPink,
        R.color.darkGray,
        R.color.gray
    )

    private val imageViewArray : MutableList<CircleImageView> = mutableListOf()

    private lateinit var mShowColorImageView : CircleImageView

    fun colorCreator(layout : androidx.gridlayout.widget.GridLayout, showColorImageView : CircleImageView){
        this.mShowColorImageView = showColorImageView
        var marginTopInt : Int
        var marginBottomInt : Int

        for ((counter, i) in (0..19).withIndex()){
            val colorImageView = CircleImageView(contextHere)
            colorImageView.setImageResource(colorArray[i])

            imageViewArray.add(colorImageView)
            colorImageView.setOnClickListener {
                clickListener(i, colorImageView)
            }
            marginTopInt = 0
            marginBottomInt = if (counter < 14){
                if (counter < 5) marginTopInt = 25
                50
            } else 25
            colorImageView.layoutParams = ParamsCreator().linearLayoutLayoutParamsCreator(
                80, 80, 25, 25, marginBottomInt, marginTopInt
            )
            layout.addView(colorImageView)
        }
    }

    private fun clickListener(i : Int, colorImageView : ImageView){
        allForegroundCleaner(imageViewArray)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            colorImageView.foreground = ContextCompat.getDrawable(contextHere, R.drawable.ic_checkmark_white)
        }
        selectedColor = colorArray[i] // colorNow
        mShowColorImageView.setImageResource(colorArray[i]) // colorNow
    }

    private fun allForegroundCleaner(imageViewArray: MutableList<CircleImageView>){
            for (i in 0 until imageViewArray.size) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    imageViewArray[i].foreground =
                        ContextCompat.getDrawable(contextHere, R.color.transparent)
                }
            }
    }
}