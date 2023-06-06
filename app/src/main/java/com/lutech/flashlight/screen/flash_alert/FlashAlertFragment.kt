package com.lutech.flashlight.screen.flash_alert

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lutech.flashlight.R
import com.lutech.flashlight.ads.Constants
import com.lutech.flashlight.callback.HandleEventCheckPermissionListener
import com.lutech.flashlight.screen.SettingsFlashAlertActivity
import com.lutech.flashlight.util.CustomDialog
import com.lutech.flashlight.util.MySharePreference
import com.lutech.flashlight.util.PermissionManager
import com.lutech.phonetracker.util.settings
import kotlinx.android.synthetic.main.fragment_flash_alert.*

class FlashAlertFragment : Fragment() {

    private lateinit var permissionManager: PermissionManager

    private lateinit var mContext: Context

    private var mType: String? = null

    private var mDialog: Dialog? = null

    private var mView: View? = null
    private var mySharePreference: MySharePreference? = null
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
        mySharePreference = MySharePreference(mContext)

        initView(view)
        handleEvent(view)
        mView = view
    }


    private fun initView(view: View) {
        view.apply {
            initSwStatus()
        }

    }

    private fun initSwStatus() {
        switchOnOff.isChecked =
            mySharePreference!!.getFlashAlert(Constants.ALERT_CALL_PHONE)!!.isStatusChecked
        tvStatus.text = if (switchOnOff.isChecked) {
            getString(R.string.txt_status_on)
        } else {
            getString(R.string.txt_status_off)
        }
    }

    private fun handleEvent(view: View) {

        view.apply {

            ftTurnOnComingCall.setOnClickListener {
                checkPermission(Constants.ALERT_CALL_PHONE)
            }

            ftTurnOnSms.setOnClickListener {
                checkPermission(Constants.ALERT_SMS)
            }
            ftTurnOnNoti.setOnClickListener {
                checkPermission(Constants.ALERT_NOTIFICATION)
            }

            switchOnOff.setOnCheckedChangeListener { _, b ->
            }
            switchOnOff.setOnClickListener {
                checkPermission(Constants.ALERT_CALL_PHONE)
            }
        }
    }


    private fun checkPermission(type: String) {
        Log.d("=======>#33333333", "checkPermission: ")
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

    override fun onResume() {
        super.onResume()
        if (view != null) {
            initSwStatus()
        }
    }

}