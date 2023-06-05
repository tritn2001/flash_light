package com.lutech.flashlight.util

import android.content.Context
import com.lutech.flashlight.ads.Constants.BRIGHTNESS_LEVEL
import com.lutech.flashlight.ads.Constants.DEFAULT_BRIGHTNESS_LEVEL
import com.lutech.flashlight.ads.Constants.STATUS_FLASH_ALERT
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_CALL_FREQUENCY_OFF
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_CALL_FREQUENCY_ON
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_CALL_PROGRESS_OFF
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_CALL_PROGRESS_ON
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_FREQUENCY_OFF
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_FREQUENCY_ON
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_NOTI_FREQUENCY_OFF
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_NOTI_FREQUENCY_ON
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_NOTI_PROGRESS_OFF
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_NOTI_PROGRESS_ON
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_PROGRESS_OFF
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_PROGRESS_ON
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_SMS_FREQUENCY_OFF
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_SMS_FREQUENCY_ON
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_SMS_PROGRESS_OFF
import com.lutech.flashlight.ads.Constants.STROBOSCOPE_SMS_PROGRESS_ON

class Config(context: Context) {

    private var prefs = MySharePreference(context)

    companion object {
        fun newInstance(context: Context) = Config(context)
    }


    var stroboscopeFrequencyOn: Long
        get() = prefs.getValueLong(STROBOSCOPE_FREQUENCY_ON, 1000L)
        set(stroboscopeFrequencyOn) = prefs?.setValueLong(
            STROBOSCOPE_FREQUENCY_ON,
            stroboscopeFrequencyOn
        )

    var stroboscopeFrequencyOff: Long
        get() = prefs.getValueLong(STROBOSCOPE_FREQUENCY_OFF, 1000L)
        set(stroboscopeFrequencyOff) = prefs?.setValueLong(
            STROBOSCOPE_FREQUENCY_OFF,
            stroboscopeFrequencyOff
        )


    var stroboscopeOnProgress: Int
        get() = prefs.getValueInt(STROBOSCOPE_PROGRESS_ON, 1000)
        set(stroboscopeOnFrequency) = prefs?.setValueInt(
            STROBOSCOPE_PROGRESS_ON,
            stroboscopeOnFrequency
        )

    var stroboscopeOffProgress: Int
        get() = prefs.getValueInt(STROBOSCOPE_PROGRESS_OFF, 1000)
        set(stroboscopeOffFrequency) = prefs?.setValueInt(
            STROBOSCOPE_PROGRESS_OFF,
            stroboscopeOffFrequency
        )


    var brightnessLevel: Int
        get() = prefs.getValueInt(BRIGHTNESS_LEVEL, DEFAULT_BRIGHTNESS_LEVEL)
        set(brightnessLevel) = prefs?.setValueInt(BRIGHTNESS_LEVEL, brightnessLevel)


}
