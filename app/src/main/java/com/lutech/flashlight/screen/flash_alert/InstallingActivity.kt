package com.lutech.flashlight.screen.flash_alert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lutech.flashlight.R
import kotlinx.android.synthetic.main.activity_installing.*

class InstallingActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_installing)

        handleEvent()
    }

    private fun handleEvent() {
        ivClose.setOnClickListener {
            finish()
        }

        btnTry.setOnClickListener {
            startActivity(Intent(this, ComingCallTryingActivity::class.java))
            finish()
        }
    }
}