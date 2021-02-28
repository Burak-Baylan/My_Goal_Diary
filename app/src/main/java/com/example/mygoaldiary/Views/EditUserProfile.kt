package com.example.mygoaldiary.Views

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.example.mygoaldiary.Creators.BottomSheets.ChooseAvatarSheet
import com.example.mygoaldiary.Notification.*
import com.example.mygoaldiary.databinding.ActivityEditUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TOPIC = "/topics/myTopic"

open class EditUserProfile : AppCompatActivity() {

    companion object {
        val currentUser = ProfileActivity.auth.currentUser!!
        lateinit var binding : ActivityEditUserProfileBinding

        fun getAvatar() = Picasso.get().load(currentUser.photoUrl).into(binding.ppIvEdit)
    }

    override fun onResume() {
        getAvatar()
        super.onResume()
    }

    private lateinit var usernameEditText : EditText
    private lateinit var emailEditText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#B8B8B8")

        fillUserProperties()

        binding.changePpIv.setOnClickListener {
            ChooseAvatarSheet(this, this).createSheet()
        }

        binding.goBackButtonEditUser.setOnClickListener {finish()}


        usernameEditText = binding.usernameEditText
        emailEditText = binding.emailEditText


        binding.deleteButton.setOnClickListener{
            val currentUser = FirebaseAuth.getInstance().currentUser!!
            currentUser.delete().addOnSuccessListener {
                FirebaseFirestore.getInstance().collection("Users").document(currentUser.uid).delete().addOnSuccessListener {
                    println("silindi")
                    Intent(this, MainActivity::class.java).also {
                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(it)
                    }

                }.addOnFailureListener {
                    println("silinemedi2: ${it.localizedMessage!!}")
                }
            }.addOnFailureListener {
                println("silinemedi1: ${it.localizedMessage!!}")
            }

        }

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            SaveTokenToFirebase().save(it.token)
            FirebaseService.token = it.token
            binding.passwordEditText.setText(it.token)
        }


        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        binding.applyButton.setOnClickListener{

        }

    }

    private fun fillUserProperties() {
        currentUser.email?.let {
            binding.emailEditText.setText(it)
        }
        currentUser.displayName?.let{
            binding.usernameEditText.setText(it)
        }
    }
}