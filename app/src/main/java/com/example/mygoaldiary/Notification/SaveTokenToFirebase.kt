package com.example.mygoaldiary.Notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SaveTokenToFirebase {
    fun save (token : String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("Users").document(currentUser!!.uid).update("notifyToken", token)
        }
    }
}