package com.example.mygoaldiary.Views

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.Notification.FirebaseService
import com.example.mygoaldiary.Notification.SaveTokenToFirebase
import com.example.mygoaldiary.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId

class LoginScreen : AppCompatActivity() {

    private lateinit var loginButton : Button
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText
    private lateinit var showAlert: ShowAlert
    private lateinit var signUpButton : Button
    private lateinit var forgotPasswordTv : TextView
    private lateinit var goBackButton : ImageView

    private var firebase = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var alert : android.app.AlertDialog.Builder

    private val firebaseSuperClass = FirebaseSuperClass(this, this)

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
        forgotPasswordTv = findViewById(R.id.forgotPasswordTv)
        goBackButton = findViewById(R.id.goBackButtonLogin)


        createForgotPasswordAlertDialog()

        goBackButton.setOnClickListener {
            finish()
        }

        showAlert = ShowAlert(this)

        loginButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                loadingDialog.startLoadingDialog()
                firebaseSuperClass.userAuthManage().login(emailEditText.text.toString(), passwordEditText.text.toString(), {loginSuccess()})
            }
            else{
                showAlert.errorAlert(getString(R.string.error), getString(R.string.emailPasswordCouldNotEmpty), true)
            }
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpScreen::class.java)
            startActivity(intent)
        }

        forgotPasswordTv.setOnClickListener {
            createForgotPasswordAlertDialog().show()
        }
    }

    private fun createForgotPasswordAlertDialog() : AlertDialog {

        val dialog : AlertDialog

        val view = this.layoutInflater.inflate(R.layout.layout_forgot_password, null)

        val builder = AlertDialog.Builder(this).apply {
            setView(view)
            setCancelable(false)
        }

        dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val emailEditText = view.findViewById<TextView>(R.id.emailEditTextFromForgotPassword)

        view.findViewById<Button>(R.id.sendForgotPasswordEmail).apply {
            setOnClickListener {
                if (emailEditText.text.isNotEmpty()) {
                    loadingDialog.startLoadingDialog()
                    firebaseSuperClass.userAuthManage().sendForgotPasswordEmail(emailEditText.text.toString())
                    dialog.dismiss()
                }
                else{
                    showAlert.errorAlert(getString(R.string.error), getString(R.string.emailShouldntEmpty), true)
                }
            }
        }
        view.findViewById<ImageView>(R.id.closeIv).apply {
            setOnClickListener{
                dialog.dismiss()
            }
        }
        return dialog
    }

    private fun loginSuccess(){
        val currentUser = auth.currentUser
        firebase.collection("Users").document(currentUser!!.uid).update("userPassword", passwordEditText.text.toString()).addOnSuccessListener {
            login()
        }.addOnFailureListener {
            showAlert.errorAlert("Error", "An error occurred. Please try again.", true)
        }
    }

    private fun login (){
        val i = Intent(this, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            SaveTokenToFirebase().save(it.token)
            FirebaseService.token = it.token
            startActivity(i)
        }
    }
}