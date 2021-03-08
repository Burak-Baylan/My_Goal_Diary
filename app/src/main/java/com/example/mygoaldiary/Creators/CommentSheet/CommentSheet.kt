package com.example.mygoaldiary.Creators.CommentSheet

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.Helpers.SocialHelpers.AddComment
import com.example.mygoaldiary.Helpers.SocialHelpers.EditTextSizeListener
import com.example.mygoaldiary.Helpers.SocialHelpers.GetComments
import com.example.mygoaldiary.Models.SocialCommentsModel
import com.example.mygoaldiary.R
import com.example.mygoaldiary.RecyclerView.SocialCommentsAdapter
import com.example.mygoaldiary.Views.BottomNavFragments.Social
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

open class CommentSheet(private val contextHere: Context, private val activity: Activity) : Social(){

    private lateinit var alertCreator : ShowAlert
    private lateinit var bottomSheetView : View
    private lateinit var sendButton : Button
    private lateinit var commentText : TextView
    private lateinit var usernameText : TextView
    private lateinit var emailText : TextView
    private lateinit var ppIv : CircleImageView
    private lateinit var userPropLayout : ConstraintLayout
    private lateinit var userPropProgress : SpinKitView
    private lateinit var likeManager : LikeManager
    private lateinit var addCommentSizeTv : TextView
    private lateinit var commentSizeProgressBar : ProgressBar

    companion object {
        lateinit var adapter : SocialCommentsAdapter
        lateinit var commentsRecyclerView : RecyclerView
        lateinit var commentPostEditText : EditText
        lateinit var spinKitLoadingView : SpinKitView
        lateinit var noComments : TextView
        var items : ArrayList<SocialCommentsModel> = ArrayList()
        lateinit var postId : String
        lateinit var commentsRefreshLayout : SwipeRefreshLayout
        lateinit var mOwnerId : String
        var mComment : String? = null
        lateinit var likeButton : ImageView
        lateinit var likeTextView : TextView
    }

    private fun <T : View> findViewById(@IdRes id : Int): T = bottomSheetView.findViewById(id)

    fun createSheet(postIdHere: String, ownerId: String, comment: String?) : View {
        mOwnerId = ownerId
        mComment = comment
        postId = postIdHere

        create()
        getUserProps()

        commentText.text = comment

        likeManager = LikeManager(contextHere, activity)
        likeManager.listenLikeCount()
        likeManager.checkIfExists(likeButton, null, "#32A852")

        return bottomSheetView
    }

    private fun getUserProps() {
        firebase.collection("Posts").document(postId).get().addOnSuccessListener { postIt ->
            if (postIt.exists() && postIt != null){
                firebase.collection("Users").document(postIt["ownerUuid"] as String).get().addOnSuccessListener {
                    if (it.exists() && it != null){
                        usernameText.text = it["userName"] as String
                        emailText.text = it["userEmail"] as String
                        it["avatarLink"]?.let { link ->
                            Picasso.get().load(link as String).into(ppIv)
                        }
                        userPropProgress.visibility = View.INVISIBLE
                        userPropLayout.visibility = View.VISIBLE
                        getComment()
                    }
                }.addOnFailureListener {
                    println("CommentSheet: ${it.localizedMessage}")
                }
            }
        }
    }

    private fun getComment(){
        commentPostEditTextListeners()
        sendButton.setOnClickListener { sendComment() }
        initializeRecyclerView()
        getComments()
        adapter.notifyDataSetChanged()
        sheetDialog.setContentView(bottomSheetView)
        sheetDialog.show()
    }

    private lateinit var sheetDialog : BottomSheetDialog

    @SuppressLint("ClickableViewAccessibility", "InflateParams")
    private fun create(){
        alertCreator = ShowAlert(contextHere)
        sheetDialog = BottomSheetDialog(contextHere, R.style.BottomSheetDialogTheme)
        bottomSheetView = LayoutInflater.from(contextHere).inflate(R.layout.sheet_comments, null)
        sendButton = findViewById(R.id.sendCommentButton)
        commentsRecyclerView = findViewById(R.id.postCommentsRecyclerView)
        commentsRefreshLayout = findViewById(R.id.commentsRefreshLayout)
        spinKitLoadingView = findViewById(R.id.commentsLoadingProgress)
        noComments = findViewById(R.id.noCommentsTv)
        commentText = findViewById(R.id.commentTv)
        usernameText = findViewById(R.id.commentsUsernameTv)
        emailText = findViewById(R.id.commentsEmailTv)
        ppIv = findViewById(R.id.ppIvComments)
        userPropLayout = findViewById(R.id.userPropLayout)
        userPropProgress = findViewById(R.id.userPropLoadingProgress)
        likeButton = findViewById(R.id.commentSheetLikeIc)
        likeTextView = findViewById(R.id.commentSheetLikeTextView)
        addCommentSizeTv = findViewById(R.id.addCommentCounterText)
        commentSizeProgressBar = findViewById(R.id.comment_counter_progress_bar)

        sheetDialog.setOnShowListener {dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        with(commentsRefreshLayout){
            this.setColorSchemeColors(Color.parseColor("#FFFFFF"))
            this.setProgressBackgroundColorSchemeColor(Color.parseColor("#F05454"))
            this.setOnRefreshListener { refresh(this) }
            this.setOnTouchListener { v, event ->
                v.parent.requestDisallowInterceptTouchEvent(true)
                when(event.action and MotionEvent.ACTION_MASK){
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                }
                false
            }
        }

        findViewById<ImageView>(R.id.closeCommentPostIc).setOnClickListener { sheetDialog.dismiss() }

        likeButton.setOnClickListener {
            likeManager.like()
        }

    }

    fun getComments() =
        GetComments().apply {
            recyclerView = commentsRecyclerView
            loadingView = spinKitLoadingView
            noCommentsTv = noComments
            get(items, postId)
        }


    private fun refresh(swipeRefreshLayout: SwipeRefreshLayout) {
        commentsRecyclerView.removeAllViews()
        items.clear()
        getComments()
        swipeRefreshLayout.isRefreshing = false
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun commentPostEditTextListeners() {
        commentPostEditText = findViewById(R.id.commentPostEditText)
        var commentEditTextIsOpen = false
        commentPostEditText.setOnTouchListener { v, event -> // Bottom sheet dialog açıkken editText scrollable özelliğini kaybediyordu. Bu onun çözümü.
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        commentPostEditText.setOnClickListener{
            commentEditTextIsOpen = if (!commentEditTextIsOpen){
                sendButton.visibility = View.VISIBLE
                false
            }else{
                sendButton.visibility = View.GONE
                true
            }
        }

        var progressCount : Int
        commentSizeProgressBar.max = 250
        commentPostEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                with(addCommentSizeTv){
                    this.text = "${s!!.length}"
                    progressCount = s.length
                    this.setTextColor(EditTextSizeListener.control(s.length))
                    commentSizeProgressBar.progress = progressCount
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun initializeRecyclerView(){
        adapter = SocialCommentsAdapter(items, activity)
        commentsRecyclerView.layoutManager = LinearLayoutManager(contextHere)
        commentsRecyclerView.adapter = adapter
    }

    private fun sendComment(){
        with(commentPostEditText.text){
            if (this.isNotEmpty()) {
                println(commentText.text.toString())
                AddComment(contextHere, activity).add(postId, this.trim().toString(), mOwnerId, commentText.text.toString())
            }
        }
    }
}