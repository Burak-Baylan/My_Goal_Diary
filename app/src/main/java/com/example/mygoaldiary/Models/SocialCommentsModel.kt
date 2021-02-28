package com.example.mygoaldiary.Models

import com.google.firebase.Timestamp

class SocialCommentsModel(
        val userUuid : String,
        val comment : String?,
        val currentDate : String,
        val currentTime : String,
        val timeStamp : Timestamp,
        val commentId : String,
        val postId : String
)