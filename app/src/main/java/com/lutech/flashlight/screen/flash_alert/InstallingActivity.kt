package com.lutech.flashlight.screen.flash_alert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.lutech.flashlight.R
import com.lutech.flashlight.ads.AdsListener
import com.lutech.flashlight.ads.AdsManager
import com.lutech.flashlight.buy_premium.BillingClientSetup
import kotlinx.android.synthetic.main.activity_installing.*
import kotlinx.android.synthetic.main.content_ads_loading.*

class InstallingActivity : AppCompatActivity(), AdsListener {

    private var mIntent: Intent? = null

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
            mIntent = null
            mIntent = Intent(this, ComingCallTryingActivity::class.java)
            showAds()
        }
    }

    private fun gotoNextScreen() {
        layoutLoadingAds.visibility = View.GONE
        if (mIntent != null) {
            startActivity(mIntent)
            mIntent = null
            finish()
        } else {
            finish()
        }
    }


    private fun showAds() {
        if (!BillingClientSetup.isUpgraded(this) && AdsManager.IsShowInterAds) {
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
}