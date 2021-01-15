package com.example.mygoaldiary

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.mygoaldiary.ComponentCreator.ShowAlert
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass.Companion.activity
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass.Companion.context

class SignUpScreen : AppCompatActivity() {

    private lateinit var usernameEditText : EditText
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText
    private lateinit var signUpButton : Button
    private lateinit var loginButton : Button

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
        firebaseSuperClass = FirebaseSuperClass()

        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signUpButton)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            finish()
        }

        signUpButton.setOnClickListener {
            if (usernameEditText.text.isNotEmpty() && emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                loadingDialog.startLoadingDialog()
                firebaseSuperClass.apply {
                    context = this@SignUpScreen
                    activity = this@SignUpScreen
                }.userAuthManage().addNewUser(usernameEditText.text.toString(), emailEditText.text.toString(), passwordEditText.text.toString())
            }
            else{
                showAlert.errorAlert("Error", "Email, Password and Username cannot be empty.", true)
            }
        }

    }
}