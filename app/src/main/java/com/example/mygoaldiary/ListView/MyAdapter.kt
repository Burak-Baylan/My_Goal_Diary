package com.example.mygoaldiary.ListView

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.mygoaldiary.R
import de.hdodenhof.circleimageview.CircleImageView


class MyAdapter(var ctx: Context, var resource: Int, var items: ArrayList<Model>, var act: Activity) : ArrayAdapter<Model>(ctx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(ctx)
        val view = layoutInflater.inflate(resource, null)

        val descTv : TextView = view.findViewById(R.id.nameTextViewFromListViewRow)
        val mImage : ImageView = view.findViewById(R.id.mImageViewFromListViewRow)
        val dateTextView : TextView = view.findViewById(R.id.dateTextViewFromListView)



        val typeFace = resource

        /*val params = LinearLayout.LayoutParams(40, 40)
        mImage.layoutParams = params*/

        descTv.text = items[position].title
        mImage.setImageResource(items[position].imgColor)
        dateTextView.text = items[position].yearDate

        return view
    }

}