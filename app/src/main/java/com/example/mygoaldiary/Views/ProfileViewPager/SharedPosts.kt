package com.example.mygoaldiary.Views.ProfileViewPager

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygoaldiary.Helpers.SocialHelpers.GetPosts
import com.example.mygoaldiary.Models.SocialModel
import com.example.mygoaldiary.RecyclerView.SocialRecyclerViewAdapter
import com.example.mygoaldiary.databinding.FragmentMyPostsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SharedPosts : Fragment() {


    var _binding : FragmentMyPostsBinding? = null
    val binding get() = _binding!!

    private lateinit var items : ArrayList<SocialModel>
    private lateinit var adapter : SocialRecyclerViewAdapter

    private val getPosts = GetPosts()

    var userUuid : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        val view = binding.root

        GetPosts.lastTimeStamp = null

        initializeRecyclerView()

        binding.myPostsRefreshLayout.setColorSchemeColors(Color.parseColor("#FFFFFF"))
        binding.myPostsRefreshLayout.setProgressBackgroundColorSchemeColor(Color.parseColor("#F05454"))

        with(binding.myPostsRefreshLayout) {
            this.setOnRefreshListener {
                GetPosts.lastTimeStamp = null
                items.clear()
                binding.myPostsRecyclerView.removeAllViews()
                get()
                this.isRefreshing = false
            }
        }
        getPosts.recyclerView = binding.myPostsRecyclerView
        getPosts.loadingProgress = binding.myPostsLoadingProgress
        getPosts.noTextView = binding.noPostsTv
        getPosts.adapter = adapter

        get()

        return view
    }

    private fun get() = getPosts.get(items, firebase.collection("Posts").whereEqualTo("ownerUuid", userUuid))

    private val firebase = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun initializeRecyclerView() {
        items = ArrayList()
        adapter = SocialRecyclerViewAdapter(items, requireActivity())
        binding.myPostsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.myPostsRecyclerView.adapter = adapter
    }
}