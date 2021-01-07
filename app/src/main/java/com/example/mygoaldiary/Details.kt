package com.example.mygoaldiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mygoaldiary.ListView.ListViewCreator
import com.example.mygoaldiary.ListView.Model

class Details : AppCompatActivity() {

    val lVCreator = ListViewCreator(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val key = intent.getStringExtra("key")
        title = key
    }



}