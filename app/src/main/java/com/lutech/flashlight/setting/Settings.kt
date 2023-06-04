package com.lutech.flashlight.setting

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.lutech.flashlight.util.unsafeLazy

class Settings(private val mContext: Context) {

    companion object {

        private const val SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_NAME"
        private var INSTANCE: Settings? = null

        fun getInstance(context: Context): Settings {
            return INSTANCE ?: Settings(context.applicationContext).apply { INSTANCE = this }
        }
    }

    private enum class Key {
        NORMAL,
        VIBRATE,
        SILENT,
        STATUS_ALERT
    }
    private val sharedPreferences by unsafeLazy {
        mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    var vibrate: Boolean
        get() = get(Key.VIBRATE, true)
        set(value) = set(Key.VIBRATE, value)

    var normal: Boolean
        get() = get(Key.NORMAL, true)
        set(value) = set(Key.NORMAL, value)

    var silent: Boolean
        get() = get(Key.SILENT, true)
        set(value) = set(Key.SILENT, value)

    var statusAlert :Boolean
        get() = get(Key.STATUS_ALERT, false)
        set(value) = set(Key.STATUS_ALERT, value)


    private fun get(key: Key, default: Int): Int {
        return sharedPreferences.getInt(key.name, default)
    }

    private fun set(key: Key, value: Int) {
        return sharedPreferences.edit()
            .putInt(key.name, value)
            .apply()
    }


    private fun get(key: Key, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key.name, default)
    }

    private fun set(key: Key, value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(key.name, value)
            .apply()
    }

}