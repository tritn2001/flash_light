package com.lutech.flashlight.screen.flash_alert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lutech.flashlight.R
import com.lutech.flashlight.ads.Constants
import kotlinx.android.synthetic.main.activity_settings_flash_alert.*

class SettingsFlashAlertActivity : AppCompatActivity() {

    private var mType: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_flash_alert)

        initView()

        handleEvent()
    }

    private fun initView() {

        mType = intent.getIntExtra(Constants.TYPE_ALERT, 0)

        var content: String? = null
        var imageStart: Int? = null

        when (mType) {
            Constants.CALL_PHONE -> {
                content = getString(R.string.txt_turn_on_for_incoming_calls)
                imageStart = R.drawable.ic_call_coming
            }
            Constants.SMS -> {
                content = getString(R.string.txt_turn_on_for_sms)
                imageStart = R.drawable.ic_mail
            }
            Constants.NOTIFICATION -> {
                content = getString(R.string.txt_turn_on_for_amp_notification)
                imageStart = R.drawable.ic_notification_2
            }

        }

        tvContentType.text = content
        ivType.setImageResource(imageStart!!)
    }

    private fun handleEvent() {

    }
}