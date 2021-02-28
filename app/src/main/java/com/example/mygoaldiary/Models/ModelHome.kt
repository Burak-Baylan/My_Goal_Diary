package com.example.mygoaldiary.Models

data class ModelHome(
        val id : String?,
        val title: String,
        val imgColor: Int,
        val yearDate: String,
        val time: String,
        val textViewColor: String,
        val typeFace: Int,
        val imageViewSize : Int,
        val isHybrid : String?,
        val projectUuid : String?,
        val lastInteraction : String?,
        val targetedDeadline : String?,
)