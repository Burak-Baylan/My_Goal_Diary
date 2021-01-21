package com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.MyHelpers
import com.example.mygoaldiary.LoginScreen
import com.example.mygoaldiary.RecyclerView.HomeRecyclerViewAdapter
import com.example.mygoaldiary.RecyclerView.Model
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import kotlin.collections.ArrayList


class Home : Fragment() {

    private lateinit var showAlert : ShowAlert
    private lateinit var sqlManage : ManageSQL
    private lateinit var items : ArrayList<Model>
    private lateinit var adapter : HomeRecyclerViewAdapter

    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        sqlManage = ManageSQL(context!!, activity!!)
        showAlert = ShowAlert(context!!)
        items = ArrayList()

        binding.searchImageView.setOnClickListener {
            openSearchView()
        }

        binding.backButtonFromSearch.setOnClickListener {
            closeSearchView()
        }

        binding.loginLayout.setOnClickListener {
            loginControl()
        }

        getProjects(items)

        val layoutManager = LinearLayoutManager(context!!)
        binding.mRecyclerView.layoutManager = layoutManager

        adapter = HomeRecyclerViewAdapter(items)
        binding.mRecyclerView.adapter = adapter

        refreshLayout()

        binding.searchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }
        })

        return view
    }

    private fun openSearchView() {
        binding.searchLayout.visibility = View.VISIBLE
        binding.homeLinearLayout.visibility = View.GONE
        UIUtil.showKeyboard(activity!!, binding.searchEditText)
    }

    private fun closeSearchView() {
        binding.searchLayout.visibility = View.GONE
        binding.homeLinearLayout.visibility = View.VISIBLE
        UIUtil.hideKeyboard(activity!!)
        binding.searchEditText.text.clear()
    }

    private fun filter(text : String){
        val filteredList : MutableList<Model> = ArrayList()
        for(item in items){
            if (item.title.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item)
            }
        }

        adapter.filteredList(filteredList)
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
        var username = getString(R.string.login)
        currentUser = auth.currentUser
        currentUser?.let {
            username = currentUser!!.displayName!!
        }
        if (username != "Login") {
            MyHelpers.wordShortener().shorten(username, "...", 5, 0, 5, binding.showUsernameTextView)
        }
    }

    @SuppressLint("Recycle")
    private fun getProjects(items : ArrayList<Model>){
        items.add(Model("Tasks", R.drawable.ic_tasks, "", "", "#000000", Typeface.NORMAL, 50))
        items.add(Model("Reports", R.drawable.ic_notes_for_reports, "", "", "#000000", Typeface.NORMAL, 50))
        items.add(Model("Diary", R.drawable.ic_diary, "", "", "#000000", Typeface.NORMAL, 50))

        val mSql = sqlManage.createSqlVariable("HomePage").apply {
            sqlManage.tableCreator(this, "allUserProjectDeneme2", "title VARCHAR, projectColor INT, yearDate VARCHAR, time VARCHAR")
        }

        try {
            val cursor = mSql?.rawQuery("SELECT * FROM allUserProjectDeneme2", null)
            while (cursor!!.moveToNext()) {
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val projectColor = cursor.getInt(cursor.getColumnIndex("projectColor"))
                val yearDate = cursor.getString(cursor.getColumnIndex("yearDate"))
                val time = cursor.getString(cursor.getColumnIndex("time"))
                items.add(Model(title, projectColor, yearDate, time, "#000000", Typeface.NORMAL, 40))
            }
        }
        catch (e: Exception){
            e.localizedMessage!!
        }

        items.add(Model("Add Project", R.drawable.ic_add, "", "", "#F05454", Typeface.BOLD, 50))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun loginControl(){
        if (auth.currentUser == null) {
            val intent = Intent(activity!!, LoginScreen::class.java)
            activity!!.startActivity(intent)
        }
        else if (auth.currentUser != null) {
            /** Profile Gönder **/
        }
    }
}