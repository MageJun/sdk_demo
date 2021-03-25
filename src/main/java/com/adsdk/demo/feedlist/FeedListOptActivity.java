package com.adsdk.demo.feedlist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adsdk.demo.R;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.NativeAdData;
import com.analytics.sdk.client.NativeAdListenerExt;
import com.analytics.sdk.client.VideoSettings;
import com.analytics.sdk.client.feedlist.FeedListNativeAdListener;
import com.analytics.sdk.client.media.MediaAdView;
import com.analytics.sdk.client.media.NativeAdMediaListener;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import java.util.ArrayList;
import java.util.List;

public class FeedListOptActivity extends Activity{

    private static final String TAG = "FEEDOPT";
    private EditText edtCodid;
    private ViewGroup adContainer;
    private AdRequest adRequest;
    private int AD_COUNT = 1;
    private NativeAdData nativeAdData;
    private AQuery mAQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_feednative_opt);
        initView();
        mAQuery = new AQuery(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * 这个方法中需要调用resume方法，主要作用是对落地页返回后，恢复广告的状态，否则会导致落地页返回后广告无法再次点击
         *
         */
        if(nativeAdData!=null){
            nativeAdData.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 页面销毁的时候进行资源释放
         */
        if(adRequest!=null){
            adRequest.recycle();
        }
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.loadSapmle :
                requestData();
                break;
            case R.id.loadList:
                Intent intent = new Intent(getIntent());
                intent.setClass(this,FeedListTopTextActivity.class);
                startActivity(intent);
                break;
            case R.id.show:
                showAdView(nativeAdData);
                break;
        }
    }

    private void initView() {
        edtCodid = findViewById(R.id.posId);
        adContainer = findViewById(R.id.ad_container);
        String codeId = getIntent().getStringExtra("codid");
        edtCodid.setText(codeId);
    }

    private void requestData() {
        VideoSettings videoSettings = new VideoSettings.Builder()
                .setAutoPlayMuted(true)
                .setNeedProgressBar(false)
                .setEnableUserControl(false)
                .setEnableDetailPage(false)
                .setVideoPlayPolicy(VideoSettings.PlayPolicy.AUTO)
                .setContainerRender(VideoSettings.ContainerRender.SDK)
                .setAutoPlayPolicy(VideoSettings.AutoPlayPolicy.ALWAYS)
                .build();

        /**
         * 信息流添加视频信息流支持
         * setVideoSettings(videoSettings)
         * setSupportVideo(true)
         *
         * 视频信息流建议加上下面的配置，设置视频信息流预加载功能，可以有效提高用户体验并提高收益
         * appendParameter(AdRequest.Parameters.KEY_ESP,AdRequest.Parameters.VALUE_ESP_4)
         */
        adRequest = new AdRequest.Builder(getApplicationContext())
                .setCodeId(edtCodid.getText().toString())
                .setVideoSettings(videoSettings)
                .setSupportVideo(true)
                .appendParameter(AdRequest.Parameters.KEY_ESP,AdRequest.Parameters.VALUE_ESP_4)
                .setAdRequestCount(AD_COUNT)
                .build();

        /*
         * ----------注意事项----------
         *
         * 1、如果开发者需要缓存自渲染信息流数据，必须对缓存数据进行有效期的控制(缓存时长具体和运营沟通，一般最长支持45分钟)，一旦超过最大缓存时长则不能再次进行曝光；
         *
         * 2、开发者通过自渲染数据NativeAdData构建完广告View之后，
         *
         *    <<<<<<<必须要调用bindView接口>>>>>>>，否则会导致无点击事件等异常，
         *
         * 3、同时开发者需要将 bindView 接口所返回的 View 进行展示，
         *
         *    <<<<<<<而不是开发者自己构建的广告 View>>>>>>>;
         *
         * 4、如果使用的是广点通广告，开发者使用 NativeAdData 所构造出的广告 View，这个View及其子View中
         *
         *    <<<<<<<不能包含 com.qq.e.ads.nativ.widget.NativeAdContainer>>>>>>>，否则会影响点击和计费
         *
         * 5、自渲染信息流发起请求示例，构建builder直接使用applicationContext
         *
         * 6、AdRequest 每次请求对应一个新的 new AdRequest 本次请求一旦释放，将不能再次使用，必须重新 new 否则会出现运行时异常.
         */
        adRequest.loadFeedListNativeAd(new FeedListNativeAdListener() {
            @Override
            public void onAdLoaded(List<NativeAdData> list) {
                if(list!=null && list.size()>0){
                    nativeAdData = list.get(0);
                    Toast.makeText(FeedListOptActivity.this,"请求成功",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAdError(AdError adError) {
                Toast.makeText(FeedListOptActivity.this,"请求失败，"+adError.getErrorMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showAdView(NativeAdData nativeAdData){
        if(nativeAdData!=null){
            adContainer.removeAllViews();
            View adView = LayoutInflater.from(this).inflate(R.layout.activity_feedlist_native_list_left_icon_item_video_ad, null);
            ImageView logo = adView.findViewById(R.id.img_logo);
            final ImageView poster = adView.findViewById(R.id.img_poster);
            TextView name = adView.findViewById(R.id.text_title);
            TextView desc = adView.findViewById(R.id.text_desc);
            Button download = adView.findViewById(R.id.btn_download);
            MediaAdView mediaAdView = adView.findViewById(R.id.sdk_adview);

            mAQuery.id(logo).image(nativeAdData.getIconUrl());
            name.setText(nativeAdData.getTitle());
            desc.setText(nativeAdData.getDesc());
            if(nativeAdData.isAppAd()){
                download.setText("下载");
            }else{
                download.setText("浏览");
            }

            List<View> clickViews = new ArrayList<>();
            clickViews.add(logo);
            clickViews.add(poster);
            clickViews.add(name);
            clickViews.add(desc);
            clickViews.add(download);

            //传(0, 0)隐藏广告LOGO
            FrameLayout.LayoutParams adlogoLayoutParams = new FrameLayout.LayoutParams(0, 0);
            nativeAdData.attach(this);
            View result = nativeAdData.bindView(adView, null, adlogoLayoutParams, clickViews, new NativeAdListenerExt() {
                @Override
                public void onADStatusChanged(int i) {
                    Log.i(TAG,"onADStatusChanged");
                }

                @Override
                public void onLoadApkProgress(int i) {
                    Log.i(TAG,"onLoadApkProgress");
                }

                @Override
                public void onADExposed() {
                    Log.i(TAG,"onADExposed");
                }

                @Override
                public void onADClicked() {
                    Log.i(TAG,"onADClicked");
                }

                @Override
                public void onAdError(AdError adError) {
                    Log.i(TAG,"onAdError adError = "+adError);
                }
            });

            if(nativeAdData.isVideoAd()){
                poster.setVisibility(View.GONE);
                nativeAdData.bindMediaView(mediaAdView, new NativeAdMediaListener() {
                    @Override
                    public void onVideoInit() {
                        Log.i(TAG,"onVideoInit");
                    }

                    @Override
                    public void onVideoLoading() {
                        Log.i(TAG,"onVideoLoading");
                    }

                    @Override
                    public void onVideoReady() {
                        Log.i(TAG,"onVideoReady");
                    }

                    @Override
                    public void onVideoLoaded(int i) {
                        Log.i(TAG,"onVideoLoaded ");
                    }

                    @Override
                    public void onVideoStart() {
                        Log.i(TAG,"onVideoStart");
                    }

                    @Override
                    public void onVideoPause() {
                        Log.i(TAG,"onVideoPause");
                    }

                    @Override
                    public void onVideoResume() {
                        Log.i(TAG,"onVideoResume");
                    }

                    @Override
                    public void onVideoCompleted() {
                        Log.i(TAG,"onVideoCompleted");
                    }

                    @Override
                    public void onVideoError(AdError adError) {
                        Log.i(TAG,"onVideoError adError = "+adError);
                    }

                    @Override
                    public void onVideoStop() {
                        Log.i(TAG,"onVideoStop");
                    }

                    @Override
                    public void onVideoClicked() {
                        Log.i(TAG,"onVideoClicked");
                    }
                });
            }else{
                poster.setVisibility(View.VISIBLE);
                mAQuery.id(poster).image(nativeAdData.getImageUrl(),false,true,0,0,new BitmapAjaxCallback(){
                    @Override
                    protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                        if(poster.getVisibility() == View.VISIBLE){
                            poster.setImageBitmap(bm);
                        }
                    }
                });
            }
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            adContainer.addView(result,lp);
        }
    }

}
