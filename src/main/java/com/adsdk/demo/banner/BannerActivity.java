package com.adsdk.demo.banner;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.common.LogControl;
import com.adsdk.demo.sdk.client.AdError;
import com.adsdk.demo.sdk.client.AdRequest;
import com.adsdk.demo.sdk.client.banner.BannerAdListener;

public class BannerActivity extends Activity {
    static final String TAG = "BannerActivity";
    FrameLayout frameLayout;
    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalConfig.RConfig.BANNER_ACTIVITY_LAYOUT_ID);
        frameLayout = findViewById(GlobalConfig.RConfig.BANNER_ACTIVITY_AD_CONTAINER);

        adRequest = new AdRequest.Builder(this)
                .setCodeId(getIntent().getStringExtra("codid"))
                .setAdContainer(frameLayout)
                .build();

        adRequest.loadBannerAd(new BannerAdListener() {
            @Override
            public void onAdError(AdError adError) {
                LogControl.i(TAG, "onAdError enter , message = " + adError.getErrorMessage());
            }

            @Override
            public void onAdClicked() {
                LogControl.i(TAG, "onAdClicked enter");
            }

            @Override
            public void onAdShow() {
                LogControl.i(TAG, "onAdShow enter");
            }

            @Override
            public void onAdExposure() {
                LogControl.i(TAG, "onAdExposure enter");
            }

            @Override
            public void onAdDismissed() {
                LogControl.i(TAG, "onAdDismissed enter");
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adRequest != null) {
            adRequest.recycle();
        }
    }
}
