package com.example.mygoaldiary.Views

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.mygoaldiary.Views.HomeMenuFragments.AddProject
import com.example.mygoaldiary.Views.HomeMenuFragments.Tasks
import com.example.mygoaldiary.Views.HomeMenuFragments.UserProjects.UserProjects
import com.example.mygoaldiary.Helpers.ShortenWord
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.databinding.ActivityDetailsBinding

class Details : AppCompatActivity() {

    private lateinit var binding : ActivityDetailsBinding

    val sqlManage = ManageSQL(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window.statusBarColor = Color.parseColor("#B8B8B8")
        supportActionBar?.hide()

        binding.goBackButtonDetails.setOnClickListener {
            finish()
        }

        key = intent.getStringExtra("key")!!

        projectId = intent.getStringExtra("id")

        with(intent) {
            this.getStringExtra("lastInteraction")?.let {
                lastInteraction = it
            }

            this.getStringExtra("targetedDeadline")?.let {
                targetedDeadline = it
            }

            this.getStringExtra("projectUuid")?.let {
                projectUuid = it
            }

            this.getStringExtra("isHybrid")?.let {
                projectIsHybrid = it
            }

            this.getIntExtra("projectColor", 0).let {
                projectColor = it.toString()
            }

            this.getStringExtra("yearDate")?.let {
                date = it
            }

            this.getStringExtra("timeDate")?.let {
                time = it
            }
        }

        keyController(key)
        ShortenWord.shorten(key, "...", 15, 0, 15, binding.titleTextView)
    }

    companion object{
        var key = ""
        var projectId : String? = null
        var projectUuid : String = ""
        var lastInteraction : String? = null
        var targetedDeadline : String? = null
        var projectIsHybrid : String? = null
        var projectColor : String? = null
        var time : String? = null
        var date : String? = null
    }

    private fun keyController (key : String){
        if (projectId == "Diary" || projectId == "Add Project" || projectId == "Tasks" || projectId == "Reports") {
            when(key) {
                "Tasks" -> { makeCurrentFragment(Tasks()) }
                "Add Project" -> {
                    makeCurrentFragment(AddProject())
                    supportActionBarHider()
                }
            }
        } else{
            makeCurrentFragment(UserProjects())
            supportActionBarHider()
        }
    }

    private fun makeCurrentFragment(fragment : Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper_details, fragment)
            commit()
        }

    private fun supportActionBarHider(){
        binding.supportActionBarLinearLayoutFromDetails.visibility = View.GONE
    }
}