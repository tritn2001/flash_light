package com.lutech.flashlight.camera

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Handler
import com.lutech.flashlight.ads.Constants.DEFAULT_BRIGHTNESS_LEVEL
import com.lutech.flashlight.ads.Constants.MIN_BRIGHTNESS_LEVEL
import com.lutech.flashlight.util.Events
import com.lutech.flashlight.util.config
import org.greenrobot.eventbus.EventBus

internal class CameraFlash(
    private val context: Context,
    private var cameraTorchListener: CameraTorchListener? = null,
) {
    private val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraId: String? = null

    private var isTiramisuPlus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU


    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            cameraTorchListener?.onTorchEnabled(enabled)
        }

        override fun onTorchModeUnavailable(cameraId: String) {
            cameraTorchListener?.onTorchUnavailable()
        }
    }

    init {
        try {
            cameraId = manager.cameraIdList[0] ?: "0"
        } catch (e: Exception) {
        }
    }

    fun toggleFlashlight(enable: Boolean) {
        try {
            if (supportsBrightnessControl() && enable) {
                val brightnessLevel = getCurrentBrightnessLevel()
                changeTorchBrightness(brightnessLevel)
            } else {
                manager.setTorchMode(cameraId!!, enable)
            }
        } catch (e: Exception) {
            val mainRunnable = Runnable {
                EventBus.getDefault().post(Events.CameraUnavailable())
            }
            Handler(context.mainLooper).post(mainRunnable)
        }
    }

    fun changeTorchBrightness(level: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            manager.turnOnTorchWithStrengthLevel(cameraId!!, level)
        }
    }

    fun getMaximumBrightnessLevel(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val characteristics = manager.getCameraCharacteristics(cameraId!!)
            characteristics.get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL)
                ?: MIN_BRIGHTNESS_LEVEL
        } else {
            MIN_BRIGHTNESS_LEVEL
        }
    }

    fun supportsBrightnessControl(): Boolean {
        val maxBrightnessLevel = getMaximumBrightnessLevel()
        return maxBrightnessLevel > MIN_BRIGHTNESS_LEVEL
    }

    fun getCurrentBrightnessLevel(): Int {
        var brightnessLevel = context.config.brightnessLevel
        if (brightnessLevel == DEFAULT_BRIGHTNESS_LEVEL) {
            brightnessLevel = getMaximumBrightnessLevel()
        }
        return brightnessLevel
    }

    fun initialize() {
        manager.registerTorchCallback(torchCallback, Handler(context.mainLooper))
    }

    fun unregisterListeners() {
        manager.unregisterTorchCallback(torchCallback)
    }

    fun release() {
        cameraTorchListener = null
    }
}
