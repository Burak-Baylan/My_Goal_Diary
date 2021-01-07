package com.example.mygoaldiary.ListView

import android.app.Activity
import android.content.Context
import android.widget.ListView
import com.example.mygoaldiary.MainActivity

class ListViewCreator (val ctx : Context, val activity : Activity) : MainActivity(){

    fun createListView (listViewId : Int, listViewRowId : Int, items : ArrayList<Model>) : ListView{
        val listView : ListView = activity.findViewById(listViewId)
        listView.adapter = MyAdapter(ctx, listViewRowId, items, activity)
        return listView
    }
}