package com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.example.mygoaldiary.MainActivity
import com.example.mygoaldiary.R

class Suggestions : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private var isSearchOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                makeCurrentFragment(Social())
                MainActivity.BOTTOM_NAV.selectedItemId = R.id.socialFromMenu
            }
        })

        return inflater.inflate(R.layout.fragment_suggestions, container, false)
    }

    fun makeCurrentFragment(fragment : Fragment) =
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
}