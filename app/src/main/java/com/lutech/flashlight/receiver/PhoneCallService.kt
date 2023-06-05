package com.lutech.flashlight.receiver

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService

class PhoneCallService : InCallService() {
    private val callback: Call.Callback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
            when (state) {
                Call.STATE_ACTIVE -> {}
                Call.STATE_DISCONNECTED -> {}
            }
        }
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        call.registerCallback(callback)
        PhoneCallManager.call = call
        var callType: CallType? = null
        if (call.state == Call.STATE_RINGING) {
            callType = CallType.CALL_IN
        } else if (call.state == Call.STATE_CONNECTING) {
            callType = CallType.CALL_OUT
        }
        if (callType != null) {
            val details: Call.Details = call.details
//            CallFullScreenActivity.actionStart(this, phoneNumber, callType)
        }
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        call.unregisterCallback(callback)
        PhoneCallManager.call = null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    enum class CallType {
        CALL_IN, CALL_OUT
    }
}