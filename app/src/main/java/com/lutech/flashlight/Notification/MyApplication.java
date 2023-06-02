package com.lutech.flashlight.Notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

public class MyApplication extends Application {
    public static final String CHANNEL_ID = "channel_service_example";
//    public  static final String CHANNEL_ID2 = "channel_service_example2";

    @Override
    public void onCreate() {
        super.onCreate();
        createChannelNotification();
    }

    private void createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Service Example", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                Log.d("====>32456", "create noti: ");
            }
        }
    }

//    private void createChannelNotification2() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID2,"Channel Service Example", NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//            if (manager != null){
//                manager.createNotificationChannel(channel);
//                Log.d("====>32456", "create noti: ");
//            }
//        }
//    }


}
