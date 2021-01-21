package com.example.mygoaldiary

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.FirebaseManage.FirebaseSuperClass

class LoginScreen : AppCompatActivity() {

    private lateinit var loginButton : Button
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText
    private lateinit var showAlert: ShowAlert
    private lateinit var signUpButton : Button
    private lateinit var forgotPasswordTv : TextView
    private lateinit var goBackButton : ImageView

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
        view.findViewById<Button>(R.id.cancelForgotPasswordEmail).apply {
            setOnClickListener{
                dialog.dismiss()
            }
        }

        return dialog
    }

    private fun loginSuccess(){
        /** Arkada açık olan tüm Activityleri kapat ve mainActivity'i tekrar başlat. **/
        val i = Intent(this, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }
}