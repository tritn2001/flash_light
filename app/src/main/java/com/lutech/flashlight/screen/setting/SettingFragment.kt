package com.lutech.flashlight.screen.setting

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lutech.flashlight.R
import com.lutech.flashlight.ads.Utils
import com.lutech.phonetracker.util.settings
import kotlinx.android.synthetic.main.fragment_flash_alert.*
import kotlinx.android.synthetic.main.fragment_setting.view.*


class SettingFragment : Fragment() {

    private var mContext: Context? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mContext = requireContext()

        initView(view)
        handleEvent(view)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun handleEvent(view: View) {
        view.apply {
            swBattery.setOnCheckedChangeListener { _, b -> mContext?.settings?.saveBattery = b }
            swFlash.setOnCheckedChangeListener { _, b ->
                mContext?.settings?.notFlashWhileTheScreenOn = b
            }
            rlPolicy.setOnClickListener {
                Utils.openLink(mContext!!, getString(R.string.link_privacy_policy))
            }
            rlShare.setOnClickListener {
                Utils.shareApp(mContext!!)
            }

        }
    }

    private fun initView(view: View) {
        view.apply {


            swBattery.isChecked = mContext?.settings?.saveBattery!!
            swFlash.isChecked = mContext?.settings?.notFlashWhileTheScreenOn!!
        }
    }


}