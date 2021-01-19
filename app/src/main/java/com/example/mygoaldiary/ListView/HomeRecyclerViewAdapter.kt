package com.example.mygoaldiary.ListView

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoaldiary.Details
import com.example.mygoaldiary.R

class HomeRecyclerViewAdapter (var items: ArrayList<Model>) : RecyclerView.Adapter<HomeRecyclerViewAdapter.Holder>() {

    class Holder (view : View) : RecyclerView.ViewHolder(view){
        var imageView : ImageView = view.findViewById(R.id.mImageViewFromListViewRow)
        var nameTextView : TextView = view.findViewById(R.id.nameTextViewFromListViewRow)
        var dateTextView : TextView = view.findViewById(R.id.dateTextViewFromListView)
        var mMainLayout : ConstraintLayout = view.findViewById(R.id.mMainLayoutFromRow)
    }

    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_list_view, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.nameTextView.text = items[position].title
        holder.imageView.setImageResource(items[position].imgColor)
        holder.dateTextView.text = items[position].yearDate
        holder.mMainLayout.setOnClickListener {
            val intent = Intent(context, Details::class.java)
            intent.putExtra("key", holder.nameTextView.text.toString())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}