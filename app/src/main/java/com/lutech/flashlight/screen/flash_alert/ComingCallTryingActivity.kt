package com.lutech.flashlight.screen.flash_alert

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.lutech.flashlight.R
import com.lutech.flashlight.ads.Constants
import com.lutech.flashlight.camera.CameraTorchListener
import com.lutech.flashlight.camera.MyCameraImpl
import com.lutech.flashlight.data.FlashAlert
import com.lutech.flashlight.util.MySharePreference
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

        val zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        val zoomOut = AnimationUtils.loadAnimation(this, R.anim.zoom_out)

        ivAcceptCall.startAnimation(zoomIn)

        zoomIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(arg0: Animation?) {
            }

            override fun onAnimationRepeat(arg0: Animation?) {
            }

            override fun onAnimationEnd(arg0: Animation?) {
                ivAcceptCall.startAnimation(zoomOut)
            }
        })

        zoomOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(arg0: Animation?) {
            }

            override fun onAnimationRepeat(arg0: Animation?) {
            }

            override fun onAnimationEnd(arg0: Animation?) {
                ivAcceptCall.startAnimation(zoomIn)
            }
        })

    }

    private fun handleEvent() {
        layoutContainer.setOnClickListener {
            finish()
        }
    }


    private fun setupCameraImpl() {

        mCameraImpl = MyCameraImpl.newInstance(this, object : CameraTorchListener {
            override fun onTorchEnabled(isEnabled: Boolean) {
                if (mCameraImpl!!.supportsBrightnessControl()) {
                }
            }

            override fun onTorchUnavailable() {
                mCameraImpl!!.onCameraNotAvailable()
            }
        }, Constants.ALERT_CALL_PHONE,false)

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
        releaseCamera()
    }

    override fun onStart() {
        super.onStart()
//        mBus!!.register(this)

        if (mCameraImpl == null) {
            setupCameraImpl()
        }
    }
}