package com.lutech.flashlight

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.MobileAds
import com.google.firebase.remoteconfig.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.lutech.flashlight.ads.*
import com.lutech.flashlight.buy_premium.BillingClientSetup
import com.lutech.flashlight.language.activity.LanguageActivity
import com.lutech.flashlight.util.ChangeLanguage
import com.lutech.flashlight.util.CheckLoginFirst
import kotlinx.android.synthetic.main.dialog_update_version.*


class SplashActivity : BaseActivity(), AdsListener, OnDismissTimerListener {
    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig

    private var mIntent: Intent? = null

    private var mWarningDialog: Dialog? = null

    private var isCloseApp = true

    private var mTimerNextScreen = object : CountDownTimer(Constants.MAX_TIME_AT_SPLASH, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            AdsManager.stopSplashAds()
            gotoNextScreen()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ChangeLanguage.setLanguageForApp(this)

        setContentView(R.layout.activity_splash)
        MobileAds.initialize(
            this
        ) {
            Log.d("88888888888", "fetchAd  ")
            MyApplication.appOpenManager?.fetchAd()
            AdsManager.loadAds(this)
        }

        Utils.IsReadyShowOpenAds = false
        initView()
        initData()
        handleEvents()


        mTimerNextScreen.start()
    }


    private fun initView() {
        mWarningDialog = Dialog(this)
        mWarningDialog?.setContentView(R.layout.dialog_update_version)
        mWarningDialog?.setCancelable(false)
        mWarningDialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mWarningDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //bg full white
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //  set status text dark
        window.statusBarColor =
            ContextCompat.getColor(this, R.color.white) // set status background white

    }

    private fun initData() {

        MobileAds.initialize(
            this
        ) {
            MyApplication.appOpenManager?.fetchAd()
            AdsManager.loadAds(this)
            Log.d("====>@34234", "load ads: ")
        }
        Utils.IsReadyShowOpenAds = false

        try {
            Log.d("====>@34234", "load true: ")

            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build()
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
            mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
            mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this) { task ->
                    loadDataRemoteConfig()
//                    gotoNextScreen()
                }
        } catch (e: Exception) {
            Log.d("====>@34234", "load false: ")

            showAds()
        }
    }

    private fun loadDataRemoteConfig() {
        AdsManager.DistanceTimeShowSameAds =
            mFirebaseRemoteConfig.getLong("distance_time_show_same_ads").toInt()
        AdsManager.DistanceTimeShowOtherAds =
            mFirebaseRemoteConfig.getLong("distance_time_show_other_ads").toInt()
//        AdsManager.DistanceTimeShowRewardAds =
//            mFirebaseRemoteConfig.getLong("distance_time_show_reward_ads").toInt()
        AdsManager.IsShowNativeHomeAds =
            mFirebaseRemoteConfig.getBoolean("is_show_native_setting_ads")
        AdsManager.IsShowNativeAlarmAds =
            mFirebaseRemoteConfig.getBoolean("is_show_native_alarm_ads")
        AdsManager.IsShowNativeHistoryAds =
            mFirebaseRemoteConfig.getBoolean("is_show_native_history_ads")
        AdsManager.IsShowNativeLanguageAds =
            mFirebaseRemoteConfig.getBoolean("is_show_native_language_ads")
        AdsManager.IsShowBannerAds = mFirebaseRemoteConfig.getBoolean("is_show_banner_ads")
        AdsManager.IsShowInterAds = mFirebaseRemoteConfig.getBoolean("is_show_inter_ads")
        AdsManager.IsShowOpenAds = mFirebaseRemoteConfig.getBoolean("is_show_open_ads")
        AdsManager.IsShowRewardAds = mFirebaseRemoteConfig.getBoolean("is_show_rewards_ads")

        Constants.MAX_TIME_AT_SPLASH = mFirebaseRemoteConfig.getLong("max_time_at_splash")

        Constants.MINIMUM_VERSION_CODE = mFirebaseRemoteConfig.getLong("minimum_version")
        Constants.CURERENT_VERSION_CODE = mFirebaseRemoteConfig.getLong("current_version")

        Constants.IS_SHOW_RATE_FIRST_BACK =
            mFirebaseRemoteConfig.getBoolean("is_show_rate_first_back")

        if (BuildConfig.VERSION_CODE >= Constants.MINIMUM_VERSION_CODE) {
            if (BuildConfig.VERSION_CODE < Constants.CURERENT_VERSION_CODE) {
                mWarningDialog?.btnDoLater?.visibility = View.VISIBLE
                mTimerNextScreen.cancel()
                mWarningDialog?.show()
                isCloseApp = false
            } else {
                showAds()
            }
        } else {
            mTimerNextScreen.cancel()
            mWarningDialog?.show()
        }
    }

    private fun handleEvents() {

        mWarningDialog?.btnGotoStore?.setOnClickListener {
            Utils.setTrackEvent(this, "event_goto_store_update", "event_goto_store_update")
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        mWarningDialog?.btnBack?.setOnClickListener {
            if (isCloseApp) {
                Utils.setTrackEvent(this, "event_no_update", "event_no_update")
                finish()
            } else {
                Utils.setTrackEvent(this, "event_update_later", "event_update_later")
                showAds()
            }
        }

        mWarningDialog?.btnDoLater?.setOnClickListener {
            Utils.setTrackEvent(this, "event_update_later", "event_update_later")
            mWarningDialog?.dismiss()
            showAds()
        }

    }

    private fun showAds() {
            val checkLoginFirst: CheckLoginFirst =
                CheckLoginFirst(this)
        mIntent = null
        mIntent = if (checkLoginFirst.isFirsSetLanguage == false) {
            Log.d("====>@34234", "gotoNextScreen false: ")
            Intent(this, LanguageActivity::class.java)

        } else {
            Log.d("====>@34234", "gotoNextScreen:true ")
            Intent(this, HomeActivity::class.java)
        }

        mIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        if (!BillingClientSetup.isUpgraded(this@SplashActivity) && AdsManager.IsShowInterAds) {
            Handler(Looper.getMainLooper()).postDelayed({
                AdsManager.showSplashAds(this, this, this)
            }, 1000)

        } else {
            mTimerNextScreen.cancel()
            startActivity(mIntent)
            finish()
        }
    }

    private fun gotoNextScreen() {
        if (mIntent == null) {
            val checkLoginFirst: CheckLoginFirst =
                CheckLoginFirst(this)
            mIntent = if (checkLoginFirst.isFirsSetLanguage == false) {
                Log.d("====>@34234", "gotoNextScreen false: ")
                Intent(this, LanguageActivity::class.java)

            } else {
                Log.d("====>@34234", "gotoNextScreen:true ")
                Intent(this, HomeActivity::class.java)
            }
        }
        mTimerNextScreen.cancel()
        startActivity(mIntent)
        finish()
    }

    override fun onAdDismissed() {
        mTimerNextScreen.cancel()
        gotoNextScreen()
    }

    override fun onWaitAds() {

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onDismiss() {
        mTimerNextScreen.cancel()
    }
}