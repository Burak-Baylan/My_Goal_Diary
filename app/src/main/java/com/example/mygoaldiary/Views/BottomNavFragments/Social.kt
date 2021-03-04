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
        lateinit var items : ArrayList<SocialModel>

        var _binding : FragmentSocialBinding? = null
        val binding get() = _binding!!

        val getPost = GetPosts()

        private lateinit var backupActivity : Activity
    }

    private lateinit var addPostSheetView : View
    private lateinit var addPostSheet : AddPostSheet
    private lateinit var firebaseAuthClass : FirebaseAuthClass

    private val auth = FirebaseAuth.getInstance()

    private var currentUser : FirebaseUser? = null
    private var sharedPref : SharedPreferences? = null


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

        backupActivity = requireActivity()

        firebaseAuthClass = FirebaseAuthClass(requireContext(), requireActivity())

        addPostSheet = AddPostSheet(requireContext(), requireActivity())
        addPostSheetView = addPostSheet.createSheet()
        binding.goToAddPost.setOnClickListener {
            addPostSheetView = addPostSheet.createSheet()
            addPostSheet.show()
        }

        getPost.recyclerView = binding.socialRecyclerView
        getPost.loadingProgress = binding.postsLoadingProgress

        with(binding.socialRefreshLayout){
            this.setColorSchemeColors(Color.parseColor("#FFFFFF"))
            this.setProgressBackgroundColorSchemeColor(Color.parseColor("#F05454"))
            this.setOnRefreshListener {
                binding.socialRecyclerView.removeAllViews()
                items.clear()
                getAllPosts()
                this.isRefreshing = false
                adapter.notifyDataSetChanged()
            }
        }

        with(addPostSheetView.findViewById(R.id.categorySpinner) as SmartMaterialSpinner<*>){
            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        binding.filterIc.setOnClickListener { FilterSocialPosts().selectFilter(requireContext(), requireActivity()) }

        backPressListener()
        initializeRecyclerView()

        getAllPosts()

        return view
    }

    protected fun getAllPosts(){
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
        binding.socialRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.socialRecyclerView.adapter = adapter
    }

    val firebase = FirebaseFirestore.getInstance()

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