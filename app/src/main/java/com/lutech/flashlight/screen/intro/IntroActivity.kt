package com.lutech.flashlight.screen.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.size
import com.lutech.flashlight.HomeActivity
import com.lutech.flashlight.R
import com.lutech.flashlight.adapter.ImageIntroViewPager
import com.lutech.flashlight.ads.AdsListener
import com.lutech.flashlight.ads.AdsManager
import com.lutech.flashlight.ads.Utils
import com.lutech.flashlight.buy_premium.BillingClientSetup
import com.lutech.flashlight.language.activity.LanguageActivity
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.activity_intro.myTemplate
import kotlinx.android.synthetic.main.activity_settings_flash_alert.*
import kotlinx.android.synthetic.main.content_ads_loading.*
import kotlinx.android.synthetic.main.fragment_flash_alert.*
import kotlinx.android.synthetic.main.layout_test.*

class IntroActivity : AppCompatActivity(), AdsListener {

    private lateinit var imageIntroViewPager: ImageIntroViewPager

    private lateinit var mListImage: ArrayList<Int>

    private var mIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        initView()
        handleEvent()
    }

    private fun handleEvent() {
        btnNext.setOnClickListener {
            if (vpImageIntro.currentItem == vpImageIntro.size) {
                mIntent = null
                mIntent = Intent(this, LanguageActivity::class.java)
                showAds()
                Log.d("=====>444444444444444", "next: ")

            } else {
                Log.d("=====>444444444444444", "goLg: ")
                vpImageIntro.currentItem = vpImageIntro.currentItem + 1
            }
        }
    }

    private fun loadNativeAds() {
        if (!BillingClientSetup.isUpgraded(this) && AdsManager.IsShowNativeAds) {
            Utils.loadNativeAds(this, myTemplate, getString(R.string.intro_native_id))
        } else {
            myTemplate.visibility = View.GONE
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


    private fun initView() {
        loadNativeAds()
        mListImage = ArrayList()

        mListImage.add(0)
        mListImage.add(0)
        mListImage.add(0)

        imageIntroViewPager = ImageIntroViewPager(mListImage, this)

        vpImageIntro.adapter = imageIntroViewPager
        vpImageIntro.currentItem = 0

        myIndicator.setViewPager(vpImageIntro)

        imageIntroViewPager.registerDataSetObserver(myIndicator.dataSetObserver)


    }
}