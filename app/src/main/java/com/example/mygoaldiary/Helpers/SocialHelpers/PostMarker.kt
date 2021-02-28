package com.example.mygoaldiary.Helpers.SocialHelpers

import android.graphics.Color
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.Models.SocialModel
import com.example.mygoaldiary.RecyclerView.SocialRecyclerViewAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PostMarker {

    private val firebase = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    private lateinit var mHolder : SocialRecyclerViewAdapter.Holder
    private lateinit var postId : String
    private lateinit var markReference : DocumentReference


    fun mark(items : ArrayList<SocialModel>, position: Int, holder : SocialRecyclerViewAdapter.Holder){
        this.mHolder = holder
        this.postId = items[position].postId
        this.markReference = firebase.collection("Users").document(currentUser!!.uid).collection("Marks").document(postId)
        checkIfExists()
    }

    private fun checkIfExists(){
        val referenceHere = firebase.collection("Users").document(currentUser!!.uid).collection("Marks").whereEqualTo("markedPost", postId)
        referenceHere.get().addOnSuccessListener {
            if (it.isEmpty) {
                markedColor()
                mark()
            }else{
                unmarkedColor()
                getBackMark()
            }
        }.addOnFailureListener {
            println("Check if exists error: ${it.localizedMessage!!}")
        }
    }

    private fun markedColor() = mHolder.bookmarkIc.setColorFilter(Color.parseColor("#4287f5"))
    private fun unmarkedColor() = mHolder.bookmarkIc.setColorFilter(Color.parseColor("#D1D1D1"))

    private fun getBackMark() {
        markReference.delete().addOnSuccessListener {
            println("Mark deleted")
        }.addOnFailureListener {
            markedColor()
            println("Mark delete fail: ${it.localizedMessage!!}")
        }
    }

    private fun mark(){
        val markHashMap = hashMapOf(
                "markedPost" to postId,
                "date" to GetCurrentDate.getDate(),
                "time" to GetCurrentDate.getTime(),
                "timeStamp" to Timestamp.now(),
                "userId" to currentUser!!.uid
        )
        markReference.set(markHashMap).addOnSuccessListener {
            println("Kaydedildi")
        }.addOnFailureListener {
            unmarkedColor()
            println("Mark fail: ${it.localizedMessage}")
        }
    }
}