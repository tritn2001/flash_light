package com.lutech.flashlight.camera

import android.content.Context
import android.os.Handler
import android.util.Log
import com.lutech.flashlight.ads.Constants
import com.lutech.flashlight.ads.Utils
import com.lutech.flashlight.data.FlashAlert
import com.lutech.flashlight.screen.flash_light.FlashLightFragment
import com.lutech.flashlight.util.Events
import com.lutech.flashlight.util.MySharePreference
import com.lutech.flashlight.util.config
import com.lutech.phonetracker.util.settings


import org.greenrobot.eventbus.EventBus

class MyCameraImpl private constructor(
    val context: Context,
    private var cameraTorchListener: CameraTorchListener? = null,
    mType: String,
    private var isFlashLightFragment: Boolean
) {
    var stroboFrequencyOn = 1000L
    var stroboFrequencyOff = 1000L

    companion object {
        var isFlashlightOn = false

        private var u = 200L // The length of one dit (Time unit)
        private val SOS = arrayListOf(
            u,
            u,
            u,
            u,
            u,
            u * 3,
            u * 3,
            u,
            u * 3,
            u,
            u * 3,
            u * 3,
            u,
            u,
            u,
            u,
            u,
            u * 7
        )

        private var shouldEnableFlashlight = false
        private var shouldEnableStroboscope = false
        private var shouldEnableSOS = false
        private var isStroboSOS = false     // are we sending SOS, or casual stroboscope?

        private var cameraFlash: CameraFlash? = null

        @Volatile
        private var shouldStroboscopeStop = false

        @Volatile
        private var isStroboscopeRunning = false

        @Volatile
        private var isSOSRunning = false

        fun newInstance(
            context: Context,
            cameraTorchListener: CameraTorchListener? = null,
            mType: String, isFlashLightFragment: Boolean
        ) = MyCameraImpl(context, cameraTorchListener, mType, isFlashLightFragment)
    }

    init {
        handleCameraSetup()
        val flashAlert = MySharePreference(context).getFlashAlert(mType)
        stroboFrequencyOn = flashAlert!!.stroboscopeOn
        stroboFrequencyOff = flashAlert.stroboscopeOff

    }

    fun toggleFlashlight() {
        isFlashlightOn = !isFlashlightOn
        checkFlashlight()
    }

    fun toggleStroboscope(): Boolean {
        if (!isFlashLightFragment) {
            if (context.settings.notFlashWhileTheScreenOn) {
                if (Utils.isScreenOn(context)) {
                    return false
                }
            }
            if (context.settings.saveBattery) {
                if (Utils.isLowBattery(context)) {
                    return false
                }
            }
        }
        handleCameraSetup()

        if (isSOSRunning) {
            stopSOS()
            shouldEnableStroboscope = true
            return true
        }

        isStroboSOS = false
        if (!isStroboscopeRunning) {
            disableFlashlight()
        }

        cameraFlash!!.unregisterListeners()

        if (!tryInitCamera()) {
            return false
        }

        return if (isStroboscopeRunning) {
            stopStroboscope()
            false
        } else {
            Thread(stroboscope).start()
            true
        }
    }

    fun stopStroboscope() {
        shouldStroboscopeStop = true
        EventBus.getDefault().post(Events.StopStroboscope())
    }

    fun toggleSOS(): Boolean {
        handleCameraSetup()

        if (isStroboscopeRunning) {
            stopStroboscope()
            shouldEnableSOS = true
            return true
        }

        isStroboSOS = true
        if (isStroboscopeRunning) {
            stopStroboscope()
        }

        if (!tryInitCamera()) {
            return false
        }

        if (isFlashlightOn) {
            disableFlashlight()
        }

        cameraFlash!!.unregisterListeners()

        return if (isSOSRunning) {
            stopSOS()
            false
        } else {
            Thread(stroboscope).start()
            true
        }
    }

    fun stopSOS() {
        shouldStroboscopeStop = true
        EventBus.getDefault().post(Events.StopSOS())
    }

    private fun tryInitCamera(): Boolean {
        handleCameraSetup()
        if (cameraFlash == null) {
            return false
        }
        return true
    }

    fun handleCameraSetup() {
        try {
            if (cameraFlash == null) {
                cameraFlash = CameraFlash(context, cameraTorchListener)
            }
        } catch (e: Exception) {
            EventBus.getDefault().post(Events.CameraUnavailable())
        }
    }

    private fun checkFlashlight() {
        handleCameraSetup()

        if (isFlashlightOn) {
            enableFlashlight()
        } else {
            disableFlashlight()
        }
    }

    fun enableFlashlight() {
        shouldStroboscopeStop = true
        if (isStroboscopeRunning || isSOSRunning) {
            shouldEnableFlashlight = true
            return
        }

        try {
            cameraFlash!!.initialize()
            cameraFlash!!.toggleFlashlight(true)
        } catch (e: Exception) {
            disableFlashlight()
        }

        val mainRunnable = Runnable { stateChanged(true) }
        Handler(context.mainLooper).post(mainRunnable)
    }

    private fun disableFlashlight() {
        if (isStroboscopeRunning || isSOSRunning) {
            return
        }

        try {
            cameraFlash!!.toggleFlashlight(false)
        } catch (e: Exception) {

            disableFlashlight()
        }
        stateChanged(false)
    }

    private fun stateChanged(isEnabled: Boolean) {
        isFlashlightOn = isEnabled
        EventBus.getDefault().post(Events.StateChanged(isEnabled))
    }

    fun releaseCamera() {
        cameraFlash?.unregisterListeners()

        if (isFlashlightOn) {
            disableFlashlight()
        }

        cameraFlash?.release()
        cameraFlash = null
        cameraTorchListener = null

        isFlashlightOn = false
        shouldStroboscopeStop = true
    }

    private val stroboscope = Runnable {
        if (isStroboscopeRunning || isSOSRunning) {
            return@Runnable
        }

        shouldStroboscopeStop = false
        if (isStroboSOS) {
            isSOSRunning = true
        } else {
            isStroboscopeRunning = true
        }

        var sosIndex = 0
        handleCameraSetup()
        while (!shouldStroboscopeStop) {
            try {
                cameraFlash!!.toggleFlashlight(true)
                val onDuration = if (isStroboSOS) SOS[sosIndex++ % SOS.size] else stroboFrequencyOn
                Log.d("=======>31211111111111", ": " + onDuration + isStroboSOS)
                Thread.sleep(onDuration)
                cameraFlash!!.toggleFlashlight(false)
                val offDuration =
                    if (isStroboSOS) SOS[sosIndex++ % SOS.size] else stroboFrequencyOff
                Log.d("=======>31211111111111", ": " + offDuration + isStroboSOS)

                Thread.sleep(offDuration)
            } catch (e: Exception) {
                shouldStroboscopeStop = true

                Log.d("=======>31211111111111", ": " + e.message)

            }
        }

        // disable flash immediately if stroboscope is stopped and normal flash mode is disabled
        if (shouldStroboscopeStop && !shouldEnableFlashlight) {
            handleCameraSetup()
            cameraFlash!!.toggleFlashlight(false)
            cameraFlash!!.release()
            cameraFlash = null
        }

        shouldStroboscopeStop = false
        if (isStroboSOS) {
            isSOSRunning = false
        } else {
            isStroboscopeRunning = false
        }

        when {
            shouldEnableFlashlight -> {
                enableFlashlight()
                shouldEnableFlashlight = false
            }
            shouldEnableSOS -> {
                toggleSOS()
                shouldEnableSOS = false
            }
            shouldEnableStroboscope -> {
                toggleStroboscope()
                shouldEnableStroboscope = false
            }
        }
    }

    fun getMaximumBrightnessLevel(): Int {
        return cameraFlash!!.getMaximumBrightnessLevel()
    }

    fun getCurrentBrightnessLevel(): Int {
        return cameraFlash!!.getCurrentBrightnessLevel()
    }

    fun supportsBrightnessControl(): Boolean {
        return cameraFlash!!.supportsBrightnessControl()
    }

    fun updateBrightnessLevel(level: Int) {
        cameraFlash!!.changeTorchBrightness(level)
    }

    fun onCameraNotAvailable() {
        disableFlashlight()
    }
}
