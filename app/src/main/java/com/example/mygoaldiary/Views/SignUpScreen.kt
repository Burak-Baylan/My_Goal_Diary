package com.example.mygoaldiary.Views

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.R
import com.example.mygoaldiary.databinding.ActivitySignUpScreenBinding

class SignUpScreen : AppCompatActivity() {


    private lateinit var showAlert: ShowAlert
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var firebaseSuperClass: FirebaseSuperClass

    private lateinit var binding : ActivitySignUpScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#B8B8B8")

        binding = ActivitySignUpScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        showAlert = ShowAlert(this)
        loadingDialog = LoadingDialog(this)
        firebaseSuperClass = FirebaseSuperClass(this, this)

        binding.goBackButtonSignUp.setOnClickListener {
            finish()
        }

        binding.loginButton.setOnClickListener {
            finish()
        }

        binding.signUpButton.setOnClickListener {
            if (binding.usernameEditText.text .isNotEmpty() && binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()){
                loadingDialog.startLoadingDialog()
                firebaseSuperClass.userAuthManage().addNewUser(binding.usernameEditText.text.toString(), binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
            }
            else{
                showAlert.errorAlert(getString(R.string.error), getString(R.string.emailPasswordUsernameCannotEmpty), true)
            }
        }
    }
}