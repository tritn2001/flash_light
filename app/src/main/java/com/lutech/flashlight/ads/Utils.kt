package com.lutech.flashlight.ads

import android.app.Activity
import android.app.ActivityManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.AdjustEvent
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.lutech.flashlight.R
import com.lutech.flashlight.ads.welcome.WelcomeBackActivity
import com.lutech.flashlight.buy_premium.BillingClientSetup
import com.lutech.flashlight.util.CustomDialog
import com.lutech.flashlight.util.MySharePreference
import kotlinx.android.synthetic.main.dialog_rateus.*


object Utils {
    var onShow = false
    private var onceThru = true
    var IsReadyShowOpenAds = true

    private const val blockCharacterSet = "~#^|$%&*!/.-"

    fun goToCHPlay(context: Context) {
        val appPackageName: String =
            context.packageName // getPackageName() from Context or Activity object
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    fun openLink(context: Context, url: String) {

        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (anfe: ActivityNotFoundException) {
            val intent: Intent = Intent(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }
    }

    fun loadNativeAds(context: Context, template: TemplateView, nativeIdAds: String) {
        val adLoader = AdLoader.Builder(context, nativeIdAds)
            .forNativeAd { nativeAd ->
                val styles: NativeTemplateStyle = NativeTemplateStyle.Builder().build()
                nativeAd.setOnPaidEventListener {
                    setTrackRevenueByAdjust(it.valueMicros, it.currencyCode)
                }
                template.setStyles(styles)
                template.visibility = View.VISIBLE
                template.setNativeAd(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    template.visibility = View.GONE
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            )
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun loadBannerAds(mAdView: AdView) {
        val adRequest2 = AdRequest.Builder().build()
        mAdView.loadAd(adRequest2)

        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                mAdView.setOnPaidEventListener {
                    setTrackRevenueByAdjust(it.valueMicros, it.currencyCode)
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                mAdView.visibility = View.GONE
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
    }


    private fun getLauncherTopApp(context: Context): String {
        val sUsageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        //isLockTypeAccessibility = SpUtil.getInstance().getBoolean(Constants.LOCK_TYPE, false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val appTasks = activityManager.getRunningTasks(1)
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks[0].topActivity!!.packageName
            }
        } else {
            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 10000
            var result = ""
            val event = UsageEvents.Event()
            val usageEvents: UsageEvents = sUsageStatsManager.queryEvents(beginTime, endTime)
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.packageName
                }
            }
            if (!TextUtils.isEmpty(result)) {
                return result
            }
        }
        return ""
    }


    private fun restartTime() {
        AdsManager.LastTimeShowAds = System.currentTimeMillis() / 1000
        onShow = false
    }

    fun showWelcomeBackScreen(activity: Activity) {
        if (BillingClientSetup.isUpgraded(activity) || !AdsManager.IsShowOpenAds || !IsReadyShowOpenAds) return
        onceThru = true
        Log.d(
            "9999999999",
            "show as=" + onShow + "__" + isShowOpenAds() + "__" + MyApplication.appOpenManager!!.isAdAvailable
        )
        if (onShow && isShowOpenAds() && MyApplication.appOpenManager!!.isAdAvailable) {
            activity.startActivity(Intent(activity, WelcomeBackActivity::class.java))
            restartTime()
        } else {
            onShow = false
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP && !getLauncherTopApp(activity).equals(activity.packageName)) {
                onShow = true
            }
        })
    }

    private fun isShowOpenAds(): Boolean {
        val delta = System.currentTimeMillis() / 1000 - AdsManager.LastTimeShowAds
        val distance = AdsManager.getDistanceTimeShowAds(false)
        if (delta >= distance) {
            return true
        }
        return false
    }

    fun getIOSCountryData(context: Context): String {
        val sharePef = context.getSharedPreferences(Constants.APP_NAME, Activity.MODE_PRIVATE)
        return sharePef.getString(Constants.KEY_LANG, "en").toString()
    }


    fun setTrackEvent(context: Context, key: String, value: String) {
        val params = Bundle()
        params.putString(key, value)
        FirebaseAnalytics.getInstance(context).logEvent("$key", params)
    }

    fun changeStatusBarColor(activity: Activity, color: Int) {
        val window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(activity, color)
        }
    }

    //Adjust
    fun setTrackRevenueByAdjust(revenue: Long, currency: String) {
        val adjustEventRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
        adjustEventRevenue.setRevenue((revenue / 1000000f).toDouble(), currency)
        Adjust.trackAdRevenue(adjustEventRevenue)
    }

    fun setTrackEventByAdjust(tokenEvent: String) {
        val adjustEvent = AdjustEvent(tokenEvent)
        Adjust.trackEvent(adjustEvent)
    }


    fun loadCollapseBannerAds(mAdView: AdView, context: Context) {
        if (AdsManager.IsShowBannerAds && !BillingClientSetup.isUpgraded(context)) {
            val extras = Bundle()
            extras.putString("collapsible", "bottom")
            val adRequest: AdRequest = AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build()

            mAdView.loadAd(adRequest)

            mAdView.setOnPaidEventListener {
                setTrackRevenueByAdjust(it.valueMicros, it.currencyCode)
            }

            mAdView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    mAdView.visibility = View.VISIBLE
                    // Code to be executed when an ad finishes loading.
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mAdView.visibility = View.GONE
                    // Code to be executed when an ad request fails.
                }

                override fun onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                    Log.d("4444", "onAdOpened")
                }

                override fun onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                    Log.d("4444", "onAdClicked")
                }

                override fun onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                    Log.d("4444", "onAdClosed")
                    mAdView.setAdSize(AdSize.BANNER)
                }
            }
        } else {
            mAdView.visibility = View.GONE
        }

    }


    var filter =
        InputFilter { source, start, end, dest, dstart, dend ->
            if (source != null && blockCharacterSet.contains("" + source)) {
                ""
            } else null
        }

    fun shareApp(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.txt_share_app))
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=${context.packageName}"
            )
            context.startActivity(Intent.createChooser(intent, "choose one"))
        } catch (e: Exception) {
            e.toString()
        }
    }

    fun openDialogRate(context: Context, isShowWhenBack: Boolean) {
        val dialog = CustomDialog(context).setDialog(R.layout.dialog_rateus)
        dialog.btnMaybeLater.setOnClickListener{dialog.dismiss() }

        dialog.btnFeedback.setOnClickListener {
            if (isShowWhenBack) {
                val mySharePreference = MySharePreference(context)
                mySharePreference.setValueBoolean(Constants.FIRST_RATE, true)
            }
            if (dialog.rateus.rating == 0f) {

            } else if (dialog.rateus.rating >= 4) {
                goToCHPlay(context)
            } else {
                sendEmail(context)
            }

            dialog.dismiss()
        }
        dialog.show()

    }


    fun sendEmail(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("teammarketing@lutech.ltd"))
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            context.getString(R.string.txt_feedback_app) + Constants.APP_NAME
        )
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.txt_write_your_feedback_here))
        try {
            context.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    fun hideKeyboardFrom(context: Context, view: View) {
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

    }

    fun initMediaPlayer(mContext: Context, resource: Int): MediaPlayer {
        var mMedia = MediaPlayer.create(mContext, resource)
        mMedia.isLooping = false
        return mMedia
    }

    fun vibrate(mContext: Context) {
        val v = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v!!.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v!!.vibrate(500)
        }
    }


    fun isScreenOn(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager?
        return pm!!.isInteractive
    }

    fun isLowBattery(context: Context): Boolean {
        val bm = context.getSystemService(BATTERY_SERVICE) as BatteryManager
        val percent = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        Log.d("=====>3333333333333333", "isLowBattery: "+percent)
        return percent <= 20
    }

}