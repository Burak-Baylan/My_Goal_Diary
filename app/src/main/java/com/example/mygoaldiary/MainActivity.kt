package com.example.mygoaldiary

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments.Aaaa
import com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments.Home
import com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments.Social
import com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments.Suggestions
import com.example.mygoaldiary.SQL.SQLVariablesModel
import com.google.android.material.bottomnavigation.BottomNavigationView


open class MainActivity : AppCompatActivity() {

    private val homeFragment = Home()
    private val aaaFragments = Aaaa()
    private val socialFragment = Social()
    private val suggestionsFragment = Suggestions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        window.statusBarColor = Color.parseColor("#B8B8B8")

        val bottomNavBar : BottomNavigationView = findViewById(R.id.bottom_nav)
        val loginLayout : LinearLayout = findViewById(R.id.loginLayout)

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
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
        }
    }

    private fun makeCurrentFragment(fragment : Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
}