package com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.RecyclerView.HomeRecyclerViewAdapter
import com.example.mygoaldiary.RecyclerView.Model
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import java.util.*
import kotlin.collections.ArrayList


class Home() : Fragment() {

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
        catch (e: Exception){
            e.localizedMessage!!
        }

        items.add(Model("Add Project", R.drawable.ic_add, "", ""))
        val recyclerViewHere : RecyclerView = view.findViewById(R.id.mRecyclerView)

        val layoutManager = LinearLayoutManager(context!!)
        recyclerViewHere.layoutManager = layoutManager

        val adapter = HomeRecyclerViewAdapter(items)
        recyclerViewHere.adapter = adapter

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN).or(ItemTouchHelper.START).or(ItemTouchHelper.END), 0) {
            override fun onMove(recyclerView: RecyclerView, dragged: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val fromPosition = dragged.adapterPosition
                val toPosition = target.adapterPosition
                Collections.swap(items, fromPosition, toPosition)
                println("items size: ${items.size} position target: $toPosition positionDragged: $fromPosition")
                if (toPosition != 0 && toPosition != 1 && toPosition != 2 && toPosition != items.size - 1) {
                    if (fromPosition != 0 && fromPosition != 1 && fromPosition != 2 && fromPosition != items.size - 1) {
                        recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
                    }
                }
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        touchHelper.attachToRecyclerView(recyclerViewHere)
        return view
    }
}