package com.lutech.flashlight.data

data class FlashAlert(
    var stroboscopeOn: Long = 1000L,
    var stroboscopeOff: Long = 1000L,
    var stroboscopeProgressOn: Int = 1000,
    var stroboscopeProgressOff: Int = 1000,
    var isStatusChecked: Boolean = false
)
