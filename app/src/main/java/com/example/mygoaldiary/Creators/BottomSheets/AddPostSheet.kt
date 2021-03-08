package com.example.mygoaldiary.Creators.BottomSheets

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.example.mygoaldiary.Creators.ShowAlert
import com.example.mygoaldiary.FirebaseManage.FirebaseAuthClass
import com.example.mygoaldiary.Helpers.GetCurrentDate
import com.example.mygoaldiary.Helpers.MyHelpers
import com.example.mygoaldiary.Helpers.SocialHelpers.EditTextSizeListener
import com.example.mygoaldiary.LoadingDialog
import com.example.mygoaldiary.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class AddPostSheet (val context : Context, val activity : Activity){

    private lateinit var alertCreator : ShowAlert
    private lateinit var bottomSheetView : View

    private var selectedCategory: String? = null
    private val loadingDialog = LoadingDialog(activity)

    fun createSheet() : View {
        alertCreator = ShowAlert(context)
        bottomSheetView = LayoutInflater.from(context).inflate(R.layout.sheet_add_post, null)

        val provinceList : MutableList<String> = ArrayList()
        val spinner = bottomSheetView.findViewById(R.id.categorySpinner) as SmartMaterialSpinner<*>
        with(provinceList){
            this.add(activity.getString(R.string.general))
            this.add(activity.getString(R.string.advices))
            this.add(activity.getString(R.string.goals))
            this.add(activity.getString(R.string.technology))
            this.add(activity.getString(R.string.games))
            spinner.item = this
        }

        with(bottomSheetView.findViewById(R.id.categorySpinner) as SmartMaterialSpinner<*>) {
            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedCategory = provinceList[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        bottomSheetView.findViewById<Button>(R.id.sharePostButton).setOnClickListener { addPost() }

        sheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        sheetDialog.setContentView(bottomSheetView)

        textChangeListener(bottomSheetView)

        return bottomSheetView
    }

    private lateinit var sheetDialog : BottomSheetDialog
    fun show(){
        sheetDialog.show()
    }

    private val firebase = FirebaseFirestore.getInstance()
    private val firebaseAuthClass = FirebaseAuthClass(context, activity)

    private var postUuid : String? = null
    private fun addPost() {

        loadingDialog.startLoadingDialog()

        postUuid = MyHelpers.getUuid()

        getPostData()?.let { data ->
            firebase.collection("Posts").document(postUuid!!).set(data).addOnSuccessListener {
                loadingDialog.dismissDialog()
                sheetDialog.dismiss()
            }.addOnFailureListener {
                loadingDialog.dismissDialog()
                showAlert.errorAlert("Error", it.localizedMessage!!, true)
            }
        }
    }

    private val showAlert = ShowAlert(context)
    private fun getPostData(): HashMap<String, Any>? {
        val currentUser = firebaseAuthClass.getCurrentUser()
        return if (currentUser != null){
            val commentEdt = bottomSheetView.findViewById<EditText>(R.id.commentEditText)
            if (selectedCategory != null) {
                if (commentEdt.text.isNotBlank()) {
                    hashMapOf(
                            "ownerUuid" to currentUser.uid,
                            "comment" to commentEdt.text.toString().trim(),
                            "category" to selectedCategory!!,
                            "timeStamp" to Timestamp.now(),
                            "postId" to postUuid!!,
                            "pushNotify" to true
                    )
                } else {
                    loadingDialog.dismissDialog()
                    showAlert.errorAlert(activity.getString(R.string.error), activity.getString(R.string.descriptionEmpty), true)
                    null
                }
            }else{
                loadingDialog.dismissDialog()
                showAlert.errorAlert(activity.getString(R.string.error), activity.getString(R.string.mustChooseCategory), true)
                null
            }
        }else{
            showAlert.infoAlert(activity.getString(R.string.error), activity.getString(R.string.loginForSharePost), true)
            loadingDialog.dismissDialog()
            null
        }
    }

    var progr = 0
    private fun textChangeListener(bottomSheetView: View) {
        progr = 0
        val counterProgressBar = bottomSheetView.findViewById<ProgressBar>(R.id.counter_progress_bar).apply {
            this.max = 250
        }
        bottomSheetView.findViewById<EditText>(R.id.commentEditText).addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                with(bottomSheetView.findViewById<TextView>(R.id.addPostCounterText)){
                    this.text = "${s!!.length}"
                    progr = s.length
                    this.setTextColor(EditTextSizeListener.control(s.length))
                    counterProgressBar.progress = progr
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}