package com.example.mygoaldiary.Creators

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.mygoaldiary.Creators.BottomSheets.ChooseAvatarSheet
import com.example.mygoaldiary.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChooseAvatarSelectorCreator (val contextHere : Context, val activityHere : Activity) : ChooseAvatarSheet(contextHere, activityHere){


    private val avatarsArray = arrayOf(
            R.drawable.avatar_genc_erkek_b,
            R.drawable.avatar_genc_erkek_s,
            R.drawable.avatar_orta_erkek_b,
            R.drawable.avatar_orta_erkek_s,
            R.drawable.avatar_genc_erkek_f_b,
            R.drawable.avatar_genc_erkek_f_s,
            R.drawable.avatar_genc_kadin_b,
            R.drawable.avatar_genc_kadin_s,
            R.drawable.avatar_orta_kadin_b,
            R.drawable.avatar_orta_kadin_s,
            R.drawable.avatar_orta_kadin_f_b,
            R.drawable.avatar_orta_kadin_f_s,
    )

    private val avatarsNameArray = arrayOf(
            "genc_erkek_beyaz",
            "genc_erkek_siyah",
            "orta_erkek_beyaz",
            "orta_erkek_siyah",
            "genc_erkek_beyaz_f",
            "genc_erkek_siyah_f",
            "genc_kadin_beyaz",
            "genc_kadin_siyah",
            "orta_kadin_beyaz",
            "orta_kadin_siyah",
            "orta_kadin_beyaz_f",
            "orta_kadin_siyah_f"
    )

    private val imageViewArray : MutableList<CircleImageView> = mutableListOf()

    fun getAvatars(){

        val mLayout = bottomSheetView.findViewById<androidx.gridlayout.widget.GridLayout>(R.id.avatarsGridLayout)

        var marginTopInt : Int
        var marginBottomInt : Int

        for ((counter, i) in (0..11).withIndex()){
            val avatarImageView = CircleImageView(contextHere)

            Picasso.get().load(avatarsArray[i])

            avatarImageView.setImageResource(avatarsArray[i])

            imageViewArray.add(avatarImageView)
            avatarImageView.setOnClickListener {
                clickListener(i, avatarImageView)
            }
            marginTopInt = 0
            marginBottomInt = if (counter < 14){
                if (counter < 5) marginTopInt = 10
                10
            } else 25
            avatarImageView.layoutParams = ParamsCreator().linearLayoutLayoutParamsCreator(
                    100, 100, 25, 25, marginBottomInt, marginTopInt
            )
            mLayout.addView(avatarImageView)
        }
    }

    companion object {
        var chosenAvatarName: String? = null
    }
    private fun clickListener(position : Int, colorImageView : ImageView){
        allForegroundCleaner(imageViewArray)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            colorImageView.foreground = ContextCompat.getDrawable(context, R.drawable.ic_checkmark_white)
        }
        chosenAvatarName = avatarsNameArray[position]
        bottomSheetView.findViewById<CircleImageView>(R.id.selectedAvatarIv).setImageResource(avatarsArray[position]);
    }

    private fun allForegroundCleaner(imageViewArray: MutableList<CircleImageView>){
        for (i in 0 until imageViewArray.size) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                imageViewArray[i].foreground =
                        ContextCompat.getDrawable(context, R.color.transparent)
            }
        }
    }

}