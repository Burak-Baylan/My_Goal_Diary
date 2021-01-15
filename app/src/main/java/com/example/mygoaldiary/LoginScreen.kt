package com.example.mygoaldiary

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.mygoaldiary.ComponentCreator.ShowAlert
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass.Companion.activity
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass.Companion.context

open class LoginScreen : AppCompatActivity() {

    private lateinit var loginButton : Button
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText
    private lateinit var showAlert: ShowAlert
    private lateinit var signUpButton : Button

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#B8B8B8")

        loadingDialog = LoadingDialog(this)

        loginButton = findViewById(R.id.loginButton)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signUpButton)

        showAlert = ShowAlert(this)

        loginButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                loadingDialog.startLoadingDialog()
                FirebaseSuperClass().apply {
                    context = this@LoginScreen
                    activity = this@LoginScreen
                }.userAuthManage().login(emailEditText.text.toString(), passwordEditText.text.toString())
            }
            else{
                showAlert.errorAlert("Error", "Email and Password cannot be empty.", true)
            }
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpScreen::class.java)
            startActivity(intent)
        }

    }
}