package com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments

import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.baoyz.swipemenulistview.SwipeMenu
import com.baoyz.swipemenulistview.SwipeMenuCreator
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.baoyz.swipemenulistview.SwipeMenuListView
import com.baoyz.widget.PullRefreshLayout
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Details
import com.example.mygoaldiary.ListView.ListViewCreator
import com.example.mygoaldiary.ListView.Model
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.SQL.SQLVariablesModel


class Home : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var showAlert : ShowAlert

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val sqlManage = ManageSQL(context!!, activity!!)

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val lVCreator = ListViewCreator(context!!, activity!!)

        val items = ArrayList<Model>()

        showAlert = ShowAlert(context!!)

        items.add(Model("Tasks", R.drawable.ic_tasks, "", ""))
        items.add(Model("Reports", R.drawable.ic_notes_for_reports, "", ""))
        items.add(Model("Diary", R.drawable.ic_diary, "", ""))

        val mSql = sqlManage.createSqlVariable("HomePage")
        sqlManage.tableCreator(mSql, "allUserProjectDeneme2", "title VARCHAR, projectColor INT, yearDate VARCHAR, time VARCHAR")

        try {
            val cursor = mSql?.rawQuery("SELECT * FROM allUserProjectDeneme2", null)
            while (cursor!!.moveToNext()) {

                val title = cursor.getString(cursor.getColumnIndex("title"))
                val projectColor = cursor.getInt(cursor.getColumnIndex("projectColor"))
                val yearDate = cursor.getString(cursor.getColumnIndex("yearDate"))
                val time = cursor.getString(cursor.getColumnIndex("time"))
                items.add(Model(title, projectColor, yearDate, time))
            }
        }
        catch (e : Exception){
            e.localizedMessage!!
        }

        items.add(Model("Add Project", R.drawable.ic_add, "", ""))
        val lvHere : ListView = view.findViewById(R.id.mListView45)

        val mListView = lVCreator.createListView(
            lvHere,
            R.layout.row_list_view,
            items
        )

        mListView.setOnItemClickListener { parent, viewHere, position, id ->
            val tvHere : TextView = viewHere.findViewById(R.id.nameTextViewFromListViewRow) as TextView
            val intent = Intent(context, Details::class.java)
            intent.putExtra("key", tvHere.text.toString())
            startActivity(intent)
        }

        mListView.setOnItemLongClickListener { parent, view, position, id ->
            if (position != 0 && position != 1 && position != 2 && position != mListView.size){
                println(mListView.size)
                showAlert.errorAlert(R.string.error, R.string.userCreateFail, true)
            }
            true
        }

        return view
    }
}