package com.adsdk.demo.interstitial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.R;
import com.adsdk.demo.common.LogControl;
import com.analytics.sdk.client.AdController;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.VideoSettings;
import com.analytics.sdk.client.interstitial.InterstitialAdExtListener;

public class InterstitialActivity extends AppCompatActivity {
    
    static String TAG = InterstitialActivity.class.getSimpleName();

    AdRequest adRequest;
    AdController mAdController;
    private Button btnShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalConfig.RConfig.INTERSTITIAL_ACTIVITY_LAYOUT_ID);
        btnShow = findViewById(R.id.btn_show);
        btnShow.setEnabled(false);


    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.btn_loadOnly :
                loadAd(true);
                btnShow.setEnabled(false);
                break;
            case R.id.btn_show:
                showAds();
                break;
            case R.id.btn_loadAndShow:
                loadAd(false);
                break;
        }
    }

    private void showAds(){
        if(mAdController!=null){
            mAdController.show();
        }
    }

    private void loadAd(boolean isOnlyLoad) {
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

        adRequest.loadInterstitialAd(new InterstitialAdExtListener() {
            @Override
            public void onAdLoaded(AdController adController) {
                LogControl.i(TAG, "onAdLoaded enter");
                mAdController = adController;
                btnShow.setEnabled(true);
            }

            @Override
            public void onAdError(AdError adError) {
                LogControl.i(TAG, "onAdError enter ,msg = " + adError.getErrorMessage());
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
        }, isOnlyLoad);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adRequest != null){
            adRequest.recycle();
        }
    }
}
