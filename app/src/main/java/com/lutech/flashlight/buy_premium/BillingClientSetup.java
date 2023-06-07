package com.lutech.flashlight.buy_premium;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.PurchasesUpdatedListener;


public class BillingClientSetup {

    //android.test.purchased
    public static final String ITEM_BUY_SUB_WEEK = "voice_sub_week";
    public static final String ITEM_BUY_SUB_YEAR = "voice_sub_year";
    public static final String ITEM_BUY_SUB_MONTH = "voice_sub_month";


    private static final String key = "Upgraded";
    private static final String NAME_APP = "VoiceChanger";
    private  static BillingClient instance;

    public static BillingClient getInstance(Context context, PurchasesUpdatedListener listener){
        return instance == null ? setupBillingClient(context, listener) : instance;
    }

    private static BillingClient setupBillingClient(Context context, PurchasesUpdatedListener listener){
        BillingClient billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener(listener)
                .build();

        return billingClient;
    }

    public static boolean isUpgraded(Context context) {
        //  return true;
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME_APP, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong("Time", 0);
        long timeCurrent = System.currentTimeMillis()/1000;
        return timeCurrent <= time;
    }

    public static void updateTimeUpgrade(Context context, long time) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("Time", time);
        editor.apply();
    }
}
