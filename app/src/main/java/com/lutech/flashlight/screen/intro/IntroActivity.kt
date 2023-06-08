package com.lutech.flashlight.screen.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.size
import androidx.viewpager.widget.ViewPager
import com.lutech.flashlight.HomeActivity
import com.lutech.flashlight.R
import com.lutech.flashlight.adapter.ImageIntroViewPager
import com.lutech.flashlight.ads.AdsListener
import com.lutech.flashlight.ads.AdsManager
import com.lutech.flashlight.ads.Utils
import com.lutech.flashlight.buy_premium.BillingClientSetup
import com.lutech.flashlight.language.activity.LanguageActivity
import com.lutech.flashlight.util.ChangeLanguage
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.activity_intro.myTemplate
import kotlinx.android.synthetic.main.activity_settings_flash_alert.*
import kotlinx.android.synthetic.main.content_ads_loading.*
import kotlinx.android.synthetic.main.fragment_flash_alert.*
import kotlinx.android.synthetic.main.layout_test.*

class IntroActivity : AppCompatActivity(), AdsListener {

    private lateinit var imageIntroViewPager: ImageIntroViewPager

    private lateinit var mListImageIntro: ArrayList<Int>

    private lateinit var mListImageBgIntro: ArrayList<Int>

    private var mIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ChangeLanguage.setLanguageForApp(this)

        setContentView(R.layout.activity_intro)

        initView()
        handleEvent()
    }

    private fun handleEvent() {


        btnNext.setOnClickListener {
            if (vpImageIntro.currentItem == vpImageIntro.size) {
                mIntent = null
                mIntent = Intent(this, HomeActivity::class.java)
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


    private fun initView() {
        loadNativeAds()
        mListImageIntro = ArrayList()
        mListImageIntro.add(R.drawable.intro_1)
        mListImageIntro.add(R.drawable.intro_2)
        mListImageIntro.add(R.drawable.intro_3)

        mListImageBgIntro = ArrayList()
        mListImageBgIntro.add(R.drawable.bg_intro_1)
        mListImageBgIntro.add(R.drawable.bg_intro_2)
        mListImageBgIntro.add(R.drawable.bg_intro_3)

        imageIntroViewPager = ImageIntroViewPager(mListImageIntro, this)

        vpImageIntro.adapter = imageIntroViewPager
        vpImageIntro.currentItem = 0

        myIndicator.setViewPager(vpImageIntro)

        imageIntroViewPager.registerDataSetObserver(myIndicator.dataSetObserver)

        val mListTitleIntro: ArrayList<Int> = ArrayList()
        mListTitleIntro.add(R.string.txt_alarm_flash_blinks_on_call_flashlight)
        mListTitleIntro.add(R.string.txt_turn_on_the_camera_and_flash_light)
        mListTitleIntro.add(R.string.txt_turn_on_the_flash_light_creen)

        vpImageIntro.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                tvTitleIntro.text = getString(mListTitleIntro[position])
                container.background = getDrawable(mListImageBgIntro[position])

            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })


    }
}