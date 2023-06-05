package com.lutech.flashlight.Notification;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationListener extends NotificationListenerService {
    private static final String TAG = "NotificationListener";
    private static final String FB_PACKAGE = "com.facebook.orca";
    private static final String WA_PACKAGE = "com.whatsapp";
    private static final String ZA_PACKAGE = "com.zing.zalo";
    private static final String WE_PACKAGE = "com.whatsapp";
    private static final String VIBER_PACKAGE = "com.whatsapp";
    private static final String TE_PACKAGE = "com.whatsapp";
    private static final String SKY_PACKAGE = "com.whatsapp";
    Context context;


    @Override
    public void onListenerConnected() {
        Log.i(TAG, "Notification Listener connected");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!sbn.getPackageName().equals(FB_PACKAGE) && !sbn.getPackageName().equals(WA_PACKAGE) && !sbn.getPackageName().equals(ZA_PACKAGE))
            return;
        Notification notification = sbn.getNotification();
        Bundle bundle = notification.extras;

        String sender = bundle.getString(NotificationCompat.EXTRA_TITLE);
        String message = bundle.getString(NotificationCompat.EXTRA_TEXT);
        String app;

//            app = getString(R.string.txt_facebook);
    }


}
