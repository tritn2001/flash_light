package com.lutech.flashlight.ads;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.lutech.flashlight.R;
import com.lutech.flashlight.SplashActivity;

/**
 * Prefetches App Open Ads.
 */
public class AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {

    private static final String LOG_TAG = "AppOpenManager";
    private AppOpenAd appOpenAd = null;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private Activity currentActivity;
    public boolean isShowingAd = false;
    private final Application myApplication;
    public boolean LoadFail = false;

    public AppOpenManager(Application myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    public void onActivityPreResumed(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreResumed(activity);
    }

    public void fetchAd() {
        try {
            // Have unused ad, no need to fetch another.
            if (isAdAvailable()) {
                return;
            }
            loadCallback =
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                            super.onAdLoaded(appOpenAd);
                            Log.d("88888888888", "onAdLoaded   ");
                            AppOpenManager.this.appOpenAd = appOpenAd;
                            AppOpenManager.this.appOpenAd.setOnPaidEventListener(new OnPaidEventListener() {
                                @Override
                                public void onPaidEvent(@NonNull AdValue adValue) {
                                    Log.d("5556666", "open:  " + (adValue.getValueMicros() / 1000000f) + "  ");
                                    Utils.INSTANCE.setTrackRevenueByAdjust(adValue.getValueMicros(), adValue.getCurrencyCode());
                                }
                            });
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            Log.d("88888888888", "onAdFailedToLoad");
                            AppOpenManager.this.appOpenAd = null;
                            LoadFail = true;
                            mOpenAdsListener.dismissAds();
                        }
                    };
            AdRequest request = getAdRequest();
            AppOpenAd.load(
                    myApplication, myApplication.getResources().getString(R.string.flash_open_id), request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAdIfAvailable() {
        Log.d("88888888888", " " + isAdAvailable() + "  " + isShowingAd);
        if (!isShowingAd && isAdAvailable()) {

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            Log.d("88888888888", "onAdDismissedFullScreenContent");
                            AppOpenManager.this.appOpenAd = null;
                            AdsManager.INSTANCE.setAdsType(Constants.TYPE_ADS_OPEN);
                            AdsManager.INSTANCE.setLastTimeShowAds(System.currentTimeMillis() / 1000);
                            fetchAd();
                            isShowingAd = false;
                            mOpenAdsListener.dismissAds();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            Log.d("88888888888", "onAdFailedToShowFullScreenContent");
                            mOpenAdsListener.dismissAds();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            Log.d("88888888888", "onAdShowedFullScreenContent");
                            LoadFail = false;
                            isShowingAd = true;
                            mOpenAdsListener.dismissTimer();
                        }
                    };

            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(currentActivity);

        } else {
            if (mOpenAdsListener != null) {
                mOpenAdsListener.dismissAds();
            }
            if (!isShowingAd) {
                fetchAd();
            }
        }
    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        return appOpenAd != null;
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        Log.d("TAG", "onActivityCreated: " + activity);
        currentActivity = activity;

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.d("TAG", "onActivityStarted: " + activity);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
        if (!(activity instanceof SplashActivity)) {
            Utils.INSTANCE.showWelcomeBackScreen(activity);
        }
        Log.d("32222222222222222", "onActivityResumed: " + activity.getLocalClassName());

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.d("32222222222222222", "onActivityResumed: " + activity.getLocalClassName());
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.d("TAG", "onActivityStopped: " + activity);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
        Log.d("TAG", "onActivitySaveInstanceState: " + activity);
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        currentActivity = null;
        Log.d("TAG", "onActivityDestroyed: " + activity);
    }

    @Override
    public void onActivityPreStarted(@NonNull Activity activity) {
    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {
    }

    private OpenAdsListener mOpenAdsListener = null;

    public void setOpenAdsListener(OpenAdsListener openAdsListener) {
        mOpenAdsListener = null;
        mOpenAdsListener = openAdsListener;
    }

    public interface OpenAdsListener {
        void dismissAds();

        void dismissTimer();
    }

}