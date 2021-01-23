package com.example.mygoaldiary

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments.Aaaa
import com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments.Home
import com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments.Social
import com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments.Suggestions
import com.example.mygoaldiary.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


open class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var BOTTOM_NAV : BottomNavigationView
    }

    private val homeFragment = Home()
    private val aaaFragments = Aaaa()
    private val socialFragment = Social()
    private val suggestionsFragment = Suggestions()

    private lateinit var showUsernameTv : TextView

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        BOTTOM_NAV = binding.bottomNav

        supportActionBar?.hide()

        window.statusBarColor = Color.parseColor("#B8B8B8")

        makeCurrentFragment(homeFragment)

        binding.bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.homeFromMenu -> makeCurrentFragment(homeFragment)
                R.id.aaaaFromMenu -> makeCurrentFragment(aaaFragments)
                R.id.socialFromMenu -> makeCurrentFragment(socialFragment)
                R.id.suggestionsFromMenu -> makeCurrentFragment(suggestionsFragment)
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment : Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
}