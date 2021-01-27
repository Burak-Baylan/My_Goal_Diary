package com.example.mygoaldiary.Models

data class TaskModel(
    val id : String?,
    val taskUuid : String,
    val title: String,
    val isDone: String,
    val isHybrid : String,
    val yearDate: String,
    val time: String,
)