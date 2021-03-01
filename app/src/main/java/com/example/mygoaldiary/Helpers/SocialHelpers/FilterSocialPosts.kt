package com.example.mygoaldiary.Helpers.SocialHelpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.RadioButton
import com.example.mygoaldiary.R
import com.example.mygoaldiary.Views.BottomNavFragments.Social

class FilterSocialPosts : Social(){

    companion object {
        private lateinit var mContext: Context
        private lateinit var mActivity: Activity
        private lateinit var filterView: View
    }

    private lateinit var alertDialog : AlertDialog

    private fun getAlertDialog (): AlertDialog {
        val builder = createBuilder()
        alertDialog = builder.create()
        if (alertDialog.window != null) { // Transparent
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        return alertDialog
    }

    private fun createBuilder () : AlertDialog.Builder {
        val builder : AlertDialog.Builder = AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
        builder.setView(filterView)
        builder.setCancelable(true)
        return builder
    }

    @SuppressLint("InflateParams")
    private fun getFilterView() : View {
        filterView = mActivity.layoutInflater.inflate(R.layout.layout_social_filter, null)
        return filterView
    }

    private lateinit var allRb : RadioButton
    private lateinit var generalRb : RadioButton
    private lateinit var advicesRb : RadioButton
    private lateinit var goalsRb : RadioButton
    private lateinit var technologyRb : RadioButton
    private lateinit var gamesRb : RadioButton

    private var sharedPref : SharedPreferences? = null

    fun selectFilter(context : Context, activity : Activity){

        mActivity = activity
        mContext = context

        sharedPref = activity.getSharedPreferences("sharedPref",Context.MODE_PRIVATE)

        val filterView = getFilterView()
        allRb = filterView.findViewById(R.id.all)
        generalRb = filterView.findViewById(R.id.general)
        advicesRb = filterView.findViewById(R.id.advices)
        goalsRb = filterView.findViewById(R.id.goals)
        technologyRb = filterView.findViewById(R.id.technologhy)
        gamesRb = filterView.findViewById(R.id.games)

        selector()

    }

    private fun selector (){
        allRb.setOnCheckedChangeListener { _, isChecked ->
            selectFilter2(isChecked, "All")
        }
        generalRb.setOnCheckedChangeListener { _, isChecked ->
            selectFilter2(isChecked, "General")
        }
        advicesRb.setOnCheckedChangeListener { _, isChecked ->
            selectFilter2(isChecked, "Advices")
        }
        goalsRb.setOnCheckedChangeListener { _, isChecked ->
            selectFilter2(isChecked, "Goals")
        }
        technologyRb.setOnCheckedChangeListener { _, isChecked ->
            selectFilter2(isChecked, "Technology")
        }
        gamesRb.setOnCheckedChangeListener { _, isChecked ->
            selectFilter2(isChecked, "Games")
        }

        alertDialog = getAlertDialog()
        alertDialog.show()
    }

    private fun selectFilter2 (isChecked : Boolean, value : String){
        if (isChecked){
            saveFilter(value)
        }
    }

    private fun saveFilter(saveValue : String){
        sharedPref!!.edit().putString("socialFilter", saveValue).apply()
        alertDialog.dismiss()
        getAllPosts()
    }
}