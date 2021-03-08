package com.example.mygoaldiary.Views

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Switch
import com.example.mygoaldiary.Creators.BottomSheets.ChooseAvatarSheet
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.EditUserProfile.DeleteAccount
import com.example.mygoaldiary.Helpers.EditUserProfile.UpdateEmail
import com.example.mygoaldiary.Helpers.EditUserProfile.UpdatePassword
import com.example.mygoaldiary.Helpers.EditUserProfile.UpdateUsername
import com.example.mygoaldiary.R
import com.example.mygoaldiary.databinding.ActivityEditUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

const val TOPIC = "/topics/myTopic"

open class EditUserProfile : AppCompatActivity() {


    private lateinit var showAlert : ShowAlert
    private val firebase = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    private lateinit var profileVisibilitySwitch : Switch
    private lateinit var notificationVisibilitySwitch : Switch

    companion object {
        lateinit var binding : ActivityEditUserProfileBinding
    }

    fun getAvatar() =
            currentUser?.photoUrl?.let {
                Picasso.get().load(currentUser.photoUrl).into(binding.ppIvEdit)
            }

    override fun onResume() {
        getAvatar()
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#B8B8B8")
        showAlert = ShowAlert(this)

        fillUserProperties()

        notificationVisibilitySwitch = binding.notificationVisibilitySwitch
        profileVisibilitySwitch = binding.profileVisibilitySwitch

        binding.changePpIv.setOnClickListener {
            ChooseAvatarSheet( this, this).createSheet()
        }

        binding.goBackButtonEditUser.setOnClickListener {finish()}

        binding.editUsername.setOnClickListener {
            UpdateUsername().show(this, this)
        }
        binding.editEmail.setOnClickListener {
            UpdateEmail().show(this, this)
        }
        binding.editPassword.setOnClickListener {
            UpdatePassword().sendMail(this, this)
        }
        binding.deleteButton.setOnClickListener{
            DeleteAccount().delete(this, this)
        }
        notificationVisibilitySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                updater("pushNotify", false, buttonView)
            }else{
                updater("pushNotify", true, buttonView)
            }
        }
        profileVisibilitySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                updater("profileVisibility", false, buttonView)
            }else{
                updater("profileVisibility", true, buttonView)
            }
        }
        getSettings()
    }

    private fun getSettings() {
        firebase.collection("Users").document(currentUser!!.uid).get().addOnSuccessListener {
            if (it.exists() && it != null){
                val pushNotify = it["pushNotify"] as Boolean
                val profileVisibility = it["profileVisibility"] as Boolean
                controller(pushNotify, profileVisibility)
            }
        }.addOnFailureListener {
            notificationVisibilitySwitch.isEnabled = false
            profileVisibilitySwitch.isEnabled = false
        }
    }

    private fun controller(pushNotify : Boolean, profileVisibility : Boolean) {
        notificationVisibilitySwitch.isChecked = !pushNotify
        profileVisibilitySwitch.isChecked = !profileVisibility
    }

    private fun updater(updateThis : String, isChecked : Boolean, switch : CompoundButton){
        firebase.collection("Users").document(currentUser!!.uid).update(updateThis, isChecked).addOnSuccessListener {
            // DO NOTHING
        }.addOnFailureListener {
            switch.isChecked = !isChecked
            showAlert.errorAlert(getString(R.string.error), getString(R.string.theSettingCouldntUpdated), true)
        }
    }

    private fun fillUserProperties() {
        currentUser!!.email?.let {
            binding.emailTextView.setText(it)
        }
        currentUser.displayName?.let{
            binding.usernameTextView.setText(it)
        }
    }
}