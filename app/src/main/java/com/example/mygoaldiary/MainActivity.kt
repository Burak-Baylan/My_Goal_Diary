package com.example.mygoaldiary

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mygoaldiary.ListView.ListViewCreator
import com.example.mygoaldiary.ListView.Model
import com.example.mygoaldiary.R


open class MainActivity : AppCompatActivity() {

    lateinit var mListView : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lVCreator = ListViewCreator(this, this)

        val items = ArrayList<Model>()
        items.add(Model("Tasks", R.drawable.ic_tasks))
        items.add(Model("Reports", R.drawable.ic_notes_for_reports))
        items.add(Model("Diary", R.drawable.ic_diary))
        items.add(Model("Add Project", R.drawable.ic_add))

        val mListView = lVCreator.createListView(R.id.mListView, R.layout.row_list_view, items)

        mListView.setOnItemClickListener { parent, view, position, id ->
            val tvHere : TextView = view.findViewById(R.id.idTv) as TextView
            val intent = Intent(this, Details::class.java)
            intent.putExtra("key", tvHere.text.toString())
            startActivity(intent)
        }
    }

}