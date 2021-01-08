package com.adsdk.demo.feedlist;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adsdk.demo.R;
import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.List;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.NativeAdData;
import com.analytics.sdk.client.data.AdDataListener;
import com.analytics.sdk.client.data.BindParameters;
import com.analytics.sdk.client.data.MultiAdData;
import com.analytics.sdk.client.data.MultiAdDataLoadListener;
import com.analytics.sdk.client.feedlist.AdView;

public class FeedListMultiActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "FeedListMultiActivity";
    private AQuery mAQuery;
    private static final int AD_COUNT = 3;
    private AdRequest adRequest;

    List<MultiAdData> mAds = new ArrayList<>();
    private LinearLayout mAdLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_multi_ad);

        mAdLayout = findViewById(R.id.ad_layout);

        loadAd();

    }

    private void loadAd() {
        adRequest = new AdRequest.Builder(this)
                .setCodeId(getIntent().getStringExtra("codid"))
                .setAdRequestCount(AD_COUNT)
                .build();

        adRequest.loadMultiAdData(new MultiAdDataLoadListener() {
            @Override
            public void onAdLoaded(List<MultiAdData> adMultiDataList) {
                mAds.addAll(adMultiDataList);

                MultiAdData adMultiData = adMultiDataList.get(0);

                int dataType = adMultiData.getAdDataType();

                Log.i(TAG, "dataType = " + dataType);

                View view = null;

                if (dataType == MultiAdData.TYPE_NATIVE) {
                    view = handleNativeAdData(adMultiData, (NativeAdData) adMultiData.getAdData());
                } else if (dataType == MultiAdData.TYPE_VIEW) {
                    view = handleAdView(adMultiData, (AdView) adMultiData.getAdData());
                }

                //view 有可能為空
                if (view == null) {
                    Log.e(TAG, "view is null");
                    return;
                }

                mAdLayout.addView(view);
            }

            @Override
            public void onAdError(AdError adError) {
                Log.e(TAG, adError.toString());
            }
        });
    }

    private View handleAdView(MultiAdData adMultiData, AdView adView) {
        View adView1 = adView.getView();

        adMultiData.bindAdData(new BindParameters.Builder(adView1).build(), new AdDataListener() {
            @Override
            public void onADExposed() {
                Log.i(TAG, "onADExposed 2 enter");
            }

            @Override
            public void onADClicked() {
                Log.i(TAG, "onADClicked 2 enter");
            }

            @Override
            public void onAdError(AdError adError) {
                Log.i(TAG, "onAdError 2 enter");
            }
        });

        adView.render();

        return adView1;
    }

    public View handleNativeAdData(MultiAdData adMultiData, final NativeAdData nativeAdData) {

        if (nativeAdData != null) {
            Log.i(TAG, "handleNativeAdData enter, title = " + nativeAdData.getTitle());
            final View inflate2 = LayoutInflater.from(this).inflate(R.layout.activity_feedlist_multi_ad, null);

            mAQuery = new AQuery(inflate2);
            ImageView imageView = inflate2.findViewById(R.id.ad_icon);
            TextView textView1 = inflate2.findViewById(R.id.ad_title);
            TextView textView2 = inflate2.findViewById(R.id.ad_desc);
            ImageView viewById = inflate2.findViewById(R.id.close_but);
            viewById.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FeedListMultiActivity.this, "关闭", Toast.LENGTH_SHORT).show();
                }
            });
//
            mAQuery.id(imageView).image(nativeAdData.getImageUrl());
            mAQuery.id(R.id.ad_title).text(nativeAdData.getTitle());
            mAQuery.id(R.id.ad_desc).text(nativeAdData.getDesc());
            final FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(0, 0);
            final List<View> clickableViews = new ArrayList<>();
            clickableViews.add(imageView);
            clickableViews.add(textView1);
            clickableViews.add(textView2);

            nativeAdData.attach(FeedListMultiActivity.this);

            BindParameters bindParameters = new BindParameters.Builder(inflate2)
                    .setAdlogoLayoutParams(fl)
                    .setClickableViews(clickableViews)
                    .setCloseView(viewById)
                    .build();

            View result = adMultiData.bindAdData(bindParameters, new AdDataListener() {
                @Override
                public void onADExposed() {
                    Log.i(TAG, "onADExposed 1 enter");
                }

                @Override
                public void onADClicked() {
                    Log.i(TAG, "onADClicked 1 enter");
                }

                @Override
                public void onAdError(AdError adError) {
                    Log.i(TAG, "onAdError 1 enter");
                }
            });

            return result;
        }
        return null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mAds != null) {
            for (MultiAdData ad : mAds) {
                ad.resume();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAds != null) {
            for (MultiAdData ad : mAds) {
                ad.recycle();
            }
        }
    }

    public void onNoAD(AdError error) {
        Log.i(TAG, "onNoAD = " + error);
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
