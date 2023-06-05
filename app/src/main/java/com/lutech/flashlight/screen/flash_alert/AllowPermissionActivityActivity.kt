package com.lutech.flashlight.screen.flash_alert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.lutech.flashlight.R
import com.lutech.flashlight.util.PermissionManager
import kotlinx.android.synthetic.main.activity_alow_permission_activity.*

class AllowPermissionActivityActivity : AppCompatActivity() {

    private var isGotoSetting: Boolean = false

    private var permissionManager: PermissionManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alow_permission_activity)

        permissionManager = PermissionManager(this)

        btnOkPermission.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            isGotoSetting = true
        }
    }

    override fun onResume() {
        super.onResume()
        if (isGotoSetting) {
            if (permissionManager!!.isNotificationServiceRunning) {
                setResult(RESULT_OK)
            }
            finish()
        }
    }
}