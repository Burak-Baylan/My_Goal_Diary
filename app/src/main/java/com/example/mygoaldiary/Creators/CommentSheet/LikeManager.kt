package com.example.mygoaldiary.Creators.CommentSheet

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import com.example.mygoaldiary.Helpers.SocialHelpers.PostLiker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class LikeManager (likeContext : Context, likeActivity : Activity): CommentSheet(likeContext, likeActivity){

    private var auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser!!

    fun listenLikeCount(){
        firebase.collection("Posts").document(postId).collection("Likes").addSnapshotListener { value, error ->
            if (error != null){
                // Error
            }else{
                if (value != null){
                    likeTextView.text = "${value.count()}"
                }else{
                    likeTextView.text = "NaN"
                }
            }
        }
    }

    private lateinit var mQuery : Query
    fun checkIfExists(imageView : ImageView, query : Query?, trueColor : String){
        mQuery = query ?: firebase.collection("Posts").document(postId).collection("Likes").whereEqualTo("userId", currentUser.uid)
        mQuery.get().addOnSuccessListener {
            imageView.setColorFilter(Color.parseColor(
                    if (!it.isEmpty) trueColor
                    else "#D1D1D1"
            ))
        }.addOnFailureListener {}
    }

    fun like() = PostLiker().askLike(currentUser, postId, mOwnerId, mComment!!, likeButton)
}