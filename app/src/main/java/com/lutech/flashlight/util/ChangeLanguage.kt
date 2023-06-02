package com.lutech.flashlight.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import com.lutech.flashlight.R
import com.lutech.flashlight.ads.Constants.APP_NAME
import com.lutech.flashlight.ads.Constants.IS_SET_LANG
import com.lutech.flashlight.ads.Constants.KEY_FLAG
import com.lutech.flashlight.ads.Constants.KEY_LANG
import java.util.*


object ChangeLanguage {
    var onShow = false
    private var onceThru = true
    var IsReadyShowOpenAds = true

    fun setLanguageForApp(context: Context) {
        val languageToLoad = getIOSCountryData(context)
        val locale: Locale = if (languageToLoad == "not-set") {
            Locale.getDefault()
        } else {
            Locale(languageToLoad)
        }
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )

    }

    fun isSetLanguage(context: Context) {
        val sharePef = context.getSharedPreferences(APP_NAME, Activity.MODE_PRIVATE)
        val editor = sharePef.edit()
        editor.putBoolean(IS_SET_LANG, true)
        editor.apply()
    }


    fun setIOSCountryData(lang: String, context: Context) {
        val sharePef = context.getSharedPreferences(APP_NAME, Activity.MODE_PRIVATE)
        val editor = sharePef.edit()
        editor.putString(KEY_LANG, lang)
        editor.apply()
    }

    fun getIOSCountryData(context: Context): String {
        val sharePef = context.getSharedPreferences(APP_NAME, Activity.MODE_PRIVATE)
        return sharePef.getString(KEY_LANG, "en").toString()
    }

    fun getCurrentFlag(context: Context): Int {
        val sharePef = context.getSharedPreferences(APP_NAME, Activity.MODE_PRIVATE)
        return sharePef.getInt(
            KEY_FLAG,
           R.drawable.ic_flag_english
        )
    }

    fun setCurrentFlag(flag: Int, context: Context) {
        val sharePef = context.getSharedPreferences(APP_NAME, Activity.MODE_PRIVATE)
        val editor = sharePef.edit()
        editor.putInt(KEY_FLAG, flag)
        editor.apply()
    }


}