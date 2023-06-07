package com.lutech.flashlight.language.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lutech.flashlight.HomeActivity
import com.lutech.flashlight.R
import com.lutech.flashlight.ads.AdsListener
import com.lutech.flashlight.ads.AdsManager
import com.lutech.flashlight.ads.TemplateView
import com.lutech.flashlight.ads.Utils
import com.lutech.flashlight.language.adapter.LanguageAdapter
import com.lutech.flashlight.language.model.Country
import com.lutech.flashlight.util.ChangeLanguage
import com.lutech.flashlight.util.CheckLoginFirst
import kotlinx.android.synthetic.main.activity_language.*
import kotlinx.android.synthetic.main.content_ads_loading.*
import java.util.*

class LanguageActivity : AppCompatActivity(), LanguageAdapter.OnItemLanguageListener, AdsListener {

    private lateinit var mItemLanguageAdapter: LanguageAdapter

    private lateinit var mLanguages: ArrayList<Country>

    var mPosCheck = 0

    private var mIOSCountry =
        listOf("en", "es", "fr", "hi", "vi", "pt", "de", "it", "ja", "ar", "iw", "ko", "nl")

    private var mIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        window.statusBarColor =
            ContextCompat.getColor(applicationContext, R.color.color_primary)
//        Utils.IsReadyShowOpenAds = true
        loadAds()
        initData()
        handleEvents()
    }

    private fun loadAds() {
        val template = findViewById<TemplateView>(R.id.my_template)
        if (AdsManager.IsShowNativeLanguageAds) {
            Utils.loadNativeAds(this, my_template, getString(R.string.language_native_id))
        } else {
            template.visibility = View.GONE
        }
    }

    override fun onItemLanguageClick(position: Int) {
        Log.d("111222222", position.toString() + "   " + mPosCheck)
        mPosCheck = position
        mItemLanguageAdapter.notifyDataSetChanged()
    }

    private fun initData() {
        mLanguages = ArrayList()

        mLanguages.add(
            Country(
                R.drawable.ic_flag_english,
                getString(R.string.english),
                locale = "en"
            )
        )

        mLanguages.add(Country(R.drawable.ic_flag_spain, getString(R.string.spain), locale = "es"))
//
        mLanguages.add(
            Country(
                R.drawable.ic_flag_france,
                getString(R.string.french),
                locale = "fr"
            )
        )

        mLanguages.add(Country(R.drawable.ic_flag_india, getString(R.string.india), locale = "hi"))

        mLanguages.add(
            Country(
                R.drawable.ic_flag_vietnam,
                getString(R.string.vietnamese),
                locale = "vi"
            )
        )

        mLanguages.add(
            Country(
                R.drawable.ic_flag_portugal,
                getString(R.string.portugal),
                locale = "pt"
            )
        )
//
        mLanguages.add(
            Country(
                R.drawable.ic_flag_germany,
                getString(R.string.germany),
                locale = "de"
            )
        )

        mLanguages.add(
            Country(
                R.drawable.icon_flag_hausa,
                getString(R.string.hausa),
                locale = "ha"
            )
        )
        mLanguages.add(
            Country(
                R.drawable.ic_flag_chinese,
                getString(R.string.chinese),
                locale = "zh"
            )
        )

//
//        mLanguages.add(Country(R.drawable.ic_flag_italy, getString(R.string.italy), locale ="it"))
//
//        mLanguages.add(Country(R.drawable.ic_flag_japan, getString(R.string.japan), locale ="ja"))
//
        mLanguages.add(Country(R.drawable.ic_flag_uae, getString(R.string.uae), locale = "ar"))
//
//        mLanguages.add(Country(R.drawable.ic_flag_israel, getString(R.string.israel), locale = "iw"))
//
//        mLanguages.add(Country(R.drawable.ic_flag_korea, getString(R.string.korea), locale = "ko"))
//
//        mLanguages.add(Country(R.drawable.ic_flag_nederland, getString(R.string.nederland), locale = "nl"))
//
        mLanguages.add(
            Country(
                R.drawable.ic_flag_africa,
                getString(R.string.sounth_african),
                locale = "zu"
            )
        )
//
//        mLanguages.add(Country(R.drawable.ic_flag_georgia, getString(R.string.georgian), locale ="ka"))
//
//        mLanguages.add(Country(R.drawable.ic_flag_poland, getString(R.string.polish), locale ="pl"))
//
//        mLanguages.add(Country(R.drawable.ic_flag_turkey, getString(R.string.turkish), locale ="tr"))

        val defaultCountry = mLanguages.find { it.locale == Locale.getDefault().language }
        if (defaultCountry != null) {
            Log.d("113333", defaultCountry.locale)
            if (defaultCountry.locale != "en") {
                mLanguages.remove(defaultCountry)
                mLanguages.add(2, defaultCountry)
            }
        }

        mPosCheck =
            mLanguages.indexOfFirst {
                it.locale == ChangeLanguage.getIOSCountryData(
                    applicationContext
                )
            }

        if (mPosCheck < 0 || mPosCheck >= mLanguages.size) {
            mPosCheck = 0
        }
        Log.d("23333333", mPosCheck.toString())

        mItemLanguageAdapter = LanguageAdapter(this, mLanguages, this)
        rvLanguage.adapter = mItemLanguageAdapter
    }

    private fun handleEvents() {
        btnTickLanguage.setOnClickListener {
            ChangeLanguage.isSetLanguage(this)
            ChangeLanguage.setIOSCountryData(mLanguages[mPosCheck].locale, this)
            ChangeLanguage.setCurrentFlag(mLanguages[mPosCheck].icon, this)

            val checkLoginFirst = CheckLoginFirst(this)
            checkLoginFirst.setFistSetLanguage(true)
            mIntent = Intent(this, HomeActivity::class.java)
            AdsManager.showAds(this, this)

        }
    }

    private fun goToNextScreen() {
        mIntent?.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(mIntent)
        finish()
    }

    override fun onAdDismissed() {
        layoutLoadingAds.visibility = View.GONE
        mIntent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(mIntent)
        finish()
    }

    override fun onWaitAds() {
        layoutLoadingAds.visibility = View.VISIBLE
    }
}