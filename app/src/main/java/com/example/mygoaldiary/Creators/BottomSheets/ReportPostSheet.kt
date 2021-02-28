package com.example.mygoaldiary.Creators.BottomSheets

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.IdRes
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.Models.SocialModel
import com.example.mygoaldiary.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class ReportPostSheet (val context : Context, val activity : Activity){

    private lateinit var alertCreator : ShowAlert

    private  lateinit var bottomSheetView: View

    private  lateinit var items : ArrayList<SocialModel>
    private  var position = 0

    private lateinit var bottomSheet : BottomSheetDialog

    private fun <T : View> findViewByIdd(@IdRes id: Int): T = bottomSheetView.findViewById(id)

    fun createSheet(username : String, items : ArrayList<SocialModel>, position: Int) : View {

        this.position = position
        this.items = items

        alertCreator = ShowAlert(context)
        bottomSheetView = LayoutInflater.from(context).inflate(R.layout.layout_report_post, null)

        findViewByIdd<TextView>(R.id.reportPostComment).text = "You reporting '$username':"
        findViewByIdd<Button>(R.id.sendReportReason).setOnClickListener { sendReason() }

        bottomSheet = BottomSheetDialog(context, R.style.BottomSheetDialogTheme).apply {
            setContentView(bottomSheetView)
        }
        bottomSheet.show()

        return bottomSheetView
    }


    private var reason : String? = null
    private lateinit var reasonEditText : EditText

    private var firebase = FirebaseFirestore.getInstance()
    private var currentUser = FirebaseAuth.getInstance().currentUser

    private var loadingDialog = LoadingDialog(activity)

    private fun sendReason() {
        reasonEditText = findViewByIdd(R.id.reportPostEditText)
        if (reasonEditText.text.isNotEmpty() && reasonEditText.text != null){
            loadingDialog.startLoadingDialog()
            reason = reasonEditText.text.toString()
            report()
        }else{
            showAlert.errorAlert("Erorr", "Please write something.", true)
        }
    }

    private val showAlert = ShowAlert(context)
    private fun report() {
        firebase.collection("Reports").document().set(getHashData(reason!!)).addOnSuccessListener {
            loadingDialog.dismissDialog()
            showAlert.successAlert("Success", "Thank for your report.", true)
            bottomSheet.dismiss()
        }.addOnFailureListener {
            loadingDialog.dismissDialog()
            showAlert.errorAlert("Error", "Raporlanamadı ${it.localizedMessage!!}", true)
            bottomSheet.dismiss()
        }
    }

    private fun getHashData(reportReason : String) : HashMap<String, Any> = hashMapOf(
            "idPost" to items[position].postId,
            "idPostOwner" to items[position].userUuid,
            "idReporter" to currentUser!!.uid,
            "reportReason" to reportReason,
            "reportDate" to GetCurrentDate.getDate(),
            "reportTime" to GetCurrentDate.getTime(),
            "timeStamp" to Timestamp.now()
    )
}










