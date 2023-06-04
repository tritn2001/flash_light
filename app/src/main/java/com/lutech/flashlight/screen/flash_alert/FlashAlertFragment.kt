package com.lutech.flashlight.screen.flash_alert

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.lutech.flashlight.R
import com.lutech.flashlight.ads.Constants
import com.lutech.flashlight.ads.Utils
import com.lutech.flashlight.callback.HandleEventCheckPermissionListener
import com.lutech.flashlight.camera.CameraTorchListener
import com.lutech.flashlight.camera.MyCameraImpl
import com.lutech.flashlight.util.CustomDialog
import com.lutech.flashlight.util.PermissionManager
import com.lutech.flashlight.util.config
import com.lutech.phonetracker.util.settings
import kotlinx.android.synthetic.main.fragment_flash_alert.*
import org.greenrobot.eventbus.EventBus


class FlashAlertFragment : Fragment() {

    private lateinit var permissionManager: PermissionManager

    private lateinit var mContext: Context

    private var mType: Int? = null

    private var mDialog: Dialog? = null

    private var mCameraImpl: MyCameraImpl? = null

    private var mBus: EventBus? = null

    private var mIsFlashlightOn = false

    private val MAX_STROBO_DELAY = 2000L

    private val MIN_STROBO_DELAY = 10L


    private lateinit var mMedia: MediaPlayer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_flash_alert, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext = requireContext()
        permissionManager = PermissionManager(mContext)
        mMedia = Utils.initMediaPlayer(mContext, R.raw.sound_switch_on)
        mBus = EventBus.getDefault()



        initView(view)
        handleEvent(view)
    }

    private fun initView(view: View) {
        view.apply {
            switchOnOff.isChecked = mContext.settings.statusAlert
        }

    }

    private fun handleEvent(view: View) {

        view.apply {

            ftTurnOnComingCall.setOnClickListener {
                checkPermission(Constants.CALL_PHONE)
            }

            ftTurnOnSms.setOnClickListener {
                checkPermission(Constants.SMS)
            }
            ftTurnOnNoti.setOnClickListener {
                checkPermission(Constants.NOTIFICATION)
            }

            switchOnOff.setOnCheckedChangeListener { _, b -> mContext!!.settings!!.statusAlert = b }

        }
    }


    private fun checkPermission(type: Int) {
        mType = type
        if (!permissionManager.isReadPhoneGranted || !permissionManager.isCameraGranted) {
            if (mDialog != null) {
                mDialog?.show()
                return
            }
            mDialog = CustomDialog(mContext).dialogCheckPermissionCallPhoneState(object :
                HandleEventCheckPermissionListener {
                override fun onAcceptPermissions() {
                    gotoAlertFlashSetting()
                }

                override fun onDeniedPermissions() {
                }
            })
            mDialog?.show()
        } else {
            gotoAlertFlashSetting()
        }

    }

    private fun gotoAlertFlashSetting() {
        val mIntent = Intent(mContext, SettingsFlashAlertActivity::class.java)
        mIntent.putExtra(Constants.TYPE_ALERT, mType)
        mContext.startActivity(mIntent)
        mType = null
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
        })
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