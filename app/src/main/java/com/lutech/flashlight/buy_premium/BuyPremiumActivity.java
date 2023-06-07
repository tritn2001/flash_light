package com.lutech.flashlight.buy_premium;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Arrays;
import java.util.List;

public class BuyPremiumActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    ImageView ivClosePremium, gift_bg;
    Button btnContinuePremium;
    LinearLayout lnBuyWeekly;
    RelativeLayout rlBuyYearly;
    TextView tvWeeklyPrice, tvYearlyPrice;
    LottieAnimationView loading;

    private BillingClient mBillingClient;
    private ConsumeResponseListener mListener;

    private static final String SUB_WEEK = BillingClientSetup.ITEM_BUY_SUB_WEEK;
    private static final String SUB_YEAR = BillingClientSetup.ITEM_BUY_SUB_YEAR;
    private SkuDetailsParams mSkuDetailsParamsSubApp;

    private final int mIndexSelected = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_buy_premium);
        initView();
        initData();
        handleEvent();


        Glide.with(this).load("https://alloffice.app/translate/premium.gif")
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loading.setVisibility(View.GONE);
                        return false;
                    }
                }).
                into(gift_bg);

    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            long time = System.currentTimeMillis() / 1000;
            if (mIndexSelected == 0) {
                time += (7 * 3600 * 24);
            } else {
                time += (24 * 3600 * 365);
            }
            BillingClientSetup.updateTimeUpgrade(this, time);
            handlePurchase(list);
        }
    }

    private void initData() {
        setupBillingClient();

        mSkuDetailsParamsSubApp = SkuDetailsParams.newBuilder().setSkusList(Arrays.asList(SUB_WEEK, SUB_YEAR))
                .setType(BillingClient.SkuType.SUBS)
                .build();
        Log.d("111111111", "dsdsdsdsdd");
        runOnUiThread(() -> mBillingClient.querySkuDetailsAsync(
                mSkuDetailsParamsSubApp,
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                            && list != null && list.size() > 0) {
                        Log.d("111111111", "dsdsdsdsdd");
                        for (SkuDetails skuDetail : list) {
                            switch (skuDetail.getSku()) {
                                case SUB_WEEK:
                                    tvWeeklyPrice.setText(skuDetail.getPrice());
                                    break;
                                case SUB_YEAR:
                                    tvYearlyPrice.setText(skuDetail.getPrice());
                                    break;
                            }
                        }
                    }
                }));
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate);
//        btnContinuePremium.startAnimation(animation);
    }

    private void initView() {
//        ivClosePremium = findViewById(R.id.ivClosePremium);
//        btnContinuePremium = findViewById(R.id.btnContinuePremium);
//        lnBuyWeekly = findViewById(R.id.lnBuyWeekly);
//        rlBuyYearly = findViewById(R.id.rlBuyYearly);
//        tvWeeklyPrice = findViewById(R.id.tvWeeklyPrice);
//        tvYearlyPrice = findViewById(R.id.tvYearlyPrice);
//        gift_bg = findViewById(R.id.gift_bg);
//        loading = findViewById(R.id.lottieLoading);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
//        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));// set status background white
    }

    private void handleEvent() {
        ivClosePremium.setOnClickListener(view -> {
            finish();
        });
        btnContinuePremium.setOnClickListener(view -> {
            launchBillingSub(mIndexSelected == 0 ? SUB_WEEK : SUB_YEAR);
        });

//        lnBuyWeekly.setOnClickListener(view -> {
//            mIndexSelected = 0;
//            lnBuyWeekly.setBackgroundResource(R.drawable.border_premium_click);
//            rlBuyYearly.setBackgroundResource(R.drawable.border_premium_unclick);
//        });
//        rlBuyYearly.setOnClickListener(view -> {
//            mIndexSelected = 1;
//            rlBuyYearly.setBackgroundResource(R.drawable.border_premium_click);
//            lnBuyWeekly.setBackgroundResource(R.drawable.border_premium_unclick);
//        });

    }

    private void launchBillingSub(String sku) {
        if (mBillingClient.isReady()) {
            mBillingClient.querySkuDetailsAsync(
                    mSkuDetailsParamsSubApp,
                    (billingResult, list) -> {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                            for (SkuDetails skuDetail : list) {
                                if (skuDetail.getSku().equals(sku)) {
                                    handleBilling(skuDetail);
                                }
                            }
                        }
                    });
        }
    }

    private void handleBilling(SkuDetails sku) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(sku)
                .build();
        mBillingClient.launchBillingFlow(this, billingFlowParams).getResponseCode();
    }

    private void setupBillingClient() {
        mListener = (billingResult, s) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                // Consume OK!

            }
        };
        mBillingClient = BillingClientSetup.getInstance(this, this);
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                // none
                Log.d("TAKKK", "onBillingServiceDisconnected");
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                Log.d("TAKKK", "onBillingSetupFinished " + billingResult.getResponseCode());
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getPriceProduct();
                        }
                    });
                    // Connect Success!
                    List<Purchase> purchases =
                            mBillingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList();
                    handleAlreadyPurchase(purchases);

                } else {
                    // Connect Fail
                }
            }

            private void getPriceProduct() {
                SkuDetailsParams mSkuDetailsParamsPriceWeek = SkuDetailsParams.newBuilder().setSkusList(Arrays.asList(SUB_WEEK, SUB_YEAR))
                        .setType(BillingClient.SkuType.SUBS)
                        .build();
                mBillingClient.querySkuDetailsAsync(mSkuDetailsParamsPriceWeek, new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK &&
                                list != null && list.size() > 0
                        ) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (SkuDetails skuDetail : list) {
                                        switch (skuDetail.getSku()) {
                                            case SUB_WEEK:
                                                tvWeeklyPrice.setText(skuDetail.getPrice());
                                                break;
                                            case SUB_YEAR:
                                                tvYearlyPrice.setText(skuDetail.getPrice());
                                                break;
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void handleAlreadyPurchase(List<Purchase> purchases) {
        if (purchases != null) {
            for (Purchase purchase : purchases) {
                if (purchase.getSkus().indexOf(SUB_WEEK) > 0 || purchase.getSkus().indexOf(SUB_YEAR) > 0) {
                    ConsumeParams consumParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    mBillingClient.consumeAsync(consumParams, mListener);
                }
            }
        }
    }

    private void showNotice(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }

    private void handlePurchase(List<Purchase> p1) {
        for (Purchase purchase : p1) {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                        @Override
                        public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                showNotice("Subscribed Successfully");
                            }
                        }
                    });
                }
            }
        }
    }

    private void clearPurchase(List<Purchase> list) {
        Purchase purchase = list.get(0);
        ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
        mBillingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
            }
        });
    }

}