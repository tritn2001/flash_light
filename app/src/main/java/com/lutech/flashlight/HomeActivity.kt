package com.lutech.flashlight

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationBarView
import com.lutech.flashlight.ads.AdsListener
import com.lutech.flashlight.ads.AdsManager
import com.lutech.flashlight.ads.AdsManager.IsShowInterAds
import com.lutech.flashlight.ads.Constants
import com.lutech.flashlight.ads.Utils
import com.lutech.flashlight.buy_premium.BillingClientSetup
import com.lutech.flashlight.screen.FirstFinishActivity
import com.lutech.flashlight.screen.flash_alert.FlashAlertFragment
import com.lutech.flashlight.screen.flash_light.FlashLightFragment
import com.lutech.flashlight.screen.guide.GuideActivity
import com.lutech.flashlight.screen.guide.GuideFragment
import com.lutech.flashlight.screen.setting.SettingFragment
import com.lutech.flashlight.util.ChangeLanguage
import com.lutech.flashlight.util.MySharePreference
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_ads_loading.*


class HomeActivity : AppCompatActivity(), AdsListener {

    private var mIntent: Intent? = null

    private var isShowedRate : Boolean =false

    private lateinit var mySharePreference: MySharePreference

    private var doublePress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ChangeLanguage.setLanguageForApp(this)

        setContentView(R.layout.activity_home)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        mySharePreference = MySharePreference(this)

        initView()
        Log.d("========>333333333333333333", "loadDataRemoteConfig: "+Constants.CURERENT_VERSION_CODE)


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

                R.id.bottom_guide -> {
                    mIntent = null
                    mIntent = Intent(this, GuideActivity::class.java)
                    showAds()
                }

                R.id.bottom_setting -> manager1.beginTransaction()
                    .replace(R.id.flContent, SettingFragment())
                    .commit()
            }
            true
        }
    }

    private fun gotoNextScreen() {
        layoutLoadingAds.visibility = View.GONE
        if (mIntent != null) {
            startActivity(mIntent)
            mIntent = null
        } else {
            finish()
        }
    }


    private fun showAds() {
        if (!BillingClientSetup.isUpgraded(this) && IsShowInterAds) {
            AdsManager.showAds(this, this)
        } else {
            gotoNextScreen()
        }
    }

    override fun onAdDismissed() {
        gotoNextScreen()
    }

    override fun onWaitAds() {
        layoutLoadingAds.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if (Constants.IS_SHOW_RATE_FIRST_BACK && !isShowedRate) {

            if (!mySharePreference.getValueBoolean(Constants.FIRST_RATE)) {
                Utils.openDialogRate(this, true)
                isShowedRate=true
                return
            }
        }

        if (doublePress) {
            startActivity(Intent(this,FirstFinishActivity::class.java))
            return
        }
        doublePress = true
        Toast.makeText(this, R.string.txt_press_again_to_exit, Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doublePress = false }, 2000)
    }

}