package com.example.mygoaldiary.Views

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.InternetController
import com.example.mygoaldiary.Helpers.SendFeedback
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.R
import com.example.mygoaldiary.Views.ProfileViewPager.Marks
import com.example.mygoaldiary.Views.ProfileViewPager.SharedPosts
import com.example.mygoaldiary.Views.ProfileViewPager.MyViewPagerAdapter
import com.example.mygoaldiary.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

open class ProfileActivity : AppCompatActivity() {

    companion object {
        var auth = FirebaseAuth.getInstance()
    }
    private lateinit var binding : ActivityProfileBinding
    private lateinit var sendFeedback: SendFeedback
    val currentUser = auth.currentUser
    var userUuid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#B8B8B8")

        val getUserUuid = intent.getStringExtra("userUuid")
        userUuid = getUserUuid ?: currentUser?.uid

        createTabs()
        sendFeedback = SendFeedback()

        binding.tabs.setupWithViewPager(binding.viewPager)
        binding.goBackButtonProfile.setOnClickListener {finish()}
        binding.feedbackLayout.setOnClickListener { sendFeedback.show(this, this) }
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
        currentUser!!.photoUrl?.let { photoUrl ->
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
        binding.editProfileBtn.visibility = View.INVISIBLE
        binding.logoutLayout.visibility = View.INVISIBLE
        binding.feedbackLayout.visibility = View.INVISIBLE
        binding.usernameTexView.text = intent.getStringExtra("username")
        Picasso.get().load(intent.getStringExtra("ppUrl")).into(binding.ppIvProfile)
    }

    protected var loadingDialog = LoadingDialog(this)
    protected var showAlert = ShowAlert(this)
    protected var firebase = FirebaseFirestore.getInstance()

    private fun logout() {
        // Kullanıcı hesabından çıkarken 'token'ı null'a çevirmemizin nedeni. Hesabında değilken veya başka hesaptayken önceki hesabından bildirim almamasını istememiz.
        firebase = FirebaseFirestore.getInstance()
        if (InternetController.internetControl(this)) {
            loadingDialog.startLoadingDialog()
            firebase.collection("Users").document(currentUser!!.uid).update("notifyToken", null).addOnSuccessListener {
                out()
            }.addOnFailureListener {
                out()
            }
        }else{
            showAlert.errorAlert(getString(R.string.error), getString(R.string.internetRequiredToExit), true)
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
                if (currentUser != null) {
                    myAccount()
                }else{
                    notMyAccount()
                }
            }else{
                notMyAccount()
            }
        }
    }
}
