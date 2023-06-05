package com.lutech.flashlight.receiver

import android.app.Activity
import com.lutech.flashlight.receiver.ActivityStack
import java.util.ArrayList

class ActivityStack {
    private val activities: MutableList<Activity> = ArrayList()
    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    val topActivity: Activity?
        get() = if (activities.isEmpty()) {
            null
        } else activities[activities.size - 1]

    fun finishTopActivity() {
        if (!activities.isEmpty()) {
            activities.removeAt(activities.size - 1).finish()
        }
    }

    fun finishActivity(activity: Activity?) {
        if (activity != null) {
            activities.remove(activity)
            activity.finish()
        }
    }

    fun finishActivity(activityClass: Class<*>) {
        for (activity in activities) {
            if (activity.javaClass == activityClass) {
                finishActivity(activity)
            }
        }
    }

    fun finishAllActivity() {
        if (!activities.isEmpty()) {
            for (activity in activities) {
                activity.finish()
                activities.remove(activity)
            }
        }
    }

    companion object {
        val instance = ActivityStack()
    }
}