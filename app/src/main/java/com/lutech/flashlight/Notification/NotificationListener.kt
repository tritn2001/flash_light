package com.lutech.flashlight.Notification

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.provider.Telephony
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.lutech.flashlight.ads.Constants
import com.lutech.flashlight.camera.CameraTorchListener
import com.lutech.flashlight.camera.MyCameraImpl
import com.lutech.flashlight.camera.MyCameraImpl.Companion.newInstance
import com.lutech.flashlight.util.MySharePreference
import org.greenrobot.eventbus.EventBus


class NotificationListener : NotificationListenerService() {
    private var mySharePreference: MySharePreference? = null
    private var mCameraImpl: MyCameraImpl? = null
    private var eventBus: EventBus? = null

    private var mIsFlashlightOn = false
    var mContext: Context? = null
    override fun onListenerConnected() {
        Log.i(TAG, "Notification Listener connected")
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        mySharePreference = MySharePreference(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val isAlertSms = mySharePreference!!.getFlashAlert(Constants.ALERT_SMS)!!.isStatusChecked
        val isAlertNotifi =
            mySharePreference!!.getFlashAlert(Constants.ALERT_NOTIFICATION)!!.isStatusChecked
        var mIsFlashlightOn: Boolean
        eventBus = EventBus.getDefault()
        if (!isAlertSms && !isAlertNotifi) {
            return
        }
        Log.d(TAG, "onNotificationPosted: " + sbn.packageName)

        if (sbn.packageName == "sms_default_application" || sbn.packageName == "com.google.android.apps.messaging") {
            if (isAlertSms) {
                Log.d(TAG, "onNotificationPosted:setupSMS")
                setupCameraImpl(Constants.ALERT_SMS)
            }
        } else {
            Log.d(TAG, "onNotificationPosted: " + isAlertNotifi)

            if (isAlertNotifi) {
                Log.d(TAG, "onNotificationPosted:setUpNotifi")

                setupCameraImpl(Constants.ALERT_NOTIFICATION)
            }
        }

    }

    private fun setupCameraImpl(type: String) {
        mCameraImpl = newInstance(this, object : CameraTorchListener {
            override fun onTorchEnabled(isEnabled: Boolean) {

            }

            override fun onTorchUnavailable() {
                mCameraImpl!!.onCameraNotAvailable()
                Log.d(TAG, "onTorchUnavailable: ")
            }
        }, type,false)
        mCameraImpl!!.handleCameraSetup()
        mIsFlashlightOn = mCameraImpl!!.toggleStroboscope()
        Handler().postDelayed({
            mCameraImpl?.stopStroboscope()
        }, 5000)

    }

    @Nullable
    fun getDefaultSmsAppPackageName(@NonNull context: Context): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) try {
            return Telephony.Sms.getDefaultSmsPackage(context)
        } catch (e: Throwable) {
        }
        val intent = Intent(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_DEFAULT).setType("vnd.android-dir/mms-sms")
        val resolveInfoList = context.packageManager.queryIntentActivities(intent, 0)
        return if (!resolveInfoList.isEmpty()) resolveInfoList[0].activityInfo.packageName else null
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
    }

    private fun releaseCamera() {
        if (mCameraImpl != null) {
            mCameraImpl!!.releaseCamera()
            mCameraImpl = null
        }
    }

    companion object {
        private const val TAG = "NotificationListener"

    }

}