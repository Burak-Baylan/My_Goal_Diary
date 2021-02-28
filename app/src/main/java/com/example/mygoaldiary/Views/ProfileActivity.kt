package com.example.mygoaldiary.Views

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.InternetController
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.Views.ProfileViewPager.Marks
import com.example.mygoaldiary.Views.ProfileViewPager.MyPosts
import com.example.mygoaldiary.Views.ProfileViewPager.MyViewPagerAdapter
import com.example.mygoaldiary.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding

    companion object {
        var auth = FirebaseAuth.getInstance()
    }

    val currentUser = auth.currentUser!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#B8B8B8")

        binding.logoutLayout.setOnClickListener {
            logout()
        }

        binding.editProfileBtn.setOnClickListener {
            Intent(this, EditUserProfile::class.java).apply {
                startActivity(this)
            }
        }

        val adapter = MyViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(MyPosts(), "Posts")
        adapter.addFragment(Marks(), "Marks")
        binding.viewPager.adapter = adapter

        binding.tabs.setupWithViewPager(binding.viewPager)

        binding.goBackButtonProfile.setOnClickListener {finish()}
    }

    private val loadingDialog = LoadingDialog(this)
    private val showAlert = ShowAlert(this)
    private val firebase = FirebaseFirestore.getInstance()

    private fun logout() {
        // Kullanıcı hesabından çıkarken 'token'ı null'a çevirmemizin nedeni. Hesabında değilken veya başka hesaptayken önceki hesabından bildirim almamasını istememiz.
        if (InternetController.internetControl(this)) {
            loadingDialog.startLoadingDialog()
            firebase.collection("Users").document(currentUser.uid).update("notifyToken", null).addOnSuccessListener {
                out()
            }.addOnFailureListener {
                out()
            }
        }else{
            showAlert.errorAlert("Error", "You must be connected to internet to exit your account.", true)
        }
    }

    private fun out(){
        loadingDialog.dismissDialog()
        auth.signOut()
        finish()
    }

    override fun onResume() {
        getUserProperties()
        super.onResume()
    }
    private fun getUserProperties() {
        currentUser.photoUrl?.let { photoUrl ->
            Picasso.get().load(photoUrl).into(binding.ppIvProfile)
        }

        binding.usernameTexView.text = currentUser.displayName
    }
}