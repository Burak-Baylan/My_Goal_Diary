package com.example.mygoaldiary.Creators.BottomSheets

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.annotation.IdRes
import com.example.mygoaldiary.Creators.ChooseAvatarSelectorCreator
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.R
import com.example.mygoaldiary.Views.EditUserProfile
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore


open class ChooseAvatarSheet(val context: Context, val activity: Activity) : EditUserProfile(){

    companion object {
        lateinit var alertCreator: ShowAlert
        lateinit var bottomSheetView: View
    }

    private var loadingDialog = LoadingDialog(activity)

    private lateinit var applyButton : Button

    private lateinit var bottomSheet : BottomSheetDialog

    private fun <T : View> findViewByIdd(@IdRes id: Int): T = bottomSheetView.findViewById(id)

    fun createSheet() : View {
        alertCreator = ShowAlert(context)
        bottomSheetView = LayoutInflater.from(context).inflate(R.layout.layout_select_avatar, null)

        ChooseAvatarSelectorCreator(context, activity).getAvatars()
        applyButton = findViewByIdd(R.id.applyButton)

        applyButton.setOnClickListener {
            applyAvatar()
        }

        bottomSheet = BottomSheetDialog(context, R.style.BottomSheetDialogTheme).apply {
            setContentView(bottomSheetView)
        }

        bottomSheet.show()

        return bottomSheetView
    }


    private val firebase = FirebaseFirestore.getInstance()
    private fun applyAvatar() {
        loadingDialog.startLoadingDialog()
        getAvatarLink()
    }

    private fun getAvatarLink() {

        fun error(){
            showAlert.errorAlert(getString(R.string.error), "An error occurred when change the avatar.", true)
            loadingDialog.dismissDialog()
        }

        val avatarName = ChooseAvatarSelectorCreator.chosenAvatarName
        if (avatarName != null){
            firebase.collection("Avatars").document(avatarName).get().addOnSuccessListener { doc ->
                if (doc.exists()){
                    putAvatarFirebase(doc["link"] as String)
                }else error()
            }.addOnFailureListener { error() }
        }else error()
    }

    private fun putAvatarFirebase(link : String){
        println("KÖRINT USIR: ${currentUser.uid}")
        firebase.collection("Users").document(currentUser.uid).update("avatarLink", link).addOnSuccessListener {
            putUserAvatar(link)
        }.addOnFailureListener {
            showAlert.errorAlert("Error", "Your avatar couldn't change.", true)
            loadingDialog.dismissDialog()
        }
    }

    private val showAlert = ShowAlert(context)
    private lateinit var updateRequest : UserProfileChangeRequest

    private fun putUserAvatar(link: String) {
        updateRequest = userProfileChangeRequest {
            this.photoUri = Uri.parse(link)
        }
        currentUser.updateProfile(updateRequest).addOnSuccessListener {
            showAlert.successAlert("Success", "Your avatar changed.", true)
            getAvatar()
            bottomSheet.dismiss()
            loadingDialog.dismissDialog()
        }.addOnFailureListener {
            firebase.collection("Users").document(currentUser.uid).update("avatarLink", null)
            showAlert.errorAlert("Error", "Your avatar couldn't change.", true)
            bottomSheet.dismiss()
            loadingDialog.dismissDialog()
        }
    }
}