package com.adsdk.demo.video;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.R;
import com.adsdk.demo.common.BaseActivity;
import com.adsdk.demo.common.LogControl;
import com.adsdk.demo.common.ThreadExecutor;
import com.adsdk.demo.pkg.sdk.client.AdController;
import com.adsdk.demo.pkg.sdk.client.AdError;
import com.adsdk.demo.pkg.sdk.client.AdRequest;
import com.adsdk.demo.pkg.sdk.client.video.FullScreenVideoAdListenerExt;

public class FullScreenVideoActivity extends BaseActivity {

    static String TAG = "FSVideo_TAG";

    private AdRequest adRequest;
    private AdController adDataController;
    private Button btnShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalConfig.RConfig.REWARD_VIDEO_ACTIVITY_LAYOUT_ID);
        btnShow = findViewById(R.id.btn_show);
        btnShow.setEnabled(false);
        setTitle("全屏视频广告");
        showEditLayout();
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

    private void loadAd(boolean isOnlyData) {
        showProgress();
        adRequest = new AdRequest.Builder(this)
                .setCodeId(getIntent().getStringExtra("codid"))
                .build();

        adRequest.loadFullScreenVideoAd(new FullScreenVideoAdListenerExt() {

            @Override
            public void onAdLoaded(AdController adController) {
                LogControl.i(TAG, "onAdLoaded enter");
                adDataController = adController;
                btnShow.setEnabled(true);
                invisibleEditLayout();
                invisibleProgress();
                loadSuccess();
            }

            @Override
            public void onAdError(AdError adError) {
                LogControl.i(TAG, "onAdError enter , msg = " + adError.getErrorMessage());
                FullScreenVideoActivity.this.finish();
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
            public void onAdVideoCompleted() {
                LogControl.i(TAG, "onAdVideoCompleted enter");
            }

            @Override
            public void onAdExposure() {
                LogControl.i(TAG, "onAdExposure enter");
                ThreadExecutor.getMainThreadHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exposureSuccess();
                    }
                },5000);

            }

            @Override
            public void onAdDismissed() {
                LogControl.i(TAG, "onAdDismissed enter");
            }
        },isOnlyData);

    }

    private void showAds(){
        if(adDataController!=null){
            adDataController.show();
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
