package com.lutech.flashlight.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lutech.flashlight.R
import com.lutech.flashlight.SplashActivity

const val channelId = "notification_channel"
const val channelName = "com.lutech.bloodpressure"
const val pkg = "com.lutech.bloodpressure"

class MyFirebaseMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("9999999999999", "receiver message notification")
        try {
            if (message.notification != null) {
                generateNotification(message.notification!!.title!!, message.notification!!.body!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews(pkg, R.layout.notification)
        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.icon, R.mipmap.ic_launcher)
        return remoteView
    }

    private fun generateNotification(title: String, message: String) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.notify(0, builder.build())
        }
    }
}