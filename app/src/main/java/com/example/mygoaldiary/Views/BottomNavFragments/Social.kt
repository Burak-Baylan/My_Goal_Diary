package com.example.mygoaldiary.Views.BottomNavFragments

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.example.mygoaldiary.Creators.BottomSheets.AddPostSheet
import com.example.mygoaldiary.FirebaseManage.FirebaseAuthClass
import com.example.mygoaldiary.Helpers.SocialHelpers.FilterSocialPosts
import com.example.mygoaldiary.Helpers.SocialHelpers.GetPosts
import com.example.mygoaldiary.Models.SocialModel
import com.example.mygoaldiary.Views.MainActivity
import com.example.mygoaldiary.R
import com.example.mygoaldiary.RecyclerView.SocialRecyclerViewAdapter
import com.example.mygoaldiary.databinding.FragmentSocialBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

open class Social : Fragment() {

    companion object{
        private lateinit var adapter : SocialRecyclerViewAdapter
        private lateinit var backupActivity : Activity
        lateinit var items : ArrayList<SocialModel>
        var _binding : FragmentSocialBinding? = null
        val binding get() = _binding!!
        val getPost = GetPosts()
    }

    private lateinit var addPostSheetView : View
    private lateinit var addPostSheet : AddPostSheet
    private lateinit var firebaseAuthClass : FirebaseAuthClass
    private val auth = FirebaseAuth.getInstance()
    private var currentUser : FirebaseUser? = null
    private var sharedPref : SharedPreferences? = null
    val firebase = FirebaseFirestore.getInstance()


    override fun onResume() {
        currentUser = auth.currentUser
        currentUser?.let {
            binding.showUsernameTextView.text = currentUser!!.displayName
            currentUser!!.photoUrl?.let{
                Picasso.get().load(it).into(binding.socialPpIv)
            }
        }
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        val view = binding.root
        GetPosts.lastTimeStamp = null

        backupActivity = requireActivity()
        firebaseAuthClass = FirebaseAuthClass(requireContext(), requireActivity())
        addPostSheet = AddPostSheet(requireContext(), requireActivity())
        addPostSheetView = addPostSheet.createSheet()

        binding.goToAddPost.setOnClickListener {
            addPostSheetView = addPostSheet.createSheet()
            addPostSheet.show()
        }

        with(binding.socialRefreshLayout){
            this.setColorSchemeColors(Color.parseColor("#FFFFFF"))
            this.setProgressBackgroundColorSchemeColor(Color.parseColor("#F05454"))
            this.setOnRefreshListener {
                GetPosts.lastTimeStamp = null
                getAllPosts(false)
                binding.socialRecyclerView.removeAllViews()
                items.clear()
                this.isRefreshing = false
                adapter.notifyDataSetChanged()
            }
        }

        backPressListener()
        initializeRecyclerView()

        getPost.recyclerView = binding.socialRecyclerView
        getPost.loadingProgress = binding.postsLoadingProgress
        getPost.adapter = adapter

        binding.socialRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    //getAllPosts()
                    sharedPref = backupActivity.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
                    val sharedValue = sharedPref!!.getString("socialFilter", "All")
                    if (sharedValue == "All") {
                        getPost.get(items, firebase.collection("Posts").orderBy("timeStamp", Query.Direction.DESCENDING))
                    }else{
                        getPost.get(items, firebase.collection("Posts").whereEqualTo("category", sharedValue).orderBy("timeStamp", Query.Direction.DESCENDING))
                    }
                }
            }
        })

        binding.filterIc.setOnClickListener { FilterSocialPosts().selectFilter(requireContext(), requireActivity()) }

        getAllPosts(true)

        return view
    }

    protected fun getAllPosts(clear : Boolean){
        if (clear){
            binding.socialRecyclerView.removeAllViews()
            items.clear()
            GetPosts.lastTimeStamp = null
        }
        sharedPref = backupActivity.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val sharedValue = sharedPref!!.getString("socialFilter", "All")
        if (sharedValue == "All") {
            getPost.get(items, firebase.collection("Posts").orderBy("timeStamp", Query.Direction.DESCENDING))
        }else{
            getPost.get(items, firebase.collection("Posts").whereEqualTo("category", sharedValue).orderBy("timeStamp", Query.Direction.DESCENDING))
        }
    }

    private fun initializeRecyclerView() {
        items = ArrayList()
        adapter = SocialRecyclerViewAdapter(items, requireActivity())
        items.clear()
        binding.socialRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.socialRecyclerView.removeAllViews()
        binding.socialRecyclerView.adapter = adapter
    }

    fun makeCurrentFragment(fragment : Fragment) =
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fl_wrapper, fragment)
                commit()
            }

    protected open fun backPressListener() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                makeCurrentFragment(Home())
                MainActivity.BOTTOM_NAV.selectedItemId = R.id.homeFromMenu
            }
        })
    }
}