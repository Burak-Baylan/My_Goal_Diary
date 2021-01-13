package com.example.mygoaldiary.Fragments.Fragments.BottomNavFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.baoyz.swipemenulistview.SwipeMenu
import com.baoyz.swipemenulistview.SwipeMenuCreator
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.baoyz.swipemenulistview.SwipeMenuListView
import com.baoyz.widget.PullRefreshLayout
import com.example.mygoaldiary.Details
import com.example.mygoaldiary.ListView.ListViewCreator
import com.example.mygoaldiary.ListView.Model
import com.example.mygoaldiary.R
import com.example.mygoaldiary.SQL.ManageSQL
import com.example.mygoaldiary.SQL.SQLVariablesModel


class Home : Fragment() {

    //val sqlManage = ManageSQL(context!!, activity!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val sqlManage = ManageSQL(context!!, activity!!)

        val view = inflater.inflate(R.layout.fragment_home, container, false)


        val lVCreator = ListViewCreator(context!!, activity!!)

        val fragmentManager = activity!!.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val items = ArrayList<Model>()

        items.add(Model("Tasks", R.drawable.ic_tasks))
        items.add(Model("Reports", R.drawable.ic_notes_for_reports))
        items.add(Model("Diary", R.drawable.ic_diary))
        /***/
        items.add(Model("Red", R.drawable.ic_red_color_round))
        items.add(Model("Pink", R.drawable.ic_pink_color_round))
        items.add(Model("Blue", R.drawable.ic_blue_color_round))
        items.add(Model("Green", R.drawable.ic_green_color_round))
        items.add(Model("Orange", R.drawable.ic_orange_color_round))
        /***/
        items.add(Model("Add Project", R.drawable.ic_add))

        /******************************************************************************************/
        val mSql = sqlManage.createSqlVariable("HomePage")
        sqlManage.tableCreator(mSql, "userProject", "name VARCHAR, age INT")

        mSql?.execSQL("DELETE FROM userProject")

        for (i in 0..5){
            try{
                mSql?.execSQL("INSERT INTO userProject (name, age) VALUES ('Burak $i', $i)")
            }catch (e : Exception){
                println()
                println("gelemedi\n$e")
            }
        }

        /*val getArrayList = sqlManage.get(mSql!!, "userProject", "name", "age")

        for(i in getArrayList){
            println("Gelen veri: $i\nArrayList Size: ${getArrayList.size}")
            items.add(Model("$i", R.drawable.ic_notes_for_reports))
        }*/

        /******************************************************************************************/
        val lvHere: SwipeMenuListView = view.findViewById(R.id.mListView45)


        listViewSlider(lvHere)

        val mListView = lVCreator.createListView(
            lvHere,
            R.layout.row_list_view,
            items
        )


        //listViewSlider(mListView)

        mListView.setOnItemClickListener { parent, viewHere, position, id ->
            val tvHere : TextView = viewHere.findViewById(R.id.nameTextViewFromListViewRow) as TextView
            val intent = Intent(context, Details::class.java)
            intent.putExtra("key", tvHere.text.toString())
            startActivity(intent)
        }

        return view
    }

    private fun makeCurrentFragment(fragment: Fragment) =
            activity!!.supportFragmentManager.beginTransaction().apply {
                add(R.id.fl_wrapper, fragment)
                commit()
            }

    private fun listViewSlider(listView: SwipeMenuListView){

        val creator = SwipeMenuCreator { menu -> // create "open" item
            val showItem = SwipeMenuItem(context!!)
            showItem.width = 100
            showItem.setIcon(R.drawable.ic_eye)
            menu.addMenuItem(showItem)

            val editItem = SwipeMenuItem(context)
            editItem.width = 100
            editItem.setIcon(R.drawable.ic_edit)
            menu.addMenuItem(editItem)

            val deleteItem = SwipeMenuItem(context)
            deleteItem.width = 100
            deleteItem.setIcon(R.drawable.ic_delete)
            menu.addMenuItem(deleteItem)
        }
        listView.setMenuCreator(creator)

        listView.setOnMenuItemClickListener { position, menu, index ->
            when (index) {
                0 -> { println("showItem Clicked") }
                1 -> { println("editItem Clicked") }
                2 -> { println("deleteItem Clicked") }
            }
            // false : close the menu; true : not close the menu
            false
        }


        listView.setOnSwipeListener(object : SwipeMenuListView.OnSwipeListener{
            override fun onSwipeStart(position: Int) {
                if (position == 0){
                    val creator2 = SwipeMenuCreator { menu -> // create "open" item
                        val deleteItem = SwipeMenuItem(context)
                        deleteItem.width = 100
                        deleteItem.setIcon(R.drawable.ic_delete)
                        menu.addMenuItem(deleteItem)
                    }
                    listView.setMenuCreator(creator2)
                    println("zoportMoport")
                }
            }

            override fun onSwipeEnd(position: Int) {

            }

        })



    }
}