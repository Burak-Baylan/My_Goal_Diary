package com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.FirebaseManage.FirebaseAuthClass
import com.example.mygoaldiary.Helpers.MyHelpers
import com.example.mygoaldiary.Helpers.ShortenWord
import com.example.mygoaldiary.LoginScreen
import com.example.mygoaldiary.R
import com.example.mygoaldiary.RecyclerView.HomeRecyclerViewAdapter
import com.example.mygoaldiary.RecyclerView.ModelHome
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil


class Home : Fragment() {

    private lateinit var showAlert : ShowAlert
    private lateinit var sqlManage : ManageSQL
    private lateinit var items : ArrayList<ModelHome>
    private lateinit var adapter : HomeRecyclerViewAdapter

    private val auth = FirebaseAuth.getInstance()

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var isSearchOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isSearchOpen) {
                    closeSearchView()
                } else {
                    requireActivity().finish()
                }
            }
        })

        sqlManage = ManageSQL(requireContext(), requireActivity())
        showAlert = ShowAlert(requireContext())
        items = ArrayList()

        binding.searchImageView.setOnClickListener { openSearchView() }

        binding.backButtonFromSearch.setOnClickListener { closeSearchView() }

        binding.loginLayout.setOnClickListener { loginControl() }

        getProjects(items)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.mRecyclerView.layoutManager = layoutManager

        adapter = HomeRecyclerViewAdapter(items)
        binding.mRecyclerView.adapter = adapter

        refreshLayout()

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                MyHelpers.filter().homeSearchFilter(s.toString(), adapter, items)
            }
        })

        binding.homeRefreshLayout.setColorSchemeColors(Color.parseColor("#FFFFFF"))
        binding.homeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.parseColor("#F05454"))

        return view
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
        UIUtil.hideKeyboard(requireActivity())
        binding.searchEditText.text.clear()
    }

    private fun refreshLayout() {
        binding.homeRefreshLayout.setOnRefreshListener {
            refresh()
        }
    }

    private fun refresh(){
        items.clear()
        adapter.notifyDataSetChanged()
        getProjects(items)
        adapter.notifyDataSetChanged()
        binding.homeRefreshLayout.isRefreshing = false
        closeSearchView()
    }

    override fun onStart() {
        super.onStart()
        refresh()
    }

    override fun onResume() {
        super.onResume()
        var username: String? = getString(R.string.login)
        FirebaseAuthClass(requireContext(), requireActivity()).getCurrentUser().let {
            username = it?.displayName
        }
        if (username != "Login") {
            if (username != null) {
                ShortenWord.shorten(username!!, "...", 5, 0, 5, binding.showUsernameTextView)
            }
        }
    }

    @SuppressLint("Recycle")
    private fun getProjects(items: ArrayList<ModelHome>){
        items.add(ModelHome("Tasks", "Tasks", R.drawable.ic_tasks, "", "", "#000000", Typeface.NORMAL, 50, null))
        items.add(ModelHome("Reports", "Reports", R.drawable.ic_notes_for_reports, "", "", "#000000", Typeface.NORMAL, 50, null))
        items.add(ModelHome("Diary", "Diary", R.drawable.ic_diary, "", "", "#000000", Typeface.NORMAL, 50, null))

        val mSql = sqlManage.createSqlVariable("HomePage").apply {
            val sqlString = "id INTEGER PRIMARY KEY, projectUuid VARCHAR, title TEXT, projectColor INTEGER, yearDate TEXT, time TEXT, lastInteraction, targetedDeadline"
            sqlManage.tableCreator(this, "allUserProjectDeneme3", sqlString)
        }

        try {
            val cursor = mSql?.rawQuery("SELECT * FROM allUserProjectDeneme3", null)
            while (cursor!!.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex("id"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val projectColor = cursor.getInt(cursor.getColumnIndex("projectColor"))
                val yearDate = cursor.getString(cursor.getColumnIndex("yearDate"))
                val time = cursor.getString(cursor.getColumnIndex("time"))
                val projectUuid = cursor.getString(cursor.getColumnIndex("projectUuid"))
                items.add(
                    ModelHome(id, title, projectColor, yearDate, time, "#000000", Typeface.NORMAL, 40, projectUuid)
                )
            }
        }
        catch (e: Exception){
            e.localizedMessage!!
        }
        items.add(ModelHome("Add Project", "Add Project", R.drawable.ic_add, "", "", "#F05454", Typeface.BOLD, 50, null))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun loginControl(){
        if (auth.currentUser == null) {
            val intent = Intent(requireActivity(), LoginScreen::class.java)
            requireActivity().startActivity(intent)
        }
        else if (auth.currentUser != null) {
            /** Profile Gönder **/
        }
    }
}