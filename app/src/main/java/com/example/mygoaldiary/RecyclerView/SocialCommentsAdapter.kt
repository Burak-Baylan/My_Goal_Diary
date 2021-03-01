package com.example.mygoaldiary.RecyclerView

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.SocialHelpers.CommentLiker
import com.example.mygoaldiary.Models.SocialCommentsModel
import com.example.mygoaldiary.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class SocialCommentsAdapter (var items: ArrayList<SocialCommentsModel>, val activity : Activity) : RecyclerView.Adapter<SocialCommentsAdapter.Holder>(){

    class Holder (view : View) : RecyclerView.ViewHolder(view){
        var ppImageView : CircleImageView = view.findViewById(R.id.ppIvComment)
        var commentTv : TextView = view.findViewById(R.id.commentTvComment)
        var usernameTv : TextView = view.findViewById(R.id.usernameTvComment)
        var emailTv : TextView = view.findViewById(R.id.emailTvComment)
        var likeButton : ImageView = view.findViewById(R.id.likeIcComment)
        var likeTv : TextView = view.findViewById(R.id.likeTextViewCount)
        var dateTv : TextView = view.findViewById(R.id.dateTvComment)
    }

    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_comments, parent, false)
        showAlert = ShowAlert(context)
        return Holder(view)
    }

    private val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.commentTv.text = items[position].comment

        val date = items[position].timeStamp.toDate()
        holder.dateTv.text = "${date.day}/${date.month}/${date.year+1900} ${date.hours}:${date.minutes}:${date.seconds}"

        getLikeCount(holder, position)

        with(holder.likeButton){
            setOnClickListener {
                if (currentUser != null) CommentLiker().askLike(currentUser, items[position].postId, items[position].commentId, this)
            }
        }
        getUserProperties(items[position].userUuid, holder, position)

        getAvatar(holder, position)

    }


    private var link : String? = null
    private fun getAvatar(holder: Holder, position: Int) {
        firebase.collection("Users").document(items[position].userUuid).get().addOnSuccessListener {
            link = it["avatarLink"] as String?
            if (it != null && link != null){
                Picasso.get().load(link).resize(100,100).into(holder.ppImageView)
            }else{
                holder.ppImageView.setImageResource(R.drawable.background_for_sheet)
                holder.ppImageView.setColorFilter(Color.parseColor("#EFEFEF"))
            }
        }.addOnFailureListener {

        }
    }

    private val firebase = FirebaseFirestore.getInstance()
    private fun getUserProperties(userId : String, holder : Holder, position : Int) {
        firebase.collection("Users").document(userId).addSnapshotListener { value, error ->
            if (error != null){
                /** HATA **/
            }else{
                if (value != null && value.exists()){
                    val userEmail = value["userEmail"] as String
                    val userName = value["userName"] as String
                    putUserProperties(holder, position, userEmail, userName)
                }else{
                    println("User props error")
                }
            }
        }
    }

    private fun getLikeCount(holder : Holder, position: Int){
        val reference = firebase.collection("Posts").document(items[position].postId).collection("Comments")
        reference.document(items[position].commentId).collection("Likes").addSnapshotListener { value, error ->
            if (error != null){
                holder.likeTv.text = "NaN"
            }else{
                if (value != null){
                    holder.likeTv.text = value.count().toString()
                }
            }
        }
    }

    private fun putUserProperties(holder : Holder, position: Int, userEmail : String, username : String){
        holder.commentTv.text = items[position].comment
        holder.emailTv.text = userEmail
        holder.usernameTv.text = username
    }

    private lateinit var showAlert : ShowAlert

    override fun getItemCount(): Int {
        return items.size
    }
}