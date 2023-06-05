package com.lutech.flashlight.screen.flash_light

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.lutech.flashlight.R
import com.lutech.flashlight.ads.Constants
import com.lutech.flashlight.ads.Utils
import com.lutech.flashlight.camera.CameraTorchListener
import com.lutech.flashlight.camera.MyCameraImpl
import com.lutech.flashlight.data.FlashAlert
import com.lutech.flashlight.util.MySharePreference
import com.lutech.phonetracker.util.settings
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.fragment_flash_light.*
import kotlinx.android.synthetic.main.fragment_flash_light.view.*
import org.greenrobot.eventbus.EventBus


class FlashLightFragment : Fragment() {

    private var mCameraImpl: MyCameraImpl? = null

    private var mBus: EventBus? = null

    private var mIsFlashlightOn = false

    private val MAX_STROBO_DELAY = 2000L

    private val MIN_STROBO_DELAY = 10L

    private lateinit var mContext: Context

    private lateinit var mMedia: MediaPlayer

    private var mFlashAlert: FlashAlert? = null

    private var mySharePreference: MySharePreference? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_flash_light, container, false)
        mContext = requireActivity()
        mySharePreference = MySharePreference(mContext)
        mFlashAlert = mySharePreference!!.getFlashAlert(Constants.ALERT_NORMAL)

        mMedia = Utils.initMediaPlayer(mContext, R.raw.sound_switch_on)
        mBus = EventBus.getDefault()

        initView(v)
        handleEvent(v)

        return v
    }

    private fun initView(view: View) {

        view.apply {
            sw_normal.isChecked = mContext!!.settings.normal
            sw_silent.isChecked = mContext!!.settings.silent
            sw_vibrate.isChecked = mContext!!.settings.vibrate

        }

    }

    private fun handleEvent(v: View) {
        v.apply {
            btnTurnOnFlash.setOnClickListener {
                handleFlash()
            }

            btnTurnOffFlash.setOnClickListener {
                handleFlash()
            }
            sbOnTime.max = (MAX_STROBO_DELAY - MIN_STROBO_DELAY + 10).toFloat()

            val mProgressOn = mFlashAlert!!.stroboscopeProgressOn.toFloat()
            sbOnTime.setProgress(mProgressOn)
            secondOnTime.text =
                "" + mProgressOn.toDouble() / 1000 + " " + getString(R.string.txt_second)

            sbOnTime.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams) {
                    if (seekParams.progress == -1) {
                        sbOnTime.setProgress(0F)
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

                    secondOnTime.text = mSeconds + " " + getString(R.string.txt_second)
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {}
            }
            val mProgressOff = mFlashAlert!!.stroboscopeProgressOff.toFloat()

            sbOffTime.max = (MAX_STROBO_DELAY - MIN_STROBO_DELAY + 10).toFloat()
            sbOffTime.setProgress(mProgressOff)

            secondOffTime.text =
                "" + mProgressOff.toDouble() / 1000 + " " + getString(R.string.txt_second)


            sbOffTime.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams) {
                    if (seekParams.progress == -1) {
                        sbOffTime.setProgress(0F)
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


                    secondOffTime.text = mSeconds + " " + getString(R.string.txt_second)
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {}
            }

            sw_normal.setOnCheckedChangeListener { _, b -> mContext!!.settings!!.normal = b }
            sw_vibrate.setOnCheckedChangeListener { _, b -> mContext!!.settings!!.vibrate = b }
            sw_silent.setOnCheckedChangeListener { _, b -> mContext!!.settings!!.silent = b }

        }
    }

    private fun saveAlertFlash(flashAlert: FlashAlert) {
        mySharePreference!!.saveFlashAlert(Constants.ALERT_NORMAL, flashAlert!!)
    }

    private fun handleFlash() {
        Log.d("=====>333333333333", "handleFlash: " + mContext!!.settings.silent)

        if (!mContext!!.settings!!.silent) {
            if (mMedia.isPlaying) {
                mMedia.stop()
            }
            mMedia.start()
        }
        if (mContext!!.settings!!.vibrate) {
            Utils.vibrate(mContext)
        }
        mIsFlashlightOn = mCameraImpl!!.toggleStroboscope()
        if (mIsFlashlightOn) {
            btnTurnOnFlash.visibility = View.GONE
            btnTurnOffFlash.visibility = View.VISIBLE
        } else {
            btnTurnOnFlash.visibility = View.VISIBLE
            btnTurnOffFlash.visibility = View.GONE
        }
        layoutLock.isVisible = btnTurnOnFlash.isGone
    }

    private fun checkState(isEnabled: Boolean) {
        if (isEnabled) {
            enableFlashlight()
        } else {
            disableFlashlight()
        }
    }

    private fun enableFlashlight() {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mIsFlashlightOn = true
    }

    private fun setupCameraImpl() {

        mCameraImpl = MyCameraImpl.newInstance(mContext!!, object : CameraTorchListener {
            override fun onTorchEnabled(isEnabled: Boolean) {
                if (mCameraImpl!!.supportsBrightnessControl()) {
                }
            }

            override fun onTorchUnavailable() {
                mCameraImpl!!.onCameraNotAvailable()
            }
        }, Constants.ALERT_NORMAL)
//        if (config.turnFlashlightOn) {
//            mCameraImpl!!.enableFlashlight()
//        }
    }

    private fun disableFlashlight() {

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mIsFlashlightOn = false
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