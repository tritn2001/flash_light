package com.lutech.phonetracker.util

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.lutech.flashlight.R
import com.lutech.flashlight.setting.Settings
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

fun AppCompatActivity.successToast(msg: String, title: String) {
    MotionToast.createToast(
        this,
        title,
        msg,
        MotionToastStyle.SUCCESS,
        MotionToast.GRAVITY_BOTTOM,
        MotionToast.SHORT_DURATION,
        ResourcesCompat.getFont(this, R.font.roboto_regular)
    )
}

fun AppCompatActivity.errorToast(msg: String, title: String) {
    MotionToast.createToast(
        this,
        title,
        msg,
        MotionToastStyle.ERROR,
        MotionToast.GRAVITY_BOTTOM,
        MotionToast.SHORT_DURATION,
        ResourcesCompat.getFont(this, R.font.roboto_regular)
    )
}

val Context.settings
    get() = Settings.getInstance(applicationContext)

fun AppCompatActivity.infoToast(msg: String, title: String) {
    MotionToast.createToast(
        this,
        title,
        msg,
        MotionToastStyle.INFO,
        MotionToast.GRAVITY_BOTTOM,
        MotionToast.SHORT_DURATION,
        ResourcesCompat.getFont(this, R.font.roboto_regular)
    )
}

fun EditText.isNotEmpty(): Boolean {
    return this.text.toString().trim().isNotEmpty()
}

fun TextView.isNotEmpty(): Boolean {
    return this.text.toString().trim().isNotEmpty()
}

fun TextView.isEmpty(): Boolean {
    return this.text.toString().trim().isEmpty()
}

val TextView.textString: String
    get() = this.text.toString().trim()


