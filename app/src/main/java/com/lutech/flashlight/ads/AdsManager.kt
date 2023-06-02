package com.lutech.flashlight.ads

import com.lutech.flashlight.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd

@SuppressLint("StaticFieldLeak")
object AdsManager {

    private var mInterstitialAd: InterstitialAd? = null

    private var mAdsListener: AdsListener? = null

    private var mOnDismissTimerListener: OnDismissTimerListener? = null

    private var mLoadFail = false

    private var mWaitLoading = false

    private var mActivity: Activity? = null

    private var mIsDismissSplashAds: Boolean = false

    var AdsType = 0
    var DistanceTimeShowSameAds = 2
    var DistanceTimeShowOtherAds = 3
    var DistanceTimeShowRewardAds = 4

    var LastTimeShowAds = 0L

    var IsShowBannerAds = true
    var IsShowInterAds = true
    var IsShowOpenAds = true
    var IsShowNativeAds = true
    var IsShowNativeHomeAds = true
    var IsShowNativeAlarmAds = true
    var IsShowNativeHistoryAds = true
    var IsShowNativeLanguageAds = true
    var IsShowRewardAds = true

    fun loadAds(context: Context) {

        mLoadFail = false

        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            context.getString(R.string.phone_tracker_inters_id),
            adRequest,
            object : InterstitialAdLoadCallback() {

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    mLoadFail = true
                    if (mAdsListener != null && mWaitLoading) {
                        mAdsListener?.onAdDismissed()
                    }
                    Log.d("99999999999999", "onAdFailedToLoad: ")

                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    if (mWaitLoading && mActivity != null) {
                        mWaitLoading = false
                        mInterstitialAd?.setOnPaidEventListener {
                            Utils.setTrackRevenueByAdjust(it.valueMicros, it.currencyCode)
                        }
                        mInterstitialAd?.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    mAdsListener?.onAdDismissed()
                                    LastTimeShowAds = System.currentTimeMillis() / 1000
                                    AdsType = Constants.TYPE_ADS_INTER
                                    mInterstitialAd = null
                                    Utils.IsReadyShowOpenAds = true
                                    loadAds(mActivity!!)
                                    mWaitLoading = false
                                    Log.d("99999999999999", "onAdDismissedFullScreenContent: ")
                                }

                                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                    super.onAdFailedToShowFullScreenContent(p0)
                                    mAdsListener?.onAdDismissed()
                                    Utils.IsReadyShowOpenAds = true
                                    loadAds(mActivity!!)
                                    Log.d("99999999999999", "onAdFailedToShowFullScreenContent: ")
                                }

                                override fun onAdShowedFullScreenContent() {
                                    Utils.IsReadyShowOpenAds = false
                                    mInterstitialAd = null
                                    mOnDismissTimerListener?.onDismiss()
                                    Log.d("99999999999999", "onAdShowedFullScreenContent: ")

                                }
                            }
                        mInterstitialAd?.show(mActivity!!)
                    }
                }
            })
    }

    fun showAds(activity: Activity, adsListener: AdsListener) {
        val timeCurrent = System.currentTimeMillis() / 1000
        val timeShow = getDistanceTimeShowAds(true)
        if (timeCurrent - LastTimeShowAds < timeShow) {
            adsListener?.onAdDismissed()
        } else {
            mAdsListener = null
            mAdsListener = adsListener
            if (mLoadFail || !IsShowInterAds) {
                adsListener?.onAdDismissed()

            } else {
                if (mInterstitialAd != null) {
                    adsListener?.onWaitAds()
//                    Handler(Looper.getMainLooper()).postDelayed({
                    mInterstitialAd?.show(activity)
                    mInterstitialAd?.setOnPaidEventListener {
                        Utils.setTrackRevenueByAdjust(it.valueMicros, it.currencyCode)
                    }
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                mAdsListener?.onAdDismissed()
                                Log.d("99999999999999", "1: ")
                                LastTimeShowAds = System.currentTimeMillis() / 1000
                                AdsType = Constants.TYPE_ADS_INTER
                                Utils.IsReadyShowOpenAds = true
                                mInterstitialAd = null
                                loadAds(activity)
//                                    mAdsListener?.onAdDismissed()  //test hide loading ads after show ads

                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                super.onAdFailedToShowFullScreenContent(p0)
                                Log.d("99999999999999", "2: ")

                                mAdsListener?.onAdDismissed()
                                Utils.IsReadyShowOpenAds = true
                                loadAds(activity)
                            }

                            override fun onAdShowedFullScreenContent() {
                                Utils.IsReadyShowOpenAds = false
                                mInterstitialAd = null
                            }
                        }
//                    }, 1000)
                } else {
                    adsListener?.onAdDismissed()
                    Log.d("99999999999999", "3: ")

                }
            }
        }
    }

    fun showSplashAds(
        activity: Activity,
        adsListener: AdsListener,
        onDismissTimerListener: OnDismissTimerListener
    ) {
        mAdsListener = null
        mAdsListener = adsListener
        mOnDismissTimerListener = onDismissTimerListener
        Log.d("222222223333", "onAdFailedToShowFullScreenContent" + mLoadFail)
        if (mLoadFail) {
            mAdsListener?.onAdDismissed()
        } else {
            if (mInterstitialAd != null) {
                mInterstitialAd?.setOnPaidEventListener {
                    Utils.setTrackRevenueByAdjust(it.valueMicros, it.currencyCode)
                }
                mInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {

                        override fun onAdDismissedFullScreenContent() {
                            mAdsListener?.onAdDismissed()
                            LastTimeShowAds = System.currentTimeMillis() / 1000
                            AdsType = Constants.TYPE_ADS_INTER
                            Utils.IsReadyShowOpenAds = true
                            mInterstitialAd = null
                            Log.d("222222223333", "onAdDismissedFullScreenContent")
                            loadAds(activity)
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            super.onAdFailedToShowFullScreenContent(p0)
                            mAdsListener?.onAdDismissed()
                            Utils.IsReadyShowOpenAds = true
                            Log.d("222222223333", "onAdFailedToShowFullScreenContent")
                            loadAds(activity)
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d("222222223333", "onAdShowedFullScreenContent")
                            Utils.IsReadyShowOpenAds = false
                            mOnDismissTimerListener?.onDismiss()
                            mInterstitialAd = null
                        }
                    }
                mInterstitialAd?.show(activity)
            } else {
                if(!mIsDismissSplashAds){
                    mWaitLoading = true
                    mActivity = activity
                }
            }
        }
    }

    fun stopSplashAds(){
        mIsDismissSplashAds = true
    }

    fun showAlwaysAds(activity: Activity, adsListener: AdsListener) {
        mAdsListener = null
        mAdsListener = adsListener

        if (mLoadFail) {
            mAdsListener?.onAdDismissed()
        } else {
            if (mInterstitialAd != null) {
                mInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {

                        override fun onAdDismissedFullScreenContent() {
                            mAdsListener?.onAdDismissed()
                            LastTimeShowAds = System.currentTimeMillis() / 1000
                            AdsType = Constants.TYPE_ADS_INTER
                            Utils.IsReadyShowOpenAds = true
                            mInterstitialAd = null
                            loadAds(activity)
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            super.onAdFailedToShowFullScreenContent(p0)
                            mAdsListener?.onAdDismissed()
                            Utils.IsReadyShowOpenAds = true
                            loadAds(activity)
                        }

                        override fun onAdShowedFullScreenContent() {
                            Utils.IsReadyShowOpenAds = false
                            mInterstitialAd = null
                        }
                    }
                adsListener.onWaitAds()
                Handler().postDelayed({
                    mInterstitialAd?.show(activity)
                }, 1500)
            } else {
                adsListener.onWaitAds()
                mWaitLoading = true
                mActivity = activity
            }
        }
    }

    fun getDistanceTimeShowAds(isInterAds: Boolean): Int {
        return if ((isInterAds && AdsType == Constants.TYPE_ADS_INTER) || (!isInterAds && AdsType == Constants.TYPE_ADS_OPEN)) {
            DistanceTimeShowSameAds
        } else {
            DistanceTimeShowOtherAds
        }
    }

    // reward ads

    private var mAdsRewardListener: AdsRewardListener? = null

    private var mLoadRewardFail = false

    private var mWaitRewardLoading = false

    private var mActivityReward: Activity? = null

    private var mRewardedAd: RewardedAd? = null

    private var mLastTimeShowReward = 0L


//    fun loadRewardAds(context: Context) {
//
//        mLoadRewardFail = false
//
//        mWaitRewardLoading = false
//
//        var adRequest = AdRequest.Builder().build()
//        RewardedAd.load(
//            context,
//            context.getString(R.string.voice_reward_ads_id),
//            adRequest,
//            object : RewardedAdLoadCallback() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    mRewardedAd = null
//                    mLoadRewardFail = true
//                    if (mAdsRewardListener != null && mWaitRewardLoading) {
//                        mAdsRewardListener?.onRewardDismissed()
//                    }
//                    Log.d("======>33333333333", "onAdFailedToLoad: ")
//                }
//
//                override fun onAdLoaded(rewardedAd: RewardedAd) {
//                    Log.d("======>33333333333", "onAdLoaded: ")
//
//                    mRewardedAd = rewardedAd
//                    if (mWaitRewardLoading && mActivityReward != null) {
//                        mRewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
//                            override fun onAdDismissedFullScreenContent() {
//                                super.onAdDismissedFullScreenContent()
//                                mLastTimeShowReward = System.currentTimeMillis()/1000
////                                loadRewardAds(context)
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                super.onAdFailedToShowFullScreenContent(p0)
////                                loadRewardAds(context)
//                            }
//
//                            override fun onAdShowedFullScreenContent() {
//                                super.onAdShowedFullScreenContent()
//                            }
//                        }
//                        Log.d("4444444", "ggggg"+isTimeReadyShowReward().toString() +"  "+ IsShowRewardAds)
//                        if(IsShowRewardAds){
//                            if(isTimeReadyShowReward()) {
//                                mRewardedAd?.show(mActivityReward!!, object:
//                                    OnUserEarnedRewardListener {
//                                    override fun onUserEarnedReward(p0: RewardItem) {
//                                        mAdsRewardListener?.onUserEarnedReward()
//                                    }
//
//                                })
//                            }else if(IsShowRewardAds){
//                                val current = System.currentTimeMillis()/1000
//                                mAdsRewardListener?.onWaitReward()
//                                Handler().postDelayed({
//                                    mRewardedAd?.show(mActivityReward!!, object:
//                                        OnUserEarnedRewardListener {
//                                        override fun onUserEarnedReward(p0: RewardItem) {
//                                            mAdsRewardListener?.onUserEarnedReward()
//                                        }
//
//                                    })
//                                }, (current- mLastTimeShowReward)*1000)
//                            }
//                        }
//
//                    }
//                }
//            })
//    }

    fun isTimeReadyShowReward():Boolean {
        val current = System.currentTimeMillis()/1000
        return (current- mLastTimeShowReward>= DistanceTimeShowRewardAds)
    }

//    fun showAdsReward(activity: Activity, adsRewardListener: AdsRewardListener) {
//        if (mRewardedAd != null) {
//            Log.d("4444444", isTimeReadyShowReward().toString())
//            if(!IsShowRewardAds) return
//            if(isTimeReadyShowReward()) {
//                mRewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
//                    override fun onAdDismissedFullScreenContent() {
//                        super.onAdDismissedFullScreenContent()
//                        adsRewardListener.onRewardDismissed()
//                        mLastTimeShowReward = System.currentTimeMillis()/1000
//                        loadRewardAds(activity)
//                    }
//
//                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                        super.onAdFailedToShowFullScreenContent(p0)
//                        loadRewardAds(activity)
//                    }
//
//                    override fun onAdShowedFullScreenContent() {
//                        super.onAdShowedFullScreenContent()
//                        adsRewardListener.onRewardDismissed()
//                    }
//                }
//                mRewardedAd?.show(activity, object: OnUserEarnedRewardListener {
//                    override fun onUserEarnedReward(p0: RewardItem) {
//                        adsRewardListener.onUserEarnedReward()
//                    }
//                })
//            }else{
//                if(!IsShowRewardAds) return
//                val current = System.currentTimeMillis()/1000
//                adsRewardListener.onWaitReward()
//                Handler().postDelayed({
//                    mRewardedAd?.show(activity, object: OnUserEarnedRewardListener {
//                        override fun onUserEarnedReward(p0: RewardItem) {
//                            adsRewardListener.onUserEarnedReward()
//                        }
//
//                    })
//                }, (current- mLastTimeShowReward)*1000)
//            }
//        }else{
//            if(!mLoadRewardFail) {
//                mAdsRewardListener = adsRewardListener
//                mActivityReward = activity
//                mWaitRewardLoading = true
//                adsRewardListener.onWaitReward()
//                Log.d("4444444", isTimeReadyShowReward().toString())
//
//            }
//        }
//    }
}