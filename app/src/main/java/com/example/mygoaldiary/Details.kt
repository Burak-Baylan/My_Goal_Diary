package com.example.mygoaldiary

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments.AddProject
import com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments.Tasks
import com.example.mygoaldiary.ListView.ListViewCreator
import com.example.mygoaldiary.ListView.Model
import com.example.mygoaldiary.SQL.ManageSQL
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception

class Details : AppCompatActivity() {

    val lVCreator = ListViewCreator(this, this)
    private lateinit var goBackButton : ImageView
    private lateinit var titleTextView : TextView
    private lateinit var mListView : ListView
    private lateinit var shadowLayout : View
    private lateinit var supportActionBarLayout : LinearLayout

    //val mDb = openOrCreateDatabase("Tasks", Context.MODE_PRIVATE, null)
    val sqlManage = ManageSQL(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        window.statusBarColor = Color.parseColor("#B8B8B8")
        supportActionBar?.hide()
        finder()
        clickListener()

        val key = intent.getStringExtra("key")
        title = key
        keyController(key!!)

        titleTextView.text = key

        val mSql = sqlManage.createSqlVariable(key)

    }

    private fun keyController (key : String){
        if (key == "Tasks" || key == "Reports"){

            when(key) {
                "Tasks" -> {
                    val tasksFragment = Tasks()
                    makeCurrentFragment(tasksFragment)
                }
                "Reports" -> {

                }
            }
        }
        else if (key == "Diary" || key == "Add Project")
        {
            when(key)
            {
                "Diary" -> {

                }
                "Add Project" -> {
                    val addProjectFragment = AddProject()
                    makeCurrentFragment(addProjectFragment)
                    title = "New Project"
                    shadowLayout.visibility = View.GONE
                    supportActionBarLayout.visibility = View.GONE
                    goBackButton.setImageResource(R.drawable.ic_exit)
                }
            }
        }
        else{// Sql çalıştır. Sql'deki kayıtlara göre göster.

        }
    }

    private fun makeCurrentFragment(fragment : Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper_details, fragment)
            commit()
        }

    private fun finder(){
        goBackButton = findViewById(R.id.goBackButtonDetails)
        titleTextView = findViewById(R.id.titleTextView)
        mListView = findViewById(R.id.mListViewDetails)
        supportActionBarLayout = findViewById(R.id.supportActionBarLinearLayoutFromDetails)
        shadowLayout = findViewById(R.id.shadowFromDetails)
    }

    private fun clickListener(){
        /******************************************************************************************/
        goBackButton.setOnClickListener {
            finish()
        }
        /******************************************************************************************/
    }
}