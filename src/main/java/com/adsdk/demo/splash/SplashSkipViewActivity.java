package com.adsdk.demo.splash;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.R;
import com.adsdk.demo.common.DevMainActivity;
import com.adsdk.demo.common.LogControl;
import com.adsdk.demo.sdk.client.AdController;
import com.adsdk.demo.sdk.client.AdError;
import com.adsdk.demo.sdk.client.AdRequest;
import com.adsdk.demo.sdk.client.splash.SplashAdExtListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 我们所有的请求已经做过线程优化处理 请勿在子线程中请求 ！！！
 */
public class SplashSkipViewActivity extends Activity {

    static final String TAG = "Splash_TAG_skipView";
    private boolean canJump = false;

    private AdRequest adRequest;
    private TextView viewById;
    private ViewGroup linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalConfig.RConfig.SPLASH_ACTIVITY_LAYOUT_ID);

        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
        } else {
            // 如果是Android6.0以下的机器，默认在安装时获得了所有权限，可以直接调用SDK
            loadAds();
        }

    }

    View skipView;

    /**
     * ----------非常重要----------
     * 当开发者需要自定义跳过布局时，布局的View不能设置任何点击事件 不可以对其绑定 OnClickListener
     * SDK 会处理 skipContainer 的点击事件
     */
    private void loadAds() {
        linearLayout = findViewById(GlobalConfig.RConfig.SPLASH_ACTIVITY_LAYOUT_AD_ID);
        skipView = getLayoutInflater().inflate(R.layout.splash_skip_view, null);
        skipView.setVisibility(View.INVISIBLE);
        viewById = skipView.findViewById(R.id.skip_textview);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 50;
        params.rightMargin = 200;

        adRequest = new AdRequest.Builder(this)
                .setCodeId(getIntent().getStringExtra("codid"))
                .setAdContainer(linearLayout)

                // 布局的View不能设置任何点击事件 不可以对其绑定 OnClickListener !!!
                // 布局的View不能设置任何点击事件 不可以对其绑定 OnClickListener !!!
                // 布局的View不能设置任何点击事件 不可以对其绑定 OnClickListener !!!
                // 如果传入的skipView是TextView 会自动帮设置   ((TextView) skipView).setText(String.format("跳过 %ds", round));
                .setSkipContainer(skipView, params)
                // 跳过时间默认5秒 需要运营沟通控制台配置
                // 超时时间自己处理
                .build();

        adRequest.loadSplashAd(new SplashAdExtListener() {
            @Override
            public void onAdTick(long millisUntilFinished) {
                skipView.setVisibility(View.VISIBLE);
                viewById.setText("跳过  " + millisUntilFinished / 1000);
                LogControl.i(TAG, "onAdTick   ---   " + millisUntilFinished);
            }

            @Override
            public void onAdSkip() {
                /**
                 * ----------非常重要----------
                 * 这里只是方便开发者 统计数据 不能在这里 finish.Splash Go to MainActivity
                 * 否则会丢失曝光！！
                 */
                LogControl.i(TAG, "onAdSkip");
            }

            /**
             * 常见错误
             *      4004	开屏广告容器不可见 (99%原因是上述 <注意事项> 中提到 超时时间过短 拉取到广告来不及展示已经跳过)
             *      5004、102006	广告无填充
             * @param adError
             */
            @Override
            public void onAdError(AdError adError) {
                LogControl.i(TAG, "onAdError enter , " + adError.toString());
                /*
                    5004、102006	广告无填充
                    这个是填充率低的问题 与SDK本身无关
                        1、是否安装有qq和微信 并打开
                        2、测试机安装上手机卡，切换到4G网络
                        3、模拟器也设置手机卡和真实的imei
                        4、请求太频繁、停一会儿再操作
                        5、新买的测试机而且没有手机卡 大概率获取不到广告的
                 */
            }

            @Override
            public void onAdClicked() {
                /**
                 * ----------非常重要----------
                 *   广告点击
                 *   开发过程中一定要确保 点击广告时候有这个回调 否则会影响收益
                 */
                LogControl.i(TAG, "onAdClicked enter");
            }

            @Override
            public void onAdLoaded(AdController adController) {
                /**
                 * 广告已经加载，准备展示
                 */
                LogControl.i(TAG, "onAdLoaded enter");
            }
            @Override
            public void onAdShow() {
                /**
                 * 广告已经准备好
                 */
                LogControl.i(TAG, "onAdShow enter");
                skipView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdExposure() {
                /**
                 * ----------非常重要----------
                 *   广告曝光
                 *   开发过程中一定要确保有这个回调 否则会影响收益
                 */
                LogControl.i(TAG, "onAdExposure enter , tid = " + Thread.currentThread().getId());
            }

            @Override
            public void onAdDismissed() {
                /**
                 * ----------非常重要----------
                 * 广告的最后一个回调
                 * 跳转到首页前在这里操作
                 *   这个回调是一定触发的 只触发一次 可以在这里 finish.Splash Go to MainActivity
                 */
                LogControl.i(TAG, "onAdDismissed enter");
                next();
            }
        });

    }

    private void next() {
        if (canJump) {
            startActivity(new Intent(SplashSkipViewActivity.this, DevMainActivity.class));
            this.finish();
        } else {
            canJump = true;
        }
    }

    /**
     * ----------非常重要----------
     * <p>
     * Android6.0以上的权限适配简单示例：
     * <p>
     * 如果targetSDKVersion >= 23，那么必须要申请到所需要的权限，再调用广点通SDK，否则广点通SDK不会工作。
     * <p>
     * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
     * 注意：下面的`checkSelfPermission`和`requestPermissions`方法都是在Android6.0的SDK中增加的API，如果您的App还没有适配到Android6.0以上，则不需要调用这些方法，直接调用广点通SDK即可。
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

//        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
//            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }

        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            loadAds();
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
            loadAds();
        } else {
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume enter ");
        if (canJump) {
            next();
        }
        canJump = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause enter");
        canJump = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy enter");
        if (adRequest != null) {
            adRequest.recycle();
            adRequest = null;
        }
    }

    /*
       结束开发之后自测流程
       1、自然跳过      查看关键回调是否正常  onAdExposure
       2、点击跳过      查看关键回调是否正常  onAdExposure
       3、点击广告      查看关键回调是否正常  onAdClicked onAdExposure
     */

}
