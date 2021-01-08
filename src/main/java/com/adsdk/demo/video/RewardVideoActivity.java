package com.adsdk.demo.video;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.R;
import com.adsdk.demo.common.LogControl;
import com.analytics.sdk.client.AdController;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.video.RewardVideoAdListener2;

public class RewardVideoActivity extends AppCompatActivity {

    static String TAG = "Reward_TAG";

    private AdRequest adRequest;
    private AdController adController;
    private Button btnShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalConfig.RConfig.REWARD_VIDEO_ACTIVITY_LAYOUT_ID);
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

    private void loadAd(boolean loadOnly) {

        adRequest = new AdRequest.Builder(this)
                .setCodeId(getIntent().getStringExtra("codid"))
                .setRewardName("金币")
                .setRewardAmount(100)
                .setUserID("user123")
                .setVolumnOn(false)
                .build();

        adRequest.loadRewardVideoAd(new RewardVideoAdListener2() {

            @Override
            public void onAdError(AdError adError) {
                LogControl.i(TAG, "onAdError enter , msg = " + adError.getErrorMessage());
                RewardVideoActivity.this.finish();
            }


            @Override
            public View getSkipView(final Activity activity) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30,100,0,0);
                Button button = new Button(activity);
                button.setLayoutParams(layoutParams);
                button.setText("跳过");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                });
                return button;
            }

            @Override
            public void onAdLoaded(AdController adcontroller) {
                LogControl.i(TAG, "onAdLoaded enter");
                adController = adcontroller;
                btnShow.setEnabled(true);
            }

            @Override
            public void onAdClicked() {
                LogControl.i(TAG, "onAdClicked enter");
//                RewardVideoActivity.this.finish();
            }

            @Override
            public void onAdShow() {
                LogControl.i(TAG, "onAdShow enter");
            }

            @Override
            public void onAdVideoCompleted() {
                LogControl.i(TAG, "onAdVideoCompleted enter");
            }

            @Override
            public void onAdExposure() {
                LogControl.i(TAG, "onAdExposure enter");
            }

            @Override
            public void onReward() {
                LogControl.i(TAG, "onReward enter");
            }

            @Override
            public void onAdDismissed() {
                LogControl.i(TAG, "onAdDismissed enter");
            }
        },loadOnly);

    }

    private void showAds(){
        if(adController!=null){
            adController.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogControl.i(TAG, "onDestroy enter");
        if (adRequest != null) {
            adRequest.recycle();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
