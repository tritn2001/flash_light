package com.lutech.flashlight.screen

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.lutech.flashlight.R

class FirstFinishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fisrt_finish)

        Handler().postDelayed(Runnable {
            finishAffinity()
        }, 1500)
    }

    override fun onBackPressed() {

    }
}