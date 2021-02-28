package com.example.mygoaldiary.Models

import com.google.firebase.Timestamp

class SocialModel (
        val userUuid : String,
        val category : String?,
        val comment : String,
        val currentDate : String,
        val currentTime : String,
        val timeStamp : Timestamp,
        val postId : String
)