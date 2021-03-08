package com.example.mygoaldiary.Helpers.SocialHelpers

import android.app.Activity
import android.content.Context
import android.view.ContextThemeWrapper
import android.widget.ImageView
import android.widget.PopupMenu
import com.example.mygoaldiary.Creators.BottomSheets.ReportPostSheet
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Models.SocialModel
import com.example.mygoaldiary.R
import com.example.mygoaldiary.RecyclerView.SocialRecyclerViewAdapter
import com.google.firebase.auth.FirebaseAuth
import java.util.ArrayList

class PostsOptionsMenu (private val context : Context, private val activity : Activity){

    private lateinit var mPostOwnerId : String

    private lateinit var popupMenu : PopupMenu

    private lateinit var menuIv : ImageView

    private var auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser

    fun createOptionsMenu(button : ImageView, postOwnerId : String){
        currentUser = auth.currentUser
        if (currentUser == null) {
            showAlert.errorAlert(activity.getString(R.string.error), activity.getString(R.string.needUserOpenOptionsMenu), true)
        }else{
            this.menuIv = button

            val wrapper = ContextThemeWrapper(context, R.style.PopupMenu)
            popupMenu = PopupMenu(wrapper, button)

            this.mPostOwnerId = postOwnerId
            showOptions()
        }
    }

    private fun showOptions(){
        userControl()
    }

    private var menu = 0
    private var amITheOwner = false
    private fun userControl(){
        menu = if (currentUser!!.uid == mPostOwnerId) {
                    amITheOwner = true
                    R.menu.user_post_menu
                } else {
                    amITheOwner = false
                    R.menu.post_menu
                }
        popupMenu.inflate(menu)
        menuClickListener(amITheOwner)
    }

    var items : ArrayList<SocialModel>? = null
    var position : Int? = null
    var holder : SocialRecyclerViewAdapter.Holder? = null

    private var showAlert = ShowAlert(context)

    private fun menuClickListener(amITheOwner : Boolean) {
        if (items != null && position != null && holder != null){
            popupMenu.setOnMenuItemClickListener { selectedOption ->
                if (amITheOwner)
                    ownerIsMe(selectedOption.itemId)
                else
                    ownerIsNotMe(selectedOption.itemId)

                false
            }
            showMenu()
        }else {
            showAlert.errorAlert(activity.getString(R.string.error), activity.getString(R.string.menuCouldntOpen), true)
        }
    }

    private fun showMenu() {
        try {
            PopupMenu::class.java.getDeclaredField("mPopup").apply {
                this.isAccessible = true
                this.get(popupMenu).apply {
                    this.javaClass
                            .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                            .invoke(this, true)
                }
            }
        }catch(e : Exception){
            e.printStackTrace()
        }finally {
            popupMenu.show()
        }
    }

    private fun ownerIsMe(menuItem : Int){
        when(menuItem) {
            R.id.notify -> FilterNotification().selectFilter(context, activity, items!![position!!].postId)
            R.id.deleteThisPost -> PostDelete.delete(context, activity, items!![position!!].postId, null)
        }
    }

    private val postMarker = PostMarker()
    private var username : String? = null

    private fun ownerIsNotMe(menuItem : Int){

        username = holder!!.usernameTv.text.toString()

        when(menuItem) {
            R.id.markThisPost -> postMarker.mark(items!!, position!!, holder!!)
            R.id.reportPost -> ReportPostSheet(context, activity).createSheet(username!!, items!!, null, position!!)
        }
    }
}