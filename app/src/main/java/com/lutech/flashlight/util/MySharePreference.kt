package com.lutech.flashlight.util

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.lutech.flashlight.ads.Constants
import com.lutech.flashlight.data.FlashAlert

class MySharePreference(var context: Context) {
    private var mSharedPreferences =
        context.getSharedPreferences(Constants.APP_NAME, Activity.MODE_PRIVATE)

    private var mEditor = mSharedPreferences.edit()

    companion object {
        var mySharePreferenceDB: MySharePreference? = null
        fun getInstance(context: Context): MySharePreference {
            if (mySharePreferenceDB == null) {
                mySharePreferenceDB = MySharePreference(context)
            }
            return mySharePreferenceDB!!
        }
    }


    fun setValueInt(key: String, value: Int) {
        mEditor.putInt(key, value)
        mEditor.apply()
    }

    fun setValueString(key: String, value: String) {
        mEditor.putString(key, value)
        mEditor.apply()
    }

    fun getValueString(key: String): String {
        return mSharedPreferences.getString(key, "").toString()
    }

    fun getValueInt(key: String): Int {
        return mSharedPreferences.getInt(key, 0)
    }

    fun getValueInt(key: String, default: Int): Int {
        return mSharedPreferences.getInt(key, default)
    }

    fun setValueBoolean(key: String, value: Boolean) {
        mEditor.putBoolean(key, value)
        mEditor.apply()
    }

    fun getValueBoolean(key: String): Boolean {
        return mSharedPreferences.getBoolean(key, false)
    }

    fun setValueLong(key: String, value: Long) {
        mEditor.putLong(key, value)
        mEditor.apply()
    }

    fun getValueLong(key: String, l: Long): Long {
        return mSharedPreferences.getLong(key, l)
    }

    fun putListString(key: String?, stringList: ArrayList<String>) {
        val myStringList = stringList.toTypedArray()
        mEditor.putString(key, TextUtils.join("‚‗‚", myStringList)).apply()
    }

    fun getListString(key: String?): java.util.ArrayList<String> {
        return java.util.ArrayList(
            listOf(
                *TextUtils.split(
                    mSharedPreferences.getString(key, ""),
                    "‚‗‚"
                )
            )
        )
    }

    fun saveFlashAlert(key: String?, flashAlert: FlashAlert) {
        mEditor.putString(key, Gson().toJson(flashAlert)).apply()
    }

    fun getFlashAlert(key: String?): FlashAlert? {
        val flashAlert = mSharedPreferences.getString(key, null) ?: return FlashAlert()
        return Gson().fromJson(flashAlert, FlashAlert::class.java)
    }

}