package com.example.mygoaldiary.Views

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mygoaldiary.Creators.BottomSheets.ChooseAvatarSheet
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.EditUserProfile.DeleteAccount
import com.example.mygoaldiary.Helpers.EditUserProfile.UpdateEmail
import com.example.mygoaldiary.Helpers.EditUserProfile.UpdatePassword
import com.example.mygoaldiary.Helpers.EditUserProfile.UpdateUsername
import com.example.mygoaldiary.databinding.ActivityEditUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

const val TOPIC = "/topics/myTopic"

open class EditUserProfile : AppCompatActivity() {


    private lateinit var showAlert : ShowAlert

    companion object {
        val currentUser = ProfileActivity.auth.currentUser!!
        lateinit var binding : ActivityEditUserProfileBinding

        fun getAvatar() = Picasso.get().load(currentUser.photoUrl).into(binding.ppIvEdit)
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
    }

    private fun fillUserProperties() {
        currentUser.email?.let {
            binding.emailTextView.setText(it)
        }
        currentUser.displayName?.let{
            binding.usernameTextView.setText(it)
        }
    }
}