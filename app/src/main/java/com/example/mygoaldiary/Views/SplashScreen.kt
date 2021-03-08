package com.example.mygoaldiary.Views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.mygoaldiary.R
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.style.ThreeBounce
import kotlin.random.Random

class SplashScreen : AppCompatActivity() {

    val texts = arrayOf(
        "“To see me does not necessarily mean to see my face. To understand my thoughts is to have seen me.” \n-Mustafa Kemal Atatürk",
        "“Our true mentor in life is science” \n-Mustafa Kemal Atatürk",
        "“Of all things, I liked books best.” \n-Tesla",
        "“Be alone, that is the secret of invention; be alone, that is when ideas are born.” \n-Tesla",
        "“Few are those who see with their own eyes and feel with their own.” \n-Einstein",
        "“Try not to become a man of success, but rather try to become a man of value.” \n-Einstein",
        "“Great spirits have always encountered violent opposition from mediocre minds.” \n-Einstein",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        val randomNumber = (0..6).random()

        val quotesTv = findViewById<TextView>(R.id.quotesTv)
        quotesTv.text = texts[randomNumber]

        val timerThread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    go()
                } finally {
                    go()
                }
            }
        }
        timerThread.start()

    }

    private fun go(){
        val intent = Intent(this@SplashScreen, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}