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
import com.example.mygoaldiary.Models.SocialCommentsModel
import com.example.mygoaldiary.Models.SocialModel
import com.example.mygoaldiary.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception
import java.util.ArrayList

class ReportPostSheet (val context : Context, val activity : Activity){

    private lateinit var alertCreator : ShowAlert

    private  lateinit var bottomSheetView: View

    private var socialItems : ArrayList<SocialModel>? = null
    private var commentItems : ArrayList<SocialCommentsModel>? = null

    private  var position = 0

    private lateinit var bottomSheet : BottomSheetDialog

    private fun <T : View> findViewByIdd(@IdRes id: Int): T = bottomSheetView.findViewById(id)

    fun createSheet(username : String, socialItems : ArrayList<SocialModel>?, commentItems : ArrayList<SocialCommentsModel>?, position: Int) : View {

        this.position = position

        if (socialItems != null) {
            this.socialItems = socialItems
        }else if (commentItems != null){
            this.commentItems = commentItems
        }

        alertCreator = ShowAlert(context)
        bottomSheetView = LayoutInflater.from(context).inflate(R.layout.layout_report_post, null)

        findViewByIdd<TextView>(R.id.reportPostComment).text = "${activity.getString(R.string.youReporting)} '$username':"
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
            showAlert.errorAlert(activity.getString(R.string.error), activity.getString(R.string.pleaseWriteSomething), true)
        }
    }

    private val showAlert = ShowAlert(context)
    private fun report() {

        val hashData : HashMap<String, Any> =
                if (socialItems != null) getSocialHashData(reason!!)
                else getCommentHashData(reason!!)


        firebase.collection("Reports").document().set(hashData).addOnSuccessListener {
            reported()
        }.addOnFailureListener {
            reportFail(it)
        }
    }

    private fun getSocialHashData(reportReason : String) : HashMap<String, Any> = hashMapOf(
            "idPost" to socialItems!![position].postId,
            "idPostOwner" to socialItems!![position].userUuid,
            "idReporter" to currentUser!!.uid,
            "reportReason" to reportReason,
            "timeStamp" to Timestamp.now()
    )

    private fun getCommentHashData(reportReason : String) : HashMap<String, Any> = hashMapOf(
            "idPost" to commentItems!![position].postId,
            "idComment" to commentItems!![position].commentId,
            "idCommentOwner" to commentItems!![position].userUuid,
            "idReporter" to currentUser!!.uid,
            "reportReason" to reportReason,
            "timeStamp" to Timestamp.now()
    )

    private fun reported(){
        loadingDialog.dismissDialog()
        showAlert.successAlert(activity.getString(R.string.success), activity.getString(R.string.thanksForReport), true)
        bottomSheet.dismiss()
    }

    private fun reportFail(exc : Exception){
        loadingDialog.dismissDialog()
        showAlert.errorAlert(activity.getString(R.string.error), "${activity.getString(R.string.couldntReport)}: ${exc.localizedMessage!!}", true)
        bottomSheet.dismiss()
    }
}