package com.example.mygoaldiary.RecyclerView

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoaldiary.Creators.BottomSheets.ReportPostSheet
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.SocialHelpers.CommentLiker
import com.example.mygoaldiary.Helpers.SocialHelpers.PostDelete
import com.example.mygoaldiary.Models.SocialCommentsModel
import com.example.mygoaldiary.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
        var deleteCommentIv : ImageView = view.findViewById(R.id.deleteCommentIv)
        var reportCommentIv : ImageView = view.findViewById(R.id.reportCommentIv)
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
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.commentTv.text = items[position].comment
        setPpImageSrcWithGrayColor(holder)
        val date = items[position].timeStamp.toDate()
        holder.dateTv.text = "${date.day}/${date.month}/${date.year+1900} ${date.hours}:${date.minutes}:${date.seconds}"

        getLikeCount(holder, position)
        getUserProperties(items[position].userUuid, holder, position)
        getAvatar(holder, position)

        with(holder.likeButton){
            setOnClickListener {
                if (currentUser != null) {
                    firebase.collection("Posts").document(items[position].postId).get().addOnSuccessListener {
                        if (it.exists() && it != null) {
                            CommentLiker(activity).askLike(currentUser, items[position].userUuid, items[position].postId, items[position].commentId, this, it["comment"] as String)
                        }
                    }.addOnFailureListener {}
                }
            }
        }

        currentUser?.let {
            checkIfLikeExists(holder, position)
        }
    }

    private var link : String? = null
    private fun getAvatar(holder: Holder, position: Int) {
        firebase.collection("Users").document(items[position].userUuid).get().addOnSuccessListener {
            link = it["avatarLink"] as String?
            if (it != null && link != null){
                Picasso.get().load(link).resize(100,100).into(holder.ppImageView)
            }else{
                setPpImageSrcWithGrayColor(holder)
            }
        }.addOnFailureListener {

        }
    }

    private fun setPpImageSrcWithGrayColor(holder : Holder){
        holder.ppImageView.setImageResource(R.drawable.background_for_sheet)
        holder.ppImageView.setColorFilter(Color.parseColor("#EFEFEF"))
    }

    private val firebase = FirebaseFirestore.getInstance()
    private fun getUserProperties(userId : String, holder : Holder, position : Int) {
        firebase.collection("Users").document(userId).addSnapshotListener { value, error ->
            if (error != null){
                putUserProperties(holder, position, "...", "...")
            }else{
                if (value != null && value.exists()){
                    val userEmail = value["userEmail"] as String
                    val userName = value["userName"] as String
                    putUserProperties(holder, position, userEmail, userName)
                }else{
                    putUserProperties(holder, position, "...", "...")
                }
            }
        }
    }

    private fun getLikeCount(holder : Holder, position: Int){
        val reference = firebase.collection("Posts").document(items[position].postId).collection("Comments")
        reference.document(items[position].commentId).collection("Likes").addSnapshotListener { value, error ->
            holder.likeTv.text =
                    if (error != null) "NaN"
                    else value?.count()?.toString() ?: "NaN"
        }
    }

    private fun putUserProperties(holder : Holder, position: Int, userEmail : String, username : String){
        holder.commentTv.text = items[position].comment
        holder.emailTv.text = userEmail
        holder.usernameTv.text = username
        if (currentUser != null) {
            if (items[position].userUuid == currentUser.uid) {
                holder.reportCommentIv.visibility = View.INVISIBLE
                holder.deleteCommentIv.visibility = View.VISIBLE
            }else{
                holder.reportCommentIv.visibility = View.VISIBLE
                holder.deleteCommentIv.visibility = View.INVISIBLE
            }
        }else{
            holder.reportCommentIv.visibility = View.VISIBLE
            holder.deleteCommentIv.visibility = View.INVISIBLE
        }

        holder.reportCommentIv.setOnClickListener {
            ReportPostSheet(context, activity).createSheet(username, null, items, position)
        }
        holder.deleteCommentIv.setOnClickListener {
            PostDelete.delete(context, activity, items[position].postId, items[position].commentId)
        }
    }

    private lateinit var showAlert : ShowAlert

    override fun getItemCount(): Int {
        return items.size
    }

    private fun checkIfLikeExists(holder: Holder, position: Int) {
        val reference = firebase.collection("Posts").document(items[position].postId).collection("Comments").document(items[position].commentId).collection("Likes").whereEqualTo("ownerId", currentUser!!.uid)
        checkIfExists(holder.likeButton, reference)
    }

    private fun checkIfExists(imageView : ImageView, query : Query){
        query.get().addOnSuccessListener {
            imageView.setColorFilter(Color.parseColor(
                    if (!it.isEmpty) "#32A852"
                    else "#D1D1D1"
            ))
        }.addOnFailureListener {

        }
    }
}