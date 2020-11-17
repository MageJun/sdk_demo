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
import android.widget.Button;
import android.widget.Toast;

import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.R;
import com.adsdk.demo.common.DevMainActivity;
import com.adsdk.demo.common.LogControl;
import com.adsdk.demo.pkg.sdk.client.AdController;
import com.adsdk.demo.pkg.sdk.client.AdError;
import com.adsdk.demo.pkg.sdk.client.AdRequest;
import com.adsdk.demo.pkg.sdk.client.splash.SplashAdExtListener;

import java.util.ArrayList;
import java.util.List;

/**
 *  demo中 我们所有的请求已经做过线程优化处理 请勿在子线程中请求 ！！！
 *
 *  不推荐自定义跳过 如需要自定义跳过参考
 *  👇👇👇👇👇👇👇
 *  @see SplashSkipViewActivity
 */
public class SplashActivity extends Activity {

    static final String TAG = "Splash_TAG";
    private boolean canJump = false;

    AdRequest adRequest;
    private View loadOnlyView;
    private boolean isLoadOnly = false;
    private Button btnShow;
    private AdController adController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalConfig.RConfig.SPLASH_ACTIVITY_LAYOUT_ID);
        loadOnlyView = findViewById(GlobalConfig.RConfig.SPLASH_ACTIVITY_LAYOUT_LOAD_ONLY_ID);
        btnShow = findViewById(GlobalConfig.RConfig.SPLASH_ACTIVITY_LAYOUT_SHOW_ID);
        btnShow.setEnabled(false);
        loadOnlyView.setVisibility(View.VISIBLE);
    }


    public void onClick(View view){
        switch (view.getId()) {
            case R.id.btn_loadOnly :
                isLoadOnly = true;
                btnShow.setEnabled(false);
                featchAd();
                break;
            case R.id.btn_show:
                loadOnlyView.setVisibility(View.GONE);
                showAds();
                break;
            case R.id.btn_loadAndShow:
                loadOnlyView.setVisibility(View.GONE);
                isLoadOnly = false;
                featchAd();
                break;
        }
    }

    private void featchAd(){
        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
        } else {
            // 如果是Android6.0以下的机器，默认在安装时获得了所有权限，可以直接调用SDK
            loadAds();
        }
    }

    /**
     * ----------注意事项----------
     * <p>
     * 聚合SDK请求开屏的时间通常在500ms-1.5S内(和当前网络有关),
     * 请尽量保证聚合SDK的请求时间充足，
     * 所以开发者应用初始化到聚合SDK开始请求这之间的逻辑一定要尽快完成，
     * 这样才能保证SDK请求时间充足，也能提高填充率和收益。
     * <p>
     * 1、开发者通常会定义从应用初始化到开屏广告返回的总超时时间，这个总超时时间太短的话就会影响收益；
     *
     * 2、开屏广告有可能部分素材展示不全，SDK只对素材进行裁剪不进行压缩；
     *
     * 3、开屏广告要达到1S以上才算是进行了曝光；
     *
     * 4、开屏广告的显示区域其高度一定要大于设备高度的75%
     *
     * 5、开屏View不能进行缓存，每次开屏需要都需要发起新的请求去获取;
     */
    private void loadAds() {
        final ViewGroup linearLayout = this.findViewById(GlobalConfig.RConfig.SPLASH_ACTIVITY_LAYOUT_AD_ID);

        if(isLoadOnly){
            //广告加载和展示分离
            //必传 展示广告的activity
            adRequest = new AdRequest.Builder(this)
                    //必传 广告位ID
                    .setCodeId(getIntent().getStringExtra("codid"))
                    //请求时可以不传广告容器，展示时传递即可
//                    .setAdContainer(linearLayout)
                    // 跳过时间默认5秒 如需修改 控制台配置
                    // 超时时间自己处理
                    .build();

            //广告加载展示分离，第二个参数需要传递true
            adRequest.loadSplashAd(splashAdExtListener,true);
        }else{
            //广告加载并展示
            //必传 展示广告的activity
            adRequest = new AdRequest.Builder(this)
                    //必传 广告位ID
                    .setCodeId(getIntent().getStringExtra("codid"))
                    //必传 展示广告的大容器
                    .setAdContainer(linearLayout)
                    // 跳过时间默认5秒 如需修改 控制台配置
                    // 超时时间自己处理
                    .build();
            adRequest.loadSplashAd(splashAdExtListener);
        }

    }


    private void showAds(){
        if (adController != null){
            final ViewGroup linearLayout = this.findViewById(GlobalConfig.RConfig.SPLASH_ACTIVITY_LAYOUT_AD_ID);
            //展示广告，传入广告容器
            boolean result = adController.show(linearLayout);
            LogControl.i(TAG, "showAds result = " + result);
        }
    }

    private SplashAdExtListener splashAdExtListener = new SplashAdExtListener() {

        @Override
        public void onAdTick(long millisUntilFinished) {
            LogControl.i(TAG, "onAdTick   ---   " + millisUntilFinished);
        }

        @Override
        public void onAdSkip() {
            LogControl.i(TAG, "onAdSkip");
        }

        @Override
        public void onAdLoaded(AdController controller) {
            /**
             *
             * 广告数据加载成功，
             *
             * 该方法回调表示广告请求成功，
             *
             * 媒体可以在这个方法中统计广告请求成功记录，
             *
             * 同时媒体自己的请求超时倒计时可以在这个方法中关闭
             *
             */
            LogControl.i(TAG, "onAdLoaded");
            adController = controller;
            btnShow.setEnabled(true);
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
             * ----------非常重要 针对所有广告----------
             *   广告点击之后会触发
             *   开发过程中一定要确保 点击广告时候有这个回调 否则会影响收益
             *
             *   媒体可以在该方法中统计广告点击记录
             *
             */
            LogControl.i(TAG, "onAdClicked enter"
            );
        }

        @Override
        public void onAdShow() {
            /**
             * 广告已经准备好开始展示
             */
            LogControl.i(TAG, "onAdShow enter");
        }

        @Override
        public void onAdExposure() {
            /**
             * ----------非常重要 针对所有广告----------
             *   广告曝光
             *
             *   媒体自己的曝光统计也要在这个地方做
             *
             *   开发过程中一定要确保有这个回调 否则会影响收益
             *   测试时候一定要看这个
             */
            LogControl.i(TAG, "onAdExposure enter , tid = " + Thread.currentThread().getId());
        }

        @Override
        public void onAdDismissed() {
            /**
             * ----------非常重要----------
             *   这个回调是一定触发的  可以在这里 finish.Splash Go to MainActivity
             *
             *   这个回调代码广告流程的结束，在这之前不允许任何释放关闭广告的操作！！！
             */
            LogControl.i(TAG, "onAdDismissed enter");
            next();
        }
    };

    private void next() {
        if (canJump) {
            Intent intent = new Intent(this, DevMainActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            canJump = true;
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

    /**
     * ----------权限申请----------
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

        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

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
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy enter");
        if (adRequest != null) {
            adRequest.recycle();
            adRequest = null;
        }
    }

    /*
     *  结束开发之后自测流程
     *  1、自然跳过      查看关键回调是否正常  onAdExposure
     *  2、点击跳过      查看关键回调是否正常  onAdExposure
     *  3、点击广告      查看关键回调是否正常  onAdClicked onAdExposure
     *
     */

}
