package com.lutech.flashlight.camera

interface CameraTorchListener {
    fun onTorchEnabled(isEnabled:Boolean)

    fun onTorchUnavailable()
}
