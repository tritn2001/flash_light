package com.lutech.flashlight.receiver

import android.content.Intent
import android.os.IBinder
import android.telecom.InCallService
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.lutech.flashlight.ads.Constants
import com.lutech.flashlight.camera.CameraTorchListener
import com.lutech.flashlight.camera.MyCameraImpl
import com.lutech.flashlight.util.MySharePreference
import org.greenrobot.eventbus.EventBus


class PhoneCallService : InCallService() {

    private var mCameraImpl: MyCameraImpl? = null

    private var mBus: EventBus? = null

    private var mIsFlashlightOn = false


    private var mySharePreference: MySharePreference? = null

    private var telephonyManager: TelephonyManager? = null
    private var phoneStateListener: PhoneStateListener? = null

    override fun onCreate() {
        super.onCreate()

        mySharePreference = MySharePreference(this)
        mBus = EventBus.getDefault()
        setupCameraImpl()
        mCameraImpl!!.handleCameraSetup()

        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> // Handle incoming call
                        if (!mIsFlashlightOn) {
                            mIsFlashlightOn = mCameraImpl!!.toggleStroboscope()
                        }
                    TelephonyManager.CALL_STATE_OFFHOOK ->                         // Call answered
                        if (mIsFlashlightOn) {
                            mIsFlashlightOn = mCameraImpl!!.toggleStroboscope()
                        }
                    TelephonyManager.CALL_STATE_IDLE ->                         // Call ended
                        if (mIsFlashlightOn) {
                            mIsFlashlightOn = mCameraImpl!!.toggleStroboscope()
                        }
                }
            }
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

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
        telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun releaseCamera() {
        mCameraImpl?.releaseCamera()
        mCameraImpl = null
    }
}