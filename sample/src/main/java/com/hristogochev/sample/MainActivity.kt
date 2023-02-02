package com.hristogochev.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CoroutineScope(Dispatchers.IO).launch {
            val loader = findViewById<View>(R.id.myLoader)
            delay(5000)
            withContext(Dispatchers.Main) {
                loader.visibility = View.INVISIBLE
            }
            delay(5000)
            withContext(Dispatchers.Main) {
                loader.visibility = View.VISIBLE
            }
        }
    }
}