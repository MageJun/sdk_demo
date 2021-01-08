package com.adsdk.demo.banner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.R;
import com.adsdk.demo.common.LogControl;
import com.analytics.sdk.client.AdController;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.banner.BannerAdExtListener;

public class BannerActivity extends Activity {
    static final String TAG = "BannerActivityTAG";
    FrameLayout frameLayout;
    AdRequest adRequest;
    private View loadOnlyView;
    private boolean isLoadOnly = false;
    private Button btnShow;
    private AdController mAdController;
    private BannerAdExtListener bannerAdListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalConfig.RConfig.BANNER_ACTIVITY_LAYOUT_ID);
        frameLayout = findViewById(GlobalConfig.RConfig.BANNER_ACTIVITY_AD_CONTAINER);
        loadOnlyView = findViewById(GlobalConfig.RConfig.SPLASH_ACTIVITY_LAYOUT_LOAD_ONLY_ID);
        btnShow = findViewById(GlobalConfig.RConfig.SPLASH_ACTIVITY_LAYOUT_SHOW_ID);
        btnShow.setEnabled(false);
        LogControl.i(TAG, "frameLayout = " + frameLayout);


    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.btn_loadOnly :
                isLoadOnly = true;
                btnShow.setEnabled(false);
                loadAd();
                break;
            case R.id.btn_show:
                showAds();
                break;
            case R.id.btn_loadAndShow:
                isLoadOnly = false;
                loadAd();
                break;
        }
    }

    private void loadAd(){

        adRequest = new AdRequest.Builder(this)
                .setCodeId(getIntent().getStringExtra("codid"))
                .setAdContainer(frameLayout)
                .setRefresh(30)
                .build();

        bannerAdListener = new BannerAdExtListener() {
            @Override
            public void onAdLoaded(AdController adController) {
                LogControl.i(TAG, "onAdLoaded enter");
                mAdController = adController;
                if (isLoadOnly) {
                    btnShow.setEnabled(true);
                } else {
                    mAdController.show();
                }

            }

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
        };
        adRequest.loadBannerAd(bannerAdListener,isLoadOnly);
    }
    private void showAds(){
        if (mAdController != null){
            boolean result = mAdController.show();
            LogControl.i(TAG, "showAds result = " + result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adRequest.recycle();
    }
}
