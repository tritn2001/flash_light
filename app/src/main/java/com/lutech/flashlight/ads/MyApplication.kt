package com.lutech.flashlight.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.BuildConfig
import com.adjust.sdk.LogLevel
import com.google.android.gms.ads.MobileAds

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            Log.d("88888888888", "AppOpenManager  ")
            MobileAds.initialize(
                this
            ) {
                appOpenManager = AppOpenManager(this)
            }
//            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
//            OneSignal.initWithContext(this)
//            OneSignal.setAppId("c53f48f1-1283-4d2c-82f2-ca7a721868a1")
//            startMyService()
            val appToken = ""
            val environment: String = if (BuildConfig.DEBUG) {
                AdjustConfig.ENVIRONMENT_SANDBOX
            } else {
                AdjustConfig.ENVIRONMENT_PRODUCTION
            }
            val config = AdjustConfig(this, appToken, environment)
            config.setLogLevel(LogLevel.VERBOSE);
            config.setPreinstallTrackingEnabled(true)

            Adjust.onCreate(config)
            registerActivityLifecycleCallbacks(AdjustLifecycleCallbacks())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private class AdjustLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
            Adjust.onResume()
        }

        override fun onActivityPaused(activity: Activity) {
            Adjust.onPause()
        } //...

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }
    }

//    private fun startMyService() {
//        try {
//            if (!Utils.isMyServiceRunning(NotificationService::class.java, applicationContext!!)) {
//                when {
//                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//                        applicationContext.startService(Intent(applicationContext, NotificationService::class.java))
//                    }
//                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
//                        applicationContext.startForegroundService(
//                            Intent(
//                                applicationContext,
//                                NotificationService::class.java
//                            )
//                        )
//                    }
//                    else -> {
//                        applicationContext.startService(Intent(applicationContext, NotificationService::class.java))
//
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }


    companion object {

        var appOpenManager: AppOpenManager? = null

        var myApplication: MyApplication? = null

        fun getInstance(): MyApplication {
            if (myApplication == null) {
                myApplication = MyApplication()
            }
            return myApplication!!
        }
    }

}