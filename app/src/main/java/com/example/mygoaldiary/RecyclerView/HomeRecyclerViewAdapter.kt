package com.example.mygoaldiary.RecyclerView

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoaldiary.Creators.ParamsCreator
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Details
import com.example.mygoaldiary.R
import kotlin.collections.ArrayList

class HomeRecyclerViewAdapter (var items: ArrayList<ModelHome>) : RecyclerView.Adapter<HomeRecyclerViewAdapter.Holder>(){

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
        val view = inflater.inflate(R.layout.projects_row, parent, false)
        showAlert = ShowAlert(context)

        itemsFull = ArrayList(items)

        return Holder(view)
    }


    lateinit var itemsFull : ArrayList<ModelHome>

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.nameTextView.setTextColor(Color.parseColor(items[position].textViewColor))
        holder.nameTextView.setTypeface(null, items[position].typeFace)
        holder.nameTextView.text = items[position].title

        holder.imageView.setImageResource(items[position].imgColor)
        holder.imageView.layoutParams = ParamsCreator().constraintLayoutLayoutParamsCreator(items[position].imageViewSize, items[position].imageViewSize)

        holder.dateTextView.text = items[position].yearDate

        holder.mMainLayout.setOnClickListener {
            val intent = Intent(context, Details::class.java)
            intent.putExtra("key", holder.nameTextView.text.toString())
            intent.putExtra("id", items[position].id)
            context.startActivity(intent)
        }

        if (position != 0 && position != 1 && position != 2 && position != items.size-1) {
            holder.mMainLayout.setOnLongClickListener {
                showAlert.errorAlert("Error", "Error", true)
                true
            }
        }
        else{
            holder.mMainLayout.setOnLongClickListener {
                // Do Nothing
                true
            }
        }
    }



    private lateinit var showAlert : ShowAlert

    override fun getItemCount(): Int {
        return items.size
    }

    fun filteredList(filteredList : MutableList<ModelHome>){
        items = filteredList as ArrayList<ModelHome>
        notifyDataSetChanged()
    }
}