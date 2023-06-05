package com.lutech.flashlight.screen.flash_alert

import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.lutech.flashlight.R
import com.lutech.flashlight.ads.Constants
import com.lutech.flashlight.ads.Utils
import com.lutech.flashlight.camera.CameraTorchListener
import com.lutech.flashlight.camera.MyCameraImpl
import com.lutech.flashlight.data.FlashAlert
import com.lutech.flashlight.util.MySharePreference
import com.lutech.phonetracker.util.settings
import kotlinx.android.synthetic.main.activity_coming_call_trying.*
import kotlinx.android.synthetic.main.fragment_flash_light.*
import org.greenrobot.eventbus.EventBus

class ComingCallTryingActivity : AppCompatActivity() {

    private var mCameraImpl: MyCameraImpl? = null

    private var mBus: EventBus? = null

    private var mIsFlashlightOn = false


    private var mySharePreference: MySharePreference? = null

    private var mFlashAlert: FlashAlert? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coming_call_trying)

        initView()
        handleEvent()
    }

    private fun initView() {
        mySharePreference = MySharePreference(this)
        mFlashAlert = mySharePreference!!.getFlashAlert(Constants.ALERT_NORMAL)
        mBus = EventBus.getDefault()
    }

    private fun handleEvent() {
        layoutContainer.setOnClickListener {
            finish()
        }
    }



    private fun setupCameraImpl() {

        mCameraImpl = MyCameraImpl.newInstance(this!!, object : CameraTorchListener {
            override fun onTorchEnabled(isEnabled: Boolean) {
                if (mCameraImpl!!.supportsBrightnessControl()) {
                }
            }

            override fun onTorchUnavailable() {
                mCameraImpl!!.onCameraNotAvailable()
            }
        }, Constants.ALERT_CALL_PHONE)

        mIsFlashlightOn = mCameraImpl!!.toggleStroboscope()

    }

    private fun releaseCamera() {
        mCameraImpl?.releaseCamera()
        mCameraImpl = null
    }

    override fun onResume() {
        super.onResume()
        mCameraImpl!!.handleCameraSetup()

    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera() }

    override fun onStart() {
        super.onStart()
//        mBus!!.register(this)

        if (mCameraImpl == null) {
            setupCameraImpl()
        }
    }
}