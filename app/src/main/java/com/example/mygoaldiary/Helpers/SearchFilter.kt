package com.example.mygoaldiary.Helpers

import com.example.mygoaldiary.RecyclerView.HomeRecyclerViewAdapter
import com.example.mygoaldiary.Models.ModelHome
import java.util.ArrayList

class SearchFilter : MyHelpers() {

    companion object{
        fun homeSearchFilter(text: String, adapter : HomeRecyclerViewAdapter, items : ArrayList<ModelHome>){
            val filteredList : MutableList<ModelHome> = ArrayList()
            for(item in items){
                if (item.title.toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(item)
                }else if (item.yearDate.toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(item)
                }
            }
            adapter.filteredList(filteredList)
        }
    }

}