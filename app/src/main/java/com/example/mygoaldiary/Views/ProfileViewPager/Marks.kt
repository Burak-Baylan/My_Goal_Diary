package com.example.mygoaldiary.Views.ProfileViewPager

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Models.SocialModel
import com.example.mygoaldiary.RecyclerView.SocialRecyclerViewAdapter
import com.example.mygoaldiary.databinding.FragmentMarksBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Marks : Fragment() {


    var _binding : FragmentMarksBinding? = null
    val binding get() = _binding!!

    private lateinit var items: ArrayList<SocialModel>
    private lateinit var adapter: SocialRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMarksBinding.inflate(inflater, container, false)
        val view = binding.root


        with(binding.marksRefreshLayout) {
            this.setColorSchemeColors(Color.parseColor("#FFFFFF"))
            this.setProgressBackgroundColorSchemeColor(Color.parseColor("#F05454"))
            this.setOnRefreshListener {
                get()
                this.isRefreshing = false
            }
        }

        showAlert = ShowAlert(requireContext())

        initializeRecyclerView()
        get()

        return view
    }

    private fun get(){
        binding.marksRecyclerView.removeAllViews()
        items.clear()

        binding.marksRecyclerView.visibility = View.INVISIBLE
        binding.marksLoadingProgress.visibility = View.VISIBLE
        binding.noMarksTv.visibility = View.INVISIBLE

        firebase.collection("Users").document(currentUser!!.uid).collection("Marks").orderBy("timeStamp", Query.Direction.DESCENDING).get().addOnSuccessListener { value1 ->
            if (value1 != null && !value1.isEmpty) {
                val documents1 = value1.documents

                for ((i, docs) in documents1.withIndex()){
                    getPostDetails(docs)

                    if (i == documents1.size - 1) {
                        binding.marksRecyclerView.visibility = View.VISIBLE
                        binding.marksLoadingProgress.visibility = View.GONE
                    }

                }
            }else noCommentsVisible()
        }.addOnFailureListener {
            println("hata like: ${it.localizedMessage!!}")
        }
    }

    private var category : String? = null
    private var comment : String? = null
    private var currentDate : String? = null
    private var currentTime : String? = null
    private var ownerUuid : String? = null
    private var timeStamp : Timestamp? = null
    private var postId : String? = null

    private lateinit var showAlert : ShowAlert

    private fun getPostDetails(docs : DocumentSnapshot){
        firebase.collection("Posts").document(docs["markedPost"] as String).get().addOnSuccessListener { value2 ->
            if (value2 != null) {
                try {
                    category = value2.get("category") as String
                    comment = value2.get("comment") as String
                    currentDate = value2.get("currentDate") as String
                    currentTime = value2.get("currentTime") as String
                    ownerUuid = value2.get("ownerUuid") as String
                    timeStamp = value2.get("timeStamp") as Timestamp
                    postId = value2.get("postId") as String

                    items.add(
                        SocialModel(ownerUuid!!, category, comment!!, currentDate!!, currentTime!!, timeStamp!!, postId!!)
                    )
                    adapter.notifyDataSetChanged()
                }
                catch (e : Exception){
                }
            }else noCommentsVisible()
        }.addOnFailureListener {

        }
    }

    private fun noCommentsVisible() {
        binding.noMarksTv.visibility = View.VISIBLE
        binding.marksRecyclerView.visibility = View.INVISIBLE
        binding.marksLoadingProgress.visibility = View.INVISIBLE
    }

    private val firebase = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    private fun initializeRecyclerView() {
        items = ArrayList()
        adapter = SocialRecyclerViewAdapter(items, requireActivity())
        binding.marksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.marksRecyclerView.adapter = adapter
    }
}