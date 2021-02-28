package com.example.mygoaldiary.Views.BottomNavFragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.FirebaseManage.FirebaseAuthClass
import com.example.mygoaldiary.Helpers.InternetController
import com.example.mygoaldiary.Helpers.MyHelpers
import com.example.mygoaldiary.Helpers.ProjectHelpers.GetProjects
import com.example.mygoaldiary.Helpers.ProjectHelpers.GetProjectsFromFb
import com.example.mygoaldiary.Helpers.ShortenWord
import com.example.mygoaldiary.Views.LoginScreen
import com.example.mygoaldiary.R
import com.example.mygoaldiary.RecyclerView.HomeRecyclerViewAdapter
import com.example.mygoaldiary.Models.ModelHome
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.Views.ProfileActivity
import com.example.mygoaldiary.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import kotlin.collections.ArrayList


open class Home : Fragment() {

    companion object{
        private var _binding : FragmentHomeBinding? = null
        protected val binding get() = _binding!!
        private lateinit var items : ArrayList<ModelHome>
        private lateinit var adapter : HomeRecyclerViewAdapter
        private lateinit var sqlManage : ManageSQL
        private lateinit var backupContext : Context
        private lateinit var backupActivity : Activity

        lateinit var homeRecyclerView : RecyclerView
    }

    private lateinit var showAlert : ShowAlert

    private val auth = FirebaseAuth.getInstance()

    private var isSearchOpen = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        backupContext = requireContext()
        backupActivity = requireActivity()
        sqlManage = ManageSQL(requireContext(), requireActivity())
        showAlert = ShowAlert(requireContext())
        items = ArrayList()
        adapter = HomeRecyclerViewAdapter(items, requireActivity())
        binding.mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.mRecyclerView.adapter = adapter

        backPressListener()

        with(binding){
            this.searchImageView.setOnClickListener { openSearchView() }
            this.backButtonFromSearch.setOnClickListener { closeSearchView() }
            this.loginLayout.setOnClickListener { loginControl() }

            this.getAllProjects.setOnClickListener {
                if (InternetController.internetControl(requireActivity())) {
                    showAlert.warningAlert("Warning", "If you get your projects from the cloud, your current projects and tasks will be deleted.", true).apply {
                        this.setOnClickListener {
                            ShowAlert.mAlertDialog.dismiss()
                            GetProjectsFromFb(requireContext(), requireActivity()).get()
                        }
                    }
                }else{
                    showAlert.errorAlert("Error", "Internet connection is required to get you projects.", true)
                }
            }

            this.searchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    MyHelpers.filter().homeSearchFilter(s.toString(), adapter, items)
                }
            })

            homeRecyclerView = this.mRecyclerView
        }

        with(binding.homeRefreshLayout){
            this.setColorSchemeColors(Color.parseColor("#FFFFFF"))
            this.setProgressBackgroundColorSchemeColor(Color.parseColor("#F05454"))
            this.setOnRefreshListener { refresh() }
        }

        return view
    }

    private fun backPressListener() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isSearchOpen) {
                    closeSearchView()
                } else {
                    requireActivity().finish()
                }
            }
        })
    }

    private fun openSearchView() {
        isSearchOpen = true
        binding.searchLayout.visibility = View.VISIBLE
        binding.homeLinearLayout.visibility = View.GONE
        UIUtil.showKeyboard(requireActivity(), binding.searchEditText)
    }

    private fun closeSearchView() {
        isSearchOpen = false
        binding.searchLayout.visibility = View.GONE
        binding.homeLinearLayout.visibility = View.VISIBLE
        UIUtil.hideKeyboard(backupActivity)
        binding.searchEditText.text.clear()
    }

    protected fun refresh(){
        items.clear()
        adapter.notifyDataSetChanged()
        GetProjects.getProjects(sqlManage, items, binding.mRecyclerView)
        binding.homeRefreshLayout.isRefreshing = false
        closeSearchView()
    }

    override fun onStart() {
        super.onStart()
        refresh()
    }

    val currentUser = auth.currentUser
    override fun onResume() {
        super.onResume()
        val username = auth.currentUser.let {
            if (it == null) {
                binding.homePpIv.setImageResource(R.drawable.ic_user)
                binding.getAllProjects.visibility = View.GONE
                "Login"
            }else {
                Picasso.get().load(it.photoUrl).into(binding.homePpIv)
                binding.getAllProjects.visibility = View.VISIBLE
                it.displayName
            }
        }
        binding.showUsernameTextView.text = username
        if (username != "Login") {
            if (username != null)
                ShortenWord.shorten(username, "...", 5, 0, 5, binding.showUsernameTextView)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun loginControl(){
        requireActivity().startActivity(
                if (auth.currentUser == null)
                    Intent(requireActivity(), LoginScreen::class.java)
                else
                    Intent(requireActivity(), ProfileActivity::class.java)
        )
    }
}