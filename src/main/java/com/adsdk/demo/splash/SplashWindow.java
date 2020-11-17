package com.adsdk.demo.splash;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.common.LogControl;
import com.adsdk.demo.R;
import com.adsdk.demo.pkg.sdk.client.AdController;
import com.adsdk.demo.pkg.sdk.client.AdError;
import com.adsdk.demo.pkg.sdk.client.AdRequest;
import com.adsdk.demo.pkg.sdk.client.EmptyActivity;
import com.adsdk.demo.pkg.sdk.client.splash.SplashAdExtListener;

import static android.view.WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

public class SplashWindow {

    static final String TAG = SplashWindow.class.getSimpleName();

    AdRequest adRequest;
    private String codeId = "";
    private WindowManager windowManager;

    public void showWindow(Context context) {

        if(TextUtils.isEmpty(codeId)) {
            codeId = GlobalConfig.ChannelId.SPLASH;
        }

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        loadAds(context);

    }

    private void loadAds(Context context){

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view = layoutInflater.inflate(R.layout.activity_splashv_window,null);

        final ViewGroup linearLayout = view.findViewById(R.id.splash_layout);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,getType(),
                FLAG_NOT_FOCUSABLE | FLAG_ALT_FOCUSABLE_IM, PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        windowManager.addView(view,layoutParams);

        adRequest = new AdRequest.Builder(new EmptyActivity(linearLayout.getContext()))
                                .setCodeId(codeId)
                                .setAdContainer(linearLayout)
                                .setTimeoutMs(5*1000)
                                .build();

        adRequest.loadSplashAd(new SplashAdExtListener() {
            @Override
            public void onAdTick(long millisUntilFinished) {
                LogControl.i(TAG,"onAdTick enter , millisUntilFinished = " + millisUntilFinished);
            }

            @Override
            public void onAdSkip() {
                LogControl.i(TAG,"onAdSkip enter , ");
            }

            @Override
            public void onAdLoaded(AdController adController) {
                LogControl.i(TAG,"onAdLoaded enter");
            }

            @Override
            public void onAdError(AdError adError) {
                LogControl.i(TAG,"onAdError enter , " + adError.toString());
                windowManager.removeView(view);
            }

            @Override
            public void onAdClicked() {
                LogControl.i(TAG,"onAdClicked enter");
            }

            @Override
            public void onAdShow() {
                LogControl.i(TAG,"onAdShow enter");
            }

            @Override
            public void onAdExposure() {
                LogControl.i(TAG,"onAdExposure enter , tid = " + Thread.currentThread().getId());
            }

            @Override
            public void onAdDismissed() {
                LogControl.i(TAG,"onAdDismissed enter");
                windowManager.removeView(view);
            }
        });

    }

    public void destroy() {
        Log.i(TAG,"onDestroy enter");
        if(adRequest != null){
            adRequest.recycle();
            adRequest = null;
        }
    }

    public static int getType() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            //7.1.1以上需要动态申请TYPE_APPLICATION_OVERLAY权限
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            //7.1.1 需要动态申请TYPE_SYSTEM_ALERT权限
            return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
    }

}
