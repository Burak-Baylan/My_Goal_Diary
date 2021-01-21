package com.example.mygoaldiary

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass

class SignUpScreen : AppCompatActivity() {

    private lateinit var usernameEditText : EditText
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText
    private lateinit var signUpButton : Button
    private lateinit var loginButton : Button
    private lateinit var goBackButton : ImageView

    private lateinit var showAlert: ShowAlert
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var firebaseSuperClass: FirebaseSuperClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_screen)
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#B8B8B8")

        showAlert = ShowAlert(this)
        loadingDialog = LoadingDialog(this)
        firebaseSuperClass = FirebaseSuperClass(this, this)

        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signUpButton)
        loginButton = findViewById(R.id.loginButton)
        goBackButton = findViewById(R.id.goBackButtonSignUp)

        goBackButton.setOnClickListener {
            finish()
        }

        loginButton.setOnClickListener {
            finish()
        }

        signUpButton.setOnClickListener {
            if (usernameEditText.text.isNotEmpty() && emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                loadingDialog.startLoadingDialog()
                firebaseSuperClass.userAuthManage().addNewUser(usernameEditText.text.toString(), emailEditText.text.toString(), passwordEditText.text.toString())
            }
            else{
                showAlert.errorAlert(getString(R.string.error), getString(R.string.emailPasswordUsernameCannotEmpty), true)
            }
        }
    }
}