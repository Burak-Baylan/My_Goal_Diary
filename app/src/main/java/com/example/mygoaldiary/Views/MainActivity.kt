package com.example.mygoaldiary.Views

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mygoaldiary.Creators.CommentSheet
import com.example.mygoaldiary.Views.BottomNavFragments.Home
import com.example.mygoaldiary.Views.BottomNavFragments.Social
import com.example.mygoaldiary.R
import com.example.mygoaldiary.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


open class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var BOTTOM_NAV : BottomNavigationView
    }

    private val homeFragment = Home()

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

        intentController()

        binding.bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.homeFromMenu -> makeCurrentFragment(Home())
                R.id.socialFromMenu -> makeCurrentFragment(Social())
            }
            true
        }
    }

    private fun intentController() {
        val comment = intent.getStringExtra("comment")
        val ownerId = intent.getStringExtra("ownerId")
        val postId = intent.getStringExtra("postId")

        if (postId != null && ownerId != null){
            makeCurrentFragment(Social())
            binding.bottomNav.selectedItemId = R.id.socialFromMenu
            CommentSheet(this, this).createSheet(postId, ownerId, comment)
        }
    }

    private fun makeCurrentFragment(fragment : Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
}