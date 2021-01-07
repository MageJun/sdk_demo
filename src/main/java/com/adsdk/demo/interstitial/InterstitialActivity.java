package com.adsdk.demo.interstitial;

import android.os.Bundle;

import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.common.BaseActivity;
import com.adsdk.demo.common.LogControl;
import com.adsdk.demo.pkg.sdk.client.AdError;
import com.adsdk.demo.pkg.sdk.client.AdRequest;
import com.adsdk.demo.pkg.sdk.client.VideoSettings;
import com.adsdk.demo.pkg.sdk.client.interstitial.InterstitialAdListener;

public class InterstitialActivity extends BaseActivity {
    
    static String TAG = InterstitialActivity.class.getSimpleName();

    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalConfig.RConfig.INTERSTITIAL_ACTIVITY_LAYOUT_ID);
        setTitle("插屏广告");
        showEditLayout();
        showBaseBtn();

    }

    @Override
    protected void onBaseBtnClick() {
        showProgress();
        VideoSettings videoSettings = new VideoSettings.Builder()
                .setAutoPlayMuted(false)
                .setAutoPlayPolicy(VideoSettings.AutoPlayPolicy.ALWAYS)
                .setVideoPlayPolicy(VideoSettings.PlayPolicy.AUTO)
                .build();

        adRequest = new AdRequest.Builder(this)
                .setCodeId(getIntent().getStringExtra("codid"))
                .setSupportVideo(true)
                .setVideoSettings(videoSettings)
                .build();

        adRequest.loadInterstitialAd(new InterstitialAdListener() {
            @Override
            public void onAdError(AdError adError) {
                LogControl.i(TAG,"onAdError enter ,msg = " + adError.getErrorMessage());
            }

            @Override
            public void onAdClicked() {
                LogControl.i(TAG,"onAdClicked enter");
            }

            @Override
            public void onAdShow() {
                invisibleEditLayout();
                invisibleProgress();
                LogControl.i(TAG,"onAdShow enter");
            }

            @Override
            public void onAdExposure() {
                LogControl.i(TAG,"onAdExposure enter");
//                loadSuccess();
                exposureSuccess();
            }

            @Override
            public void onAdDismissed() {
                LogControl.i(TAG,"onAdDismissed enter");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adRequest != null){
            adRequest.recycle();
        }
    }
}
