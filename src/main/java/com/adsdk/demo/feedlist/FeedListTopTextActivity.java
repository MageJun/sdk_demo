package com.adsdk.demo.feedlist;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adsdk.demo.R;
import com.analytics.sdk.client.AdExtras;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.NativeAdData;
import com.analytics.sdk.client.NativeAdListener;
import com.analytics.sdk.client.feedlist.FeedListNativeAdListener;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import java.util.ArrayList;
import java.util.List;

public class FeedListTopTextActivity extends Activity{

    private static final String TAG = "FLNADTAG";
    private AQuery mAQuery ;

    private List<NativeAdData> mAds = new ArrayList<>();


    private static final int AD_COUNT = 3;

    private AdRequest adRequest;
    private ViewGroup adContainer;
    private Button btnLoad,btnShow,closeView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedlist_native_ad);
        initView();
        mAQuery =  new AQuery(this);
    }

    public void onBaseClick(View view) {
        switch (view.getId()) {
            case R.id.btn_loadOnly:
                loadAd();
                break;
            case R.id.btn_show:
                showAd();
                break;
            case R.id.btn_close:
                closeView();
                break;
        }
    }

    private void closeView() {
        Toast.makeText(this,"关闭按键点击",Toast.LENGTH_SHORT).show();
        if(adContainer!=null){
            adContainer.removeAllViews();
        }
        closeView.setVisibility(View.GONE);
    }

    private void loadAd(){
        adRequest = new AdRequest.Builder(getApplicationContext())
                .setCodeId(getIntent().getStringExtra("codid"))
                .setAdRequestCount(AD_COUNT)
                .build();

        adRequest.loadFeedListNativeAd(new FeedListNativeAdListener() {
            @Override
            public void onAdLoaded(List<NativeAdData> adViewList) {
                onADLoaded(adViewList);
                Log.i(TAG, " size = " + adViewList.size());
                for (NativeAdData nativeAdData : adViewList) {
                    Log.i(TAG, " nativeAdData hash " + nativeAdData.hashCode());
                }
            }

            @Override
            public void onAdError(com.analytics.sdk.client.AdError adError) {
                Log.i(TAG, "onAdError info = "+adError.toString());
                onNoAD(adError);
            }
        });
    }

    private void showAd(){
        if(mAds==null && mAds.size()==0){
            return;
        }
        Log.i(TAG,"showAd enter");
        View adView = LayoutInflater.from(this).inflate(R.layout.activity_feedlist_native_bobo_ad,null);
        AQuery logoAQ = mAQuery.recycle(adView);
        final NativeAdData ad = mAds.get(0);
        ViewHolder holder = new ViewHolder();
        holder.logo = adView.findViewById(R.id.img_logo);
        holder.poster = adView.findViewById(R.id.img_poster);
        holder.name = adView.findViewById(R.id.text_title);
        holder.desc = adView.findViewById(R.id.text_desc);
        holder.adSource = adView.findViewById(R.id.textView3);
        holder.download = adView.findViewById(R.id.btn_download);
        logoAQ.id(R.id.img_logo).image(TextUtils.isEmpty(ad.getIconUrl()) ? ad.getImageUrl() : ad.getIconUrl(), false, true);
        holder.name.setText(ad.getTitle());
        holder.desc.setText(ad.getDesc());
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(holder.download);
        clickableViews.add(holder.poster);
        clickableViews.add(holder.desc);
        adContainer.removeAllViews();
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(0, 0);
        Log.i(TAG, "ad.getAdPatternType() = " + ad.getAdPatternType()
                +", ecpm = "+ad.getAdExtras().getStringExtra(AdExtras.EXTRA_ECPM,"无")
                +", mid = "+ad.getAdExtras().getStringExtra(AdExtras.EXTRA_MATERIAL_ID,"无")
                +", imgUrl = "+ad.getAdExtras().getStringExtra(AdExtras.EXTRA_IMG_URL,"无"));
        ad.attach(FeedListTopTextActivity.this);
        String adSource = ad.getAdExtras().getStringExtra(AdExtras.EXTRA_AD_SOURCE,"");
        holder.adSource.setText("广告源："+adSource);
        View result = ad.bindView(adView, null, fl, clickableViews,new NativeAdListener() {
            @Override
            public void onADExposed() {
                Log.i(TAG, "onADExposed enter,ad = "+ad.hashCode());
                closeView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onADClicked() {
                Log.i(TAG, "onADClicked enter,ad = "+ad.hashCode());
            }

            @Override
            public void onAdError(com.analytics.sdk.client.AdError adError) {
                Log.i(TAG, "onADError enter , error = " + adError);
            }

        });
        adContainer.addView(result);
        logoAQ.id(R.id.img_poster).image(ad.getImageUrl(), false, true, 0, 0,
                new BitmapAjaxCallback() {
                    @Override
                    protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                        if (iv.getVisibility() == View.VISIBLE) {
                            iv.setImageBitmap(bm);
                        }
                    }
                });

        updateAdAction(holder.download, ad);
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
        Log.i(TAG,"onDestroy");
        if (mAds != null) {
            for (NativeAdData ad : mAds) {
                ad.recycle();
            }
        }
        mAds = null;

        if (adRequest != null) {
            adRequest.recycle();
        }
    }

    private void initView() {
        adContainer = findViewById(R.id.adContainer);
        btnLoad = findViewById(R.id.btn_loadOnly);
        btnShow = findViewById(R.id.btn_show);
        btnShow.setEnabled(false);
        closeView = findViewById(R.id.btn_close);
    }


    public void onADLoaded(List<NativeAdData> ads) {
        if (mAds != null) {
            mAds.addAll(ads);
        }
        Log.i(TAG,"onADLoaded ,mAds.size = "+mAds.size());
        Toast.makeText(this,"广告请求成功",Toast.LENGTH_SHORT).show();
        btnShow.setEnabled(true);
    }

    public void onNoAD(com.analytics.sdk.client.AdError error) {
        Toast.makeText(this,"广告请求失败",Toast.LENGTH_SHORT).show();
    }



    static class ViewHolder {
        public TextView title;
        public RelativeLayout adInfoContainer;
        public TextView name;
        public TextView desc;
        public TextView adSource;
        public ImageView logo;
        public ImageView poster;
        public Button download;
    }


    public static void updateAdAction(Button button, NativeAdData ad) {
        if (!ad.isAppAd()) {
            button.setText("浏览");
        } else {
            button.setText("下载");
        }
        button.setVisibility(View.INVISIBLE);
    }

}
