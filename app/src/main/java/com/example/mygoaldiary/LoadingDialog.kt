package com.example.mygoaldiary

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.style.ThreeBounce
import com.github.ybq.android.spinkit.style.Wave

class LoadingDialog (private val activity : Activity){

    companion object{
        private lateinit var dialog : AlertDialog
    }

    fun startLoadingDialog(){
        val view = activity.layoutInflater.inflate(R.layout.loading_dialog, null)

        val builder = AlertDialog.Builder(activity).apply {
            setView(view)
            setCancelable(false)
        }

        val spinKitProgress = view.findViewById<SpinKitView>(R.id.spin_kit_progress)

        spinKitProgress.setIndeterminateDrawable(ThreeBounce())

        dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun dismissDialog(){
        dialog.dismiss()
    }
}