package com.example.mygoaldiary.RecyclerView

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoaldiary.Creators.CommentSheet
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.SocialHelpers.OptionsMenu
import com.example.mygoaldiary.Helpers.SocialHelpers.PostLiker
import com.example.mygoaldiary.Helpers.SocialHelpers.PostMarker
import com.example.mygoaldiary.Models.SocialModel
import com.example.mygoaldiary.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class SocialRecyclerViewAdapter(var items: ArrayList<SocialModel>, val activity: Activity) : RecyclerView.Adapter<SocialRecyclerViewAdapter.Holder>(){

    class Holder(view: View) : RecyclerView.ViewHolder(view){
        var ppImageView : CircleImageView = view.findViewById(R.id.ppIv)
        var usernameTv : TextView = view.findViewById(R.id.usernameTv)
        var emailTv : TextView = view.findViewById(R.id.emailTv)
        var categoryTv : TextView = view.findViewById(R.id.categoryTv)
        var commentTv : TextView = view.findViewById(R.id.commentTv)
        var dateTv : TextView = view.findViewById(R.id.dateTv)
        var likeButton : ImageView = view.findViewById(R.id.likeIc)
        var likeTextView : TextView = view.findViewById(R.id.likeTextView)
        var commentButton : ImageView = view.findViewById(R.id.commentIc)
        var shareButton : ImageView = view.findViewById(R.id.shareIc)
        var bookmarkIc : ImageView = view.findViewById(R.id.bookmarkIc)
        val commentsCountTv : TextView = view.findViewById(R.id.commentsTv)
        val optionsMenu: ImageView = view.findViewById(R.id.optionsMenu)
    }

    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_feed, parent, false)
        showAlert = ShowAlert(context)
        optionsMenu = OptionsMenu(context, activity)
        commentSheet = CommentSheet(context, activity)
        return Holder(view)
    }
    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser

    private lateinit var optionsMenu : OptionsMenu

    private val postMarker = PostMarker()

    private lateinit var commentSheet : CommentSheet

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.optionsMenu.setOnClickListener {
            optionsMenu.holder = holder
            optionsMenu.items = items
            optionsMenu.position = position
            optionsMenu.createOptionsMenu(holder.optionsMenu, items[position].userUuid)
        }

        with(holder.bookmarkIc) {
            this.visibility = View.VISIBLE

            this.setOnClickListener {
                if (currentUser != null)
                    postMarker.mark(items, position, holder)
                else
                    showAlert.infoAlert(activity.getString(R.string.info), "You must be logged in to mark a post", true)

            }
        }

        with(holder.likeButton){
            setOnClickListener {
                currentUser = auth.currentUser
                if (currentUser != null)
                    PostLiker().askLike(currentUser!!, items[position].postId, items[position].userUuid, items[position].comment, this)
                else
                    showAlert.infoAlert(activity.getString(R.string.info), "You must be logged in to like a post", true)

            }
        }

        holder.commentButton.setOnClickListener {
            currentUser = auth.currentUser
            if (currentUser != null){
                commentSheet.createSheet(items[position].postId, items[position].userUuid, items[position].comment)
            }else{
                showAlert.infoAlert(activity.getString(R.string.info), "If you want to join the chat, you must be logged in", true)
            }
        }

        getAvatar(holder, position)
        getLikeCount(holder, position)
        getCommentsCount(holder)
        getPostOwnerProperties(items[position].userUuid, holder, position)

        currentUser?.let{
            if (it.uid == items[position].userUuid) holder.bookmarkIc.visibility = View.INVISIBLE
            holder.likeButton.setColorFilter(Color.parseColor("#D1D1D1"))
            holder.bookmarkIc.setColorFilter(Color.parseColor("#D1D1D1"))
            checkIfLikeExists(holder, position)
            checkIfMarkExists(holder)
        }
    }

    private val firebase = FirebaseFirestore.getInstance()
    private fun getPostOwnerProperties(userId: String, holder: Holder, position: Int) {
        firebase.collection("Users").document(userId).addSnapshotListener { value, error ->
            if (error != null){
                /** HATA **/
            }else{
                if (value != null && value.exists()){
                    val userEmail = value["userEmail"] as String
                    val userName = value["userName"] as String
                    putUserProperties(holder, position, userEmail, userName)
                }else{
                    println("Adapter içindeki value boş ya da boş")
                }
            }
        }
    }

    private lateinit var postId : String
    private lateinit var date : Date
    private fun putUserProperties(holder: Holder, position: Int, userEmail: String, username: String){
        holder.commentTv.text = items[position].comment
        holder.categoryTv.text = "Category: ${items[position].category}"
        holder.emailTv.text = userEmail
        holder.usernameTv.text = username

        date = items[position].timeStamp.toDate()
        holder.dateTv.text =" ${date.day}/${date.month}/${date.year+1900} ${date.hours}:${date.minutes}:${date.seconds}"
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

    private fun getLikeCount(holder: Holder, position: Int) {
        val holderHere = holder
        postId = items[position].postId
        firebase.collection("Posts").document(postId).collection("Likes").addSnapshotListener { value, error ->
            if (error != null){
                holderHere.likeTextView.text = "NaN"
            }else{
                if (value != null) holderHere.likeTextView.text = value.count().toString()
            }
        }
    }

    private fun checkIfExists(imageView : ImageView, query : Query, trueColor : String){
        query.get().addOnSuccessListener {
            imageView.setColorFilter(Color.parseColor(
                    if (!it.isEmpty) trueColor
                    else "#D1D1D1"
            ))
        }.addOnFailureListener {

        }
    }

    private fun checkIfLikeExists(holder: Holder, position: Int) {
        val reference = firebase.collection("Posts").document(items[position].postId).collection("Likes").whereEqualTo("userId", currentUser!!.uid)
        checkIfExists(holder.likeButton, reference, "#32A852")
        
    }

    private fun checkIfMarkExists(holder: Holder) {
        val reference = firebase.collection("Users").document(currentUser!!.uid).collection("Marks").whereEqualTo("markedPost", postId)
        checkIfExists(holder.bookmarkIc, reference, "#4287F5")
    }

    private fun getCommentsCount(holder: Holder){
        firebase.collection("Posts").document(postId).collection("Comments").addSnapshotListener { value, error ->
            if (error != null) holder.commentTv.text = "NaN"
            else
                if (value != null)
                    holder.commentsCountTv.text = value.count().toString()

        }
    }

    private lateinit var showAlert : ShowAlert

    override fun getItemCount() = items.size
}