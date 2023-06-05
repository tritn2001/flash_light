package com.lutech.flashlight.receiver

import android.content.Context
import android.hardware.Camera
import androidx.annotation.RequiresApi
import android.os.Build
import android.media.AudioManager
import android.hardware.camera2.CameraManager
import android.telecom.Call
import android.telecom.VideoProfile
import com.lutech.flashlight.receiver.PhoneCallManager

class PhoneCallManager(context: Context) {
    private var context: Context?
    private var audioManager: AudioManager?
    private val camera: Camera? = null
    var mCameraManager: CameraManager? = null

    init {
        this.context = context
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    fun answer() {
        if (call != null) {
            call!!.answer(VideoProfile.STATE_AUDIO_ONLY)
            openSpeaker()
        }
    }

    fun disconnect() {
        if (call != null) {
            call!!.disconnect()
        }
    }

    fun openSpeaker() {
        if (audioManager != null) {
            audioManager!!.mode = AudioManager.MODE_IN_CALL
            audioManager!!.isSpeakerphoneOn = true
        }
    }

    fun TurnOffSpeaker() {
        if (audioManager != null) {
            audioManager!!.mode = AudioManager.MODE_NORMAL
            audioManager!!.isSpeakerphoneOn = false
        }
    }

    fun destroy() {
        call = null
        context = null
        audioManager = null
    }

    companion object {
        var call: Call? = null
    }
}