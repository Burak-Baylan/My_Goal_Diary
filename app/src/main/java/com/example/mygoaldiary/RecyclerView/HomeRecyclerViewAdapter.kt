package com.example.mygoaldiary.RecyclerView

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoaldiary.Creators.DeleteAlertDialog
import com.example.mygoaldiary.Creators.ParamsCreator
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Views.Details
import com.example.mygoaldiary.Helpers.ProjectHelpers.DeleteProject
import com.example.mygoaldiary.Models.ModelHome
import com.example.mygoaldiary.R
import kotlin.collections.ArrayList

class HomeRecyclerViewAdapter (var items: ArrayList<ModelHome>, val activity : Activity) : RecyclerView.Adapter<HomeRecyclerViewAdapter.Holder>(){

    class Holder (view : View) : RecyclerView.ViewHolder(view){
        var imageView : ImageView = view.findViewById(R.id.mImageViewFromListViewRow)
        var nameTextView : TextView = view.findViewById(R.id.nameTextViewFromListViewRow)
        var dateTextView : TextView = view.findViewById(R.id.dateTextViewFromListView)
        var mMainLayout : ConstraintLayout = view.findViewById(R.id.mMainLayoutFromRow)
        var cloudImage : ImageView = view.findViewById(R.id.projectCloudImage)
        val sqlAndCloudImageLayout : LinearLayout = view.findViewById(R.id.sqlAndCloudImageLayout)
    }

    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.projects_row, parent, false)
        showAlert = ShowAlert(context)
        itemsFull = ArrayList(items)
        homeItems = items
        return Holder(view)
    }

    companion object{
        lateinit var homeItems : ArrayList<ModelHome>
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
            sendToDetailsActivity(holder, position)
        }

        isDataHybrid(position, holder)

        controlPositionForLongClickListener(holder, position)
    }

    private fun isDataHybrid(position : Int, holder : Holder) {
        when (items[position].isHybrid) {
            "true" -> {
                holder.cloudImage.visibility = View.VISIBLE
                holder.sqlAndCloudImageLayout.visibility = View.VISIBLE
            }
            "false" -> {
                holder.cloudImage.visibility = View.INVISIBLE
                holder.sqlAndCloudImageLayout.visibility = View.VISIBLE
            }
            else -> {
                holder.sqlAndCloudImageLayout.visibility = View.INVISIBLE
            }
        }
    }

    private fun sendToDetailsActivity(holder : Holder, position : Int) {
        with(Intent(context, Details::class.java)){
            this.putExtra("key", holder.nameTextView.text.toString())
            this.putExtra("id", items[position].id)
            this.putExtra("projectUuid", items[position].projectUuid)
            this.putExtra("isHybrid", items[position].isHybrid)
            this.putExtra("lastInteraction", items[position].lastInteraction)
            this.putExtra("targetedDeadline", items[position].targetedDeadline)
            this.putExtra("projectColor", items[position].imgColor)
            this.putExtra("yearDate", items[position].yearDate)
            this.putExtra("timeDate", items[position].time)
            context.startActivity(this)
        }
    }

    private fun controlPositionForLongClickListener(holder : Holder, position : Int) {
        if (position != items.size-1) {
            holder.mMainLayout.setOnLongClickListener {
                createDeleteAlertDialog(position)
                true
            }
        }
        else{
            holder.mMainLayout.setOnLongClickListener { /* Do Nothing */ true }
        }
    }

    private lateinit var showAlert : ShowAlert

    override fun getItemCount(): Int {
        return items.size
    }

    private fun createDeleteAlertDialog (position : Int){
        val deleteProjectView = DeleteAlertDialog.apply {
            create(context, activity)
            titleText = activity.getString(R.string.youDeletingAProject)
            messageText = "${activity.getString(R.string.ifYouDeleteThis)} \"${items[position].title}\" ${activity.getString(R.string.ifYouDeleteYouCantGetBackProject)}"
            this.view.findViewById<CheckBox>(R.id.deleteInternetTooCheckBox).visibility = if (items[position].isHybrid == "false"){
                View.GONE
            }else{
                View.VISIBLE
            }
        }.show()

        deleteProjectView.findViewById<Button>(R.id.deleteWarningYesButton).setOnClickListener {
            DeleteProject(context, activity).delete(deleteProjectView, items[position].projectUuid!!, items[position].isHybrid!!)
        }
    }

    fun filteredList(filteredList : MutableList<ModelHome>){
        items = filteredList as ArrayList<ModelHome>
        notifyDataSetChanged()
    }
}