package com.adsdk.demo.feedlist;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adsdk.demo.R;
import com.adsdk.demo.sdk.client.AdError;
import com.adsdk.demo.sdk.client.AdRequest;
import com.adsdk.demo.sdk.client.NativeAdData;
import com.adsdk.demo.sdk.client.NativeAdListener;
import com.adsdk.demo.sdk.client.feedlist.FeedListNativeAdListener;
import com.adsdk.demo.sdk.client.s.SIPLInterface_3;
import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestFeedListLeftIconActivity extends Activity implements View.OnClickListener , SIPLInterface_3 {

    private static final String TAG = TestFeedListLeftIconActivity.class.getSimpleName();
    private AQuery mAQuery;
    private List<NativeAdData> mAds = new ArrayList<>();
    private static final int AD_COUNT = 1;


    private int mLastVisibleItem = 0;
    private int mTotalItemCount = 0;
    private boolean mIsLoading = true;
    private AdRequest adRequest1;
    private AdRequest adRequest2;
    private ImageView mImgLogo;
    private Button mBtnDownload;
    private TextView mTextTitle;
    private TextView mTextDesc;
    private RelativeLayout mAdInfoContainer;
    private ImageView mImgPoster;
    private View inflate1;
    private View inflate2;
    private LinearLayout mIcon;
    private LinearLayout mIcon1;
    private ImageView mImage;
    private ImageView imageView;
    private TextView textView1;
    private TextView textView2;
    private Timer timer;
    AdRequest mAdRequest1 = null;
    AdRequest mAdRequest2 = null;
    List<NativeAdData> adData1;
    List<NativeAdData> adData2;
    /**
     * 翻页
     */
    private Button mPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        mIcon = (LinearLayout) findViewById(R.id.Icon);
        mIcon1 = (LinearLayout) findViewById(R.id.Icon1);

        initView();
        mIcon.addView(inflate1);
        mIcon1.addView(inflate2);
        loadAd();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                adRequest2 = new AdRequest.Builder(TestFeedListLeftIconActivity.this)
                        .appendParameter(AdRequest.Parameters.KEY_ESP,AdRequest.Parameters.VALUE_ESP_1)
                        .setCodeId("D1000117").setAdRequestCount(AD_COUNT).build();
                adRequest2.loadFeedListNativeAd(new FeedListNativeAdListener() {
                    @Override
                    public void onAdLoaded(List<NativeAdData> adViewList) {
                        onADLoaded(adViewList, adRequest2,2);
                    }

                    @Override
                    public void onAdError(AdError adError) {
                        onNoAD(adError);
                    }
                });
            }
        }, 0, 40 * 1000);
    }

    private void loadAd() {
//        adRequest1 = new AdRequest.Builder(this).setCodeId("D1000117")
//                .appendParameter(AdRequest.Parameters.KEY_ESP,AdRequest.Parameters.VALUE_ESP_1).setAdRequestCount(AD_COUNT).build();
//        adRequest1.loadFeedListNativeAd(new FeedListNativeAdListener() {
//            @Override
//            public void onAdLoaded(List<NativeAdData> adViewList) {
//                onADLoaded(adViewList, adRequest1,1);
//            }
//
//            @Override
//            public void onAdError(AdError adError) {
//                onNoAD(adError);
//            }
//        });
    }

    private void initView() {
        inflate1 = LayoutInflater.from(this).inflate(R.layout.activity_feedlist_native_list_left_icon_item_ad, null);
        inflate2 = LayoutInflater.from(this).inflate(R.layout.activity_feedlist_native_banner_ad, null);

        mAQuery = new AQuery(this);
        mImgLogo = (ImageView) inflate1.findViewById(R.id.img_logo);
        mBtnDownload = (Button) inflate1.findViewById(R.id.btn_download);
        mTextTitle = (TextView) inflate1.findViewById(R.id.text_title);
        mTextDesc = (TextView) inflate1.findViewById(R.id.text_desc);
        mAdInfoContainer = (RelativeLayout) inflate1.findViewById(R.id.ad_info_container);
        mImgPoster = (ImageView) inflate1.findViewById(R.id.img_poster);

        imageView = (ImageView) inflate2.findViewById(R.id.left_img);
        textView1 = (TextView) inflate2.findViewById(R.id.text_title2);
        textView2 = (TextView) inflate2.findViewById(R.id.text_desc2);


        mPage = (Button) findViewById(R.id.page);
        mPage.setOnClickListener(this);
    }

    public void onADLoaded(final List<NativeAdData> ads , final AdRequest mAdRequest, int j) {
        if (j == 1) {
            if (ads != null) {
                NativeAdData nativeAdData = ads.get(0);
                mAQuery.id(R.id.img_logo).image(nativeAdData.getIconUrl());
                mAQuery.id(R.id.img_poster).image(nativeAdData.getImageUrl());
                mAQuery.id(R.id.text_title).text(nativeAdData.getTitle());
                mAQuery.id(R.id.text_desc).text(nativeAdData.getDesc());
                FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(0, 0);
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(mBtnDownload);
                clickableViews.add(mImgPoster);
                clickableViews.add(mTextDesc);
                nativeAdData.bindView(inflate1, null, fl, clickableViews, new NativeAdListener() {
                    @Override
                    public void onADExposed() {
                        Log.i(TAG, "onADExposed enter");
                        if (mAdRequest1 != null){
                            mAdRequest1.recycle();
                            for (NativeAdData adData : adData2) {
                                adData.recycle();
                            }
                            adData1.clear();
                            adData1 = ads;
                            mAdRequest1 = mAdRequest;
                        }else {
                            mAdRequest1 = mAdRequest;
                            adData1 = ads;
                        }
                    }

                    @Override
                    public void onADClicked() {
                        Log.i(TAG, "onADClicked enter");
                    }

                    @Override
                    public void onAdError(AdError adError) {
                        Log.i(TAG, "onADError enter , error = " + adError);
                    }

                });
                updateAdAction(mBtnDownload, nativeAdData);
            }

        } else {
            if (ads != null) {
                    NativeAdData nativeAdData = ads.get(0);
                    mAQuery.id(imageView).image(nativeAdData.getImageUrl());
                    mAQuery.id(R.id.text_title2).text(nativeAdData.getTitle());
                    mAQuery.id(R.id.text_desc2).text(nativeAdData.getDesc());
                    final FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(0, 0);
                    final List<View> clickableViews = new ArrayList<>();
                    clickableViews.add(imageView);
                    clickableViews.add(textView1);
                    clickableViews.add(textView2);
                    nativeAdData.bindView(inflate2, null, fl, clickableViews, new NativeAdListener() {
                        @Override
                        public void onADExposed() {
                            Log.i(TAG, "onADExposed enter");
                            if (mAdRequest2 != null){
                                mAdRequest2.recycle();
                                for (NativeAdData adData : adData2) {
                                    adData.recycle();
                                }
                                adData2.clear();
                                adData2 = ads;
                                mAdRequest2 = mAdRequest;
                            }else {
                                mAdRequest2 = mAdRequest;
                                adData2 = ads;
                            }
                        }

                        @Override
                        public void onADClicked() {
                            Log.i(TAG, "onADClicked enter");
                        }

                        @Override
                        public void onAdError(AdError adError) {
                            Log.i(TAG, "onADError enter , error = " + adError);
                        }

                    });
                    updateAdAction(mBtnDownload, nativeAdData);
            }
        }
    }

    public static void updateAdAction(Button button, NativeAdData ad) {

        if (!ad.isAppAd()) {
            button.setText("浏览");
        } else {
            button.setText("下载");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAds != null) {
            for (NativeAdData ad : mAds) {
                ad.resume();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        if (mAds != null) {
            for (NativeAdData ad : mAds) {
                ad.recycle();
            }
        }
        mAds = null;

        if (adRequest1 != null) {
            adRequest1.recycle();
        }
        if (adRequest2 != null) {
            adRequest2.recycle();
        }

    }


    public void onNoAD(AdError error) {
        mIsLoading = false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.page:
                loadAd();
                break;
        }
    }
}
