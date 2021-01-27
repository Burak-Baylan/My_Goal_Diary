package com.example.mygoaldiary

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments.AddProject
import com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments.Tasks
import com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments.UserProjects
import com.example.mygoaldiary.Helpers.ShortenWord
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.databinding.ActivityDetailsBinding

class Details : AppCompatActivity() {

    private lateinit var binding : ActivityDetailsBinding

    //val mDb = openOrCreateDatabase("Tasks", Context.MODE_PRIVATE, null)
    val sqlManage = ManageSQL(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window.statusBarColor = Color.parseColor("#B8B8B8")
        supportActionBar?.hide()
        clickListener()

        key = intent.getStringExtra("key")!!
        projectId = intent.getStringExtra("id")
        val getProjectUuid = intent.getStringExtra("projectUuid")

        if (getProjectUuid != null){
            projectUuid = getProjectUuid
        }

        keyController(key)
        ShortenWord.shorten(key, "...", 15, 0, 15, binding.titleTextView)
    }

    companion object{
        var key = ""
        var projectId : String? = null
        var projectUuid : String = ""
    }

    private fun keyController (key : String){
        if (projectId == "Diary" || projectId == "Add Project" || projectId == "Tasks" || projectId == "Reports")
        {
            when(key)
            {
                "Tasks" -> { makeCurrentFragment(Tasks()) }
                "Reports" -> {}
                "Diary" -> { }
                "Add Project" -> {
                    makeCurrentFragment(AddProject())
                    supportActionBarHider()
                }
            }
        }
        else{// Sql'deki kayıtlara göre göster
            val userProject = UserProjects()
            makeCurrentFragment(userProject)
            supportActionBarHider()
        }
    }

    private fun makeCurrentFragment(fragment : Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper_details, fragment)
            commit()
        }

    private fun clickListener(){
        binding.goBackButtonDetails.setOnClickListener {
            finish()
        }
    }

    private fun supportActionBarHider(){
        binding.shadowFromDetails.visibility = View.GONE
        binding.supportActionBarLinearLayoutFromDetails.visibility = View.GONE
    }
}