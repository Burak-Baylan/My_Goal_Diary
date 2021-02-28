package com.example.mygoaldiary.Helpers.SocialHelpers

import android.os.DropBoxManager
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mygoaldiary.Models.ModelHome
import com.example.mygoaldiary.Models.SocialModel
import com.example.mygoaldiary.Views.BottomNavFragments.Social
import com.example.mygoaldiary.Views.ProfileViewPager.MyPosts
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.lang.ref.Reference

class GetPosts{

    var recyclerView : RecyclerView? = null
    var loadingProgress : SpinKitView? = null
    var noTextView : TextView? = null

    val firebase = FirebaseFirestore.getInstance()


    fun get (items : ArrayList<SocialModel>, referenceQuery : Query){

        println("Girik")

        recyclerView?.visibility = View.GONE
        loadingProgress?.visibility = View.VISIBLE
        noTextView?.visibility = View.GONE

        referenceQuery.get().addOnSuccessListener { value ->
            if (value != null && !value.isEmpty) {
                recyclerView?.removeAllViews()
                items.clear()
                val documents = value.documents

                var category : String
                var comment : String
                var currentDate : String
                var currentTime : String
                var ownerUuid : String
                var timeStamp : Timestamp
                var postId : String

                for ((i, docs) in documents.withIndex()) {
                    //println("forda")
                    category = docs.get("category") as String
                    comment = docs.get("comment") as String
                    currentDate = docs.get("currentDate") as String
                    currentTime = docs.get("currentTime") as String
                    ownerUuid = docs.get("ownerUuid") as String
                    timeStamp = docs.get("timeStamp") as Timestamp
                    postId = docs.get("postId") as String

                    items.add(SocialModel(
                            ownerUuid, category, comment, currentDate, currentTime, timeStamp, postId
                    ))


                    if (i == documents.size - 1) {
                        recyclerView?.visibility = View.VISIBLE
                        loadingProgress?.visibility = View.GONE
                    }
                }
            }else noTextVisible()
        }.addOnFailureListener {
            println("hata: ${it.localizedMessage!!}")
        }
    }

    private fun noTextVisible() {
        recyclerView?.visibility = View.GONE
        loadingProgress?.visibility = View.GONE
        noTextView?.visibility = View.VISIBLE
    }
}