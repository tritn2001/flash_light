package com.lutech.flashlight

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationBarView
import com.lutech.flashlight.screen.flash_alert.FlashAlertFragment
import com.lutech.flashlight.screen.flash_light.FlashLightFragment
import com.lutech.flashlight.screen.guide.GuideActivity
import com.lutech.flashlight.screen.guide.GuideFragment
import com.lutech.flashlight.screen.setting.SettingFragment
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        initView()


    }

    private fun initView() {
        val manager1 = supportFragmentManager
        manager1.beginTransaction()
            .replace(R.id.flContent, FlashAlertFragment())
            .commit()
        bottom_navigation.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.bottom_flash_alert -> manager1.beginTransaction()
                    .replace(R.id.flContent, FlashAlertFragment())
                    .commit()

                R.id.bottom_flash_light ->
                    manager1.beginTransaction()
                        .replace(R.id.flContent, FlashLightFragment())
                        .commit()

                R.id.bottom_guide -> startActivity(Intent(this,GuideActivity::class.java))

                R.id.bottom_setting -> manager1.beginTransaction()
                    .replace(R.id.flContent, SettingFragment())
                    .commit()
            }
            true
        }
    }
}