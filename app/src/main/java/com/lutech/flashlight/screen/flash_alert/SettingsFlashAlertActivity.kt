package com.lutech.flashlight.screen


import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lutech.flashlight.R
import com.lutech.flashlight.ads.Constants
import com.lutech.flashlight.ads.Utils
import com.lutech.flashlight.camera.CameraTorchListener
import com.lutech.flashlight.camera.MyCameraImpl
import com.lutech.flashlight.data.FlashAlert
import com.lutech.flashlight.screen.flash_alert.AllowPermissionActivityActivity
import com.lutech.flashlight.screen.flash_alert.InstallingActivity
import com.lutech.flashlight.util.CustomDialog
import com.lutech.flashlight.util.MySharePreference
import com.lutech.flashlight.util.PermissionManager
import com.lutech.phonetracker.util.settings
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.activity_settings_flash_alert.*
import kotlinx.android.synthetic.main.fragment_flash_light.*
import kotlinx.android.synthetic.main.fragment_flash_light.view.*
import org.greenrobot.eventbus.EventBus

class SettingsFlashAlertActivity : AppCompatActivity() {

    private var mType: String? = null

    private lateinit var permissionManager: PermissionManager

    private var mCameraImpl: MyCameraImpl? = null

    private var mBus: EventBus? = null

    private var mIsFlashlightOn = false

    private val MAX_STROBO_DELAY = 2000L

    private val MIN_STROBO_DELAY = 10L

    private var mFlashAlert: FlashAlert? = null

    private lateinit var mySharePreference: MySharePreference

    private lateinit var mMedia: MediaPlayer

    private var dialogLoading: Dialog? = null

    private var mIntent: Intent? = null


    private var startActivityResult = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            mFlashAlert?.isStatusChecked = true
            mySharePreference.saveFlashAlert(mType, mFlashAlert!!)
            openDialogLoading()
        }
        swStatus.isChecked = mFlashAlert!!.isStatusChecked

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_flash_alert)

        mySharePreference = MySharePreference(this)
        permissionManager = PermissionManager(this)

        mType = intent.getStringExtra(Constants.TYPE_ALERT)
        mFlashAlert = mySharePreference.getFlashAlert(mType)

        mMedia = Utils.initMediaPlayer(this, R.raw.sound_switch_on)
        mBus = EventBus.getDefault()

        initView()

        handleEvent()
    }

    private fun initView() {

        dialogLoading = CustomDialog(this).dialogInstalling()

        swStatus.isChecked = mFlashAlert!!.isStatusChecked

        var content: String? = null
        var imageStart: Int? = null

        when (mType) {
            Constants.ALERT_CALL_PHONE -> {
                content = getString(R.string.txt_turn_on_for_incoming_calls)
                imageStart = R.drawable.ic_call_coming
            }
            Constants.ALERT_SMS -> {
                content = getString(R.string.txt_turn_on_for_sms)
                imageStart = R.drawable.ic_mail
            }
            Constants.ALERT_NOTIFICATION -> {
                content = getString(R.string.txt_turn_on_for_amp_notification)
                imageStart = R.drawable.ic_notification_2
            }
        }

        tvContentType.text = content
        ivType.setImageResource(imageStart!!)
    }

    private fun handleEvent() {
        swStatus.setOnCheckedChangeListener { _, b ->
            if (b) {
                if (mIsFlashlightOn) {
                    handleFlash()
                    btnTest.text = getString(R.string.txt_test)
                }
                mIntent = null
                if (permissionManager!!.isNotificationServiceRunning) {
                    openDialogLoading()
                    mFlashAlert!!.isStatusChecked = b
                } else {
                    mIntent = Intent(this, AllowPermissionActivityActivity::class.java)
                    startActivityResult.launch(mIntent)
                }
            } else {
                mFlashAlert?.isStatusChecked = b
            }
            mySharePreference.saveFlashAlert(mType, mFlashAlert!!)
        }



        sbAlertOnTime.max = (MAX_STROBO_DELAY - MIN_STROBO_DELAY + 10).toFloat()
        val mProgressOn = mFlashAlert!!.stroboscopeProgressOn.toFloat()
        sbAlertOnTime.setProgress(mProgressOn)
        secondAlertOnTime.text =
            "" + mProgressOn.toDouble() / 1000 + " " + getString(R.string.txt_second)

        sbAlertOnTime.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {
                if (seekParams.progress == -1) {
                    sbAlertOnTime.setProgress(0F)
                }
                var frequency = seekParams.progress.toLong()
                if (frequency == 9L) {
                    frequency = 0
                }
                mCameraImpl?.stroboFrequencyOn = frequency
                mFlashAlert!!.stroboscopeOn = frequency
                mFlashAlert!!.stroboscopeProgressOn = seekParams.progress
                saveAlertFlash(mFlashAlert!!)


                val mSeconds = (frequency.toDouble() / 1000).toString()

                secondAlertOnTime.text = mSeconds + " " + getString(R.string.txt_second)
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {}
        }

        val mProgressOff = mFlashAlert!!.stroboscopeProgressOff.toFloat()

        sbAlertOffTime.max = (MAX_STROBO_DELAY - MIN_STROBO_DELAY + 10).toFloat()
        sbAlertOffTime.setProgress(mProgressOff)

        secondAlertOffTime.text =
            "" + mProgressOff.toDouble() / 1000 + " " + getString(R.string.txt_second)

        sbAlertOffTime.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {
                if (seekParams.progress == -1) {
                    sbAlertOffTime.setProgress(0F)
                }
                var frequency = seekParams.progress.toLong()
                if (frequency == 9L) {
                    frequency = 0
                }
                mCameraImpl?.stroboFrequencyOff = frequency
                mFlashAlert!!.stroboscopeOff = frequency
                mFlashAlert!!.stroboscopeProgressOff = seekParams.progress
                saveAlertFlash(mFlashAlert!!)
                val mSeconds = (frequency.toDouble() / 1000).toString()
                secondAlertOffTime.text = mSeconds + " " + getString(R.string.txt_second)
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {}
        }

        btnTest.setOnClickListener {
            handleFlash()
            if (mIsFlashlightOn) {
                btnTest.text = getString(R.string.txt_stop)
            } else {
                btnTest.text = getString(R.string.txt_test)
            }
        }
    }

    private fun openDialogLoading() {
        if (mType != Constants.ALERT_CALL_PHONE) {
            CustomDialog(this).dialogInstallSuccess().show()
            return
        }
        dialogLoading?.show()
        Handler().postDelayed({
            mIntent = Intent(this, InstallingActivity::class.java)
            startActivityResult.launch(mIntent)
            dialogLoading?.dismiss()
        }, 1200)
    }

    private fun saveAlertFlash(flashAlert: FlashAlert) {
        mySharePreference!!.saveFlashAlert(mType, flashAlert!!)
    }

    private fun handleFlash() {

        if (settings!!.silent) {
            if (mMedia.isPlaying) {
                mMedia.stop()
            }
            mMedia.start()
        }
        if (settings!!.vibrate) {
            Utils.vibrate(this)
        }
        mIsFlashlightOn = mCameraImpl!!.toggleStroboscope()

    }

    private fun setupCameraImpl() {
        if (mType == null) {
            mType = intent.getStringExtra(Constants.TYPE_ALERT)
        }
        mCameraImpl = MyCameraImpl.newInstance(this!!, object : CameraTorchListener {
            override fun onTorchEnabled(isEnabled: Boolean) {
                if (mCameraImpl!!.supportsBrightnessControl()) {
                }
            }

            override fun onTorchUnavailable() {
                mCameraImpl!!.onCameraNotAvailable()
            }
        }, mType!!)
//        if (config.turnFlashlightOn) {
//            mCameraImpl!!.enableFlashlight()
//        }
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
        mMedia.release()
    }

    override fun onStart() {
        super.onStart()
//        mBus!!.register(this)

        if (mCameraImpl == null) {
            setupCameraImpl()
        }
    }
}