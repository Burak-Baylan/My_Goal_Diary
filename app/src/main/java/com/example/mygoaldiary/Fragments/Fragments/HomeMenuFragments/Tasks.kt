package com.example.mygoaldiary.Fragments.Fragments.HomeMenuFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mygoaldiary.ListView.ListViewCreator
import com.example.mygoaldiary.ListView.Model
import com.example.mygoaldiary.R

class Tasks : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)

        val lVCreator = ListViewCreator(context!!, activity!!)

        /*val items = ArrayList<Model>()
        items.add(Model("Daily", R.drawable.ic_sunny_for_day))
        items.add(Model("Weekly", R.drawable.ic_notes_for_reports))
        items.add(Model("Monthly", R.drawable.ic_notes_for_reports))
        items.add(Model("Yearly", R.drawable.ic_diary))*/
        return view
    }
}