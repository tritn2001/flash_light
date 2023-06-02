package com.lutech.flashlight.util

class Events {
    class StateChanged(val isEnabled: Boolean)

    class CameraUnavailable

    class StopStroboscope

    class StopSOS
}
