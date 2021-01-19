package com.example.mygoaldiary

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments.Aaaa
import com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments.Home
import com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments.Social
import com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments.Suggestions
import com.example.mygoaldiary.Helpers.InternetController
import com.example.mygoaldiary.Helpers.WordShortener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


open class MainActivity : AppCompatActivity() {

    private val homeFragment = Home()
    private val aaaFragments = Aaaa()
    private val socialFragment = Social()
    private val suggestionsFragment = Suggestions()

    private lateinit var showUsernameTv : TextView

    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        window.statusBarColor = Color.parseColor("#B8B8B8")

        val bottomNavBar : BottomNavigationView = findViewById(R.id.bottom_nav)
        val loginLayout : LinearLayout = findViewById(R.id.loginLayout)
        showUsernameTv= findViewById(R.id.showUsernameTextView)

        makeCurrentFragment(homeFragment)

        bottomNavBar.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.homeFromMenu -> makeCurrentFragment(homeFragment)
                R.id.aaaaFromMenu -> makeCurrentFragment(aaaFragments)
                R.id.socialFromMenu -> makeCurrentFragment(socialFragment)
                R.id.suggestionsFromMenu -> makeCurrentFragment(suggestionsFragment)
            }
            true
        }

        loginLayout.setOnClickListener {
            if (auth.currentUser == null) {
                val intent = Intent(this, LoginScreen::class.java)
                startActivity(intent)
            }
            else if (auth.currentUser != null) {
                /** Profile Gönder **/
            }
        }
    }

    var username : String? = "${R.string.login}"
    override fun onResume() {
        super.onResume()
        currentUser = auth.currentUser
        currentUser?.let {
            username = currentUser!!.displayName
        }
        if (username != "Login") {
            showUsernameTv.text = WordShortener.shorten(username!!, "...", 5, 0, 5)
        }
    }

    private fun makeCurrentFragment(fragment : Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
}