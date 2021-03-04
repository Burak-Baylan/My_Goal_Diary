package com.example.mygoaldiary.Views

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.InternetController
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.Views.ProfileViewPager.Marks
import com.example.mygoaldiary.Views.ProfileViewPager.SharedPosts
import com.example.mygoaldiary.Views.ProfileViewPager.MyViewPagerAdapter
import com.example.mygoaldiary.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding

    companion object {
        var auth = FirebaseAuth.getInstance()
    }

    val currentUser = auth.currentUser!!
    var userUuid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#B8B8B8")

        val getUserUuid = intent.getStringExtra("userUuid")
        userUuid = getUserUuid ?: currentUser.uid

        createTabs()

        binding.tabs.setupWithViewPager(binding.viewPager)
        binding.goBackButtonProfile.setOnClickListener {finish()}
    }

    private fun createTabs() {

        val sharedPosts = SharedPosts()
        sharedPosts.userUuid = userUuid

        val marks = Marks()
        marks.userUuid = userUuid

        val adapter = MyViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(sharedPosts, "Posts")
        adapter.addFragment(marks, "Marks")
        binding.viewPager.adapter = adapter
    }

    private fun myAccount() {
        currentUser.photoUrl?.let { photoUrl ->
            Picasso.get().load(photoUrl).into(binding.ppIvProfile)
        }
        binding.usernameTexView.text = currentUser.displayName

        binding.logoutLayout.setOnClickListener {
            logout()
        }
        binding.editProfileBtn.setOnClickListener {
            Intent(this, EditUserProfile::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun notMyAccount(){
        println("ZOBORİİİİK: ${intent.getStringExtra("username")} / ${intent.getStringExtra("ppUrl")}")
        binding.editProfileBtn.visibility = View.INVISIBLE
        binding.logoutLayout.visibility = View.INVISIBLE
        binding.usernameTexView.text = intent.getStringExtra("username")
        Picasso.get().load(intent.getStringExtra("ppUrl")).into(binding.ppIvProfile)
    }

    private val loadingDialog = LoadingDialog(this)
    private val showAlert = ShowAlert(this)
    private var firebase = FirebaseFirestore.getInstance()

    private fun logout() {
        // Kullanıcı hesabından çıkarken 'token'ı null'a çevirmemizin nedeni. Hesabında değilken veya başka hesaptayken önceki hesabından bildirim almamasını istememiz.
        firebase = FirebaseFirestore.getInstance()
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
        intent.getBooleanExtra("accountControl", true).apply {
            if (this){
                myAccount()
            }else{
                notMyAccount()
            }
        }
    }
}