package com.example.mygoaldiary.Helpers.SocialHelpers

import com.example.mygoaldiary.Creators.ShowAlert
import com.google.firebase.firestore.FirebaseFirestore

open class SuperSocial {

    protected val firebase = FirebaseFirestore.getInstance()
    protected lateinit var showAlert : ShowAlert


}