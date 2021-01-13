package com.example.mygoaldiary.ListView

import android.app.Activity
import android.content.Context
import android.widget.ListView
import com.example.mygoaldiary.MainActivity

class ListViewCreator (private val ctx : Context, private val activity : Activity) : MainActivity(){

    fun createListView (listView : ListView, listViewRowId : Int, items : ArrayList<Model>) : ListView{
        listView.adapter = MyAdapter(ctx, listViewRowId, items, activity)
        return listView
    }
}