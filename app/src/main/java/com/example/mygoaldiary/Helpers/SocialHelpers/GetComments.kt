package com.example.mygoaldiary.Helpers.SocialHelpers

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoaldiary.Creators.CommentSheet.CommentSheet
import com.example.mygoaldiary.Models.SocialCommentsModel
import com.example.mygoaldiary.Views.BottomNavFragments.Social
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class GetComments: Social(){


    lateinit var loadingView : SpinKitView
    lateinit var recyclerView : RecyclerView
    lateinit var noCommentsTv : TextView

    fun get (items : ArrayList<SocialCommentsModel>, postId : String){
        loadingVisible()
        firebase.collection("Posts").document(postId).collection("Comments").orderBy("timeStamp", Query.Direction.DESCENDING).get().addOnSuccessListener { value ->
            if (value != null && !value.isEmpty) {
                CommentSheet.commentsRecyclerView.removeAllViews()
                items.clear()
                val documents = value.documents
                for ((i, docs) in documents.withIndex()) {
                    val comment = docs.get("comment") as String
                    val commentUuid = docs.get("commentUuid") as String
                    val date = docs.get("date") as String
                    val time = docs.get("time") as String
                    val ownerId = docs.get("ownerId") as String
                    val timeStamp = docs.get("timeStamp") as Timestamp
                    items.add(SocialCommentsModel(ownerId, comment, time, date, timeStamp, commentUuid, postId))
                    if (i >= documents.size - 1) recyclerViewVisible()
                }
            }else allInvisible()
        }.addOnFailureListener {
            recyclerViewVisible()
        }
    }

    private fun loadingVisible(){
        loadingView.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
        noCommentsTv.visibility = View.INVISIBLE
    }
    private fun recyclerViewVisible(){
        loadingView.visibility = View.INVISIBLE
        recyclerView.visibility = View.VISIBLE
        noCommentsTv.visibility = View.INVISIBLE
    }
    private fun allInvisible(){
        loadingView.visibility = View.INVISIBLE
        recyclerView.visibility = View.INVISIBLE
        noCommentsTv.visibility = View.VISIBLE
    }
}