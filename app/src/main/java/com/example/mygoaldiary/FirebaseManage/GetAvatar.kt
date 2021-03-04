package com.example.mygoaldiary.FirebaseManage

import android.graphics.Color
import com.example.mygoaldiary.Models.SocialModel
import com.example.mygoaldiary.R
import com.example.mygoaldiary.RecyclerView.SocialRecyclerViewAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.ArrayList

class GetAvatar {
    companion object {
        private val firebase = FirebaseFirestore.getInstance()
        fun getAvatar(items : ArrayList<SocialModel>, position : Int, holder : SocialRecyclerViewAdapter.Holder) {
            firebase.collection("Users").document(items[position].userUuid).get().addOnSuccessListener {
                val link = it["avatarLink"] as String?
                if (it != null && link != null) {
                    Picasso.get().load(link).resize(100, 100).into(holder.ppImageView)
                } else {
                    holder.ppImageView.setImageResource(R.drawable.background_for_sheet)
                    holder.ppImageView.setColorFilter(Color.parseColor("#EFEFEF"))
                }
            }.addOnFailureListener {}
        }
    }
}