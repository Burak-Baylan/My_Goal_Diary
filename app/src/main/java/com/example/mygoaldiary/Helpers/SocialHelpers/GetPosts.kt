package com.example.mygoaldiary.Helpers.SocialHelpers

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoaldiary.Models.SocialModel
import com.example.mygoaldiary.RecyclerView.SocialRecyclerViewAdapter
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class GetPosts{

    companion object {
        var lastTimeStamp: Timestamp? = null
    }

    private lateinit var startAfter : QuerySnapshot
    private lateinit var mQuery : Query
    private lateinit var mItems : ArrayList<SocialModel>
    lateinit var adapter : SocialRecyclerViewAdapter
    var recyclerView : RecyclerView? = null
    var loadingProgress : SpinKitView? = null
    var noTextView : TextView? = null
    val firebase = FirebaseFirestore.getInstance()
    private var haveAPost = false


    fun get (items : ArrayList<SocialModel>, referenceQuery : Query){
        this.mItems = items
        if (lastTimeStamp == null){
            mQuery = referenceQuery
                    .limit(10)
            getter()
        }else{
            firebase.collection("Posts").whereEqualTo("timeStamp", lastTimeStamp).get().addOnSuccessListener {
                val lastVisible = it.documents[it.size() - 1]
                startAfter = it
                mQuery = referenceQuery
                        .startAfter(lastVisible)
                        .limit(10)
                getter()
            }
        }
    }

    private fun getter() {
        haveAPost = false
        loadingProgress!!.visibility = View.VISIBLE
        recyclerView!!.isNestedScrollingEnabled = false
        mQuery.get().addOnSuccessListener { value ->
            if (value != null && !value.isEmpty) {
                haveAPost = true
                val documents = value.documents

                var category: String
                var comment: String
                var currentDate: String
                var currentTime: String
                var ownerUuid: String
                var timeStamp: Timestamp? = null
                var postId: String

                for (docs in documents) {
                    category = docs.get("category") as String
                    comment = docs.get("comment") as String
                    currentDate = docs.get("currentDate") as String
                    currentTime = docs.get("currentTime") as String
                    ownerUuid = docs.get("ownerUuid") as String
                    timeStamp = docs.get("timeStamp") as Timestamp
                    postId = docs.get("postId") as String

                    mItems.add(SocialModel(
                            ownerUuid, category, comment, currentDate, currentTime, timeStamp, postId
                    ))

                    adapter.notifyDataSetChanged()
                }
                lastTimeStamp = timeStamp
                loadMoreViews()
            } else {
                if (!haveAPost) {
                    noTextVisible()
                }
                loadMoreViews()
            }
        }.addOnFailureListener {
            println("hata: ${it.localizedMessage!!}")
        }
    }

    private fun loadMoreViews(){
        loadingProgress!!.visibility = View.GONE
        recyclerView!!.visibility = View.VISIBLE
        recyclerView!!.isNestedScrollingEnabled = true
    }

    private fun noTextVisible() {
        recyclerView?.visibility = View.GONE
        loadingProgress?.visibility = View.GONE
        noTextView?.visibility = View.VISIBLE
    }
}