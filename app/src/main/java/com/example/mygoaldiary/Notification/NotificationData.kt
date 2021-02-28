package com.example.mygoaldiary.Notification

data class NotificationData(
    val title : String,
    val message : String,
    val postId : String,
    val ownerId : String,
    val comment : String?
)