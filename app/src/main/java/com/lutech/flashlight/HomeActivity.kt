package com.lutech.flashlight

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationBarView
import com.lutech.flashlight.screen.flash_alert.FlashAlertFragment
import com.lutech.flashlight.screen.flash_light.FlashLightFragment
import com.lutech.flashlight.screen.guide.GuideFragment
import com.lutech.flashlight.screen.setting.SettingFragment
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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

                R.id.bottom_guide -> manager1.beginTransaction()
                    .replace(R.id.flContent, GuideFragment())
                    .commit()

                R.id.bottom_setting -> manager1.beginTransaction()
                    .replace(R.id.flContent, SettingFragment())
                    .commit()
            }
            true
        }
    }


}