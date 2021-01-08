package com.adsdk.demo.feedlist;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;

import com.adsdk.demo.common.LogControl;
import com.adsdk.demo.R;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.LayoutStyle;
import com.analytics.sdk.client.ViewStyle;
import com.analytics.sdk.client.feedlist.AdSize;
import com.analytics.sdk.client.feedlist.AdView;
import com.analytics.sdk.client.feedlist.FeedListAdListener;

import java.util.List;

public class TestFeedListTemplateActivity extends Activity implements FeedListAdListener {
    private static final String TAG = TestFeedListTemplateActivity.class.getName();
    private AdRequest adRequest;
    private FrameLayout mExpressAdContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_feedlist_item_express_ad);
        initView();
        requestFeedList();
    }

    private void requestFeedList() {
//        AdSize adSize = new AdSize(AdSize.FULL_WIDTH, UIHelper.dip2px(this,58)); // 消息流中用AUTO_HEIGHT
        AdSize adSize = new AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT); // 消息流中用AUTO_HEIGHT
        LogControl.i(TAG, "loadInformationFlow enter");

        ViewStyle titleStyle = ViewStyle.obtain()
                .setTextSize(12)
                .setTextColor(Color.RED);
//                                            .setBgColor(Color.BLACK);

//        ViewStyle descStyle = ViewStyle.obtain(titleStyle);

        LayoutStyle layoutStyle = LayoutStyle.obtain()
                .setHiddenClose(false)
                .setBgColor(Color.TRANSPARENT)
                .addViewStyle(ViewStyle.STYLE_TITLE, titleStyle);
//                                                .addViewStyle(ViewStyle.STYLE_DESC,descStyle);

        adRequest = new AdRequest.Builder(this)
                .setCodeId(getIntent().getStringExtra("codid"))
                .setAdRequestCount(1)
                .setAdSize(adSize)
                .setLayoutStyle(layoutStyle)
                .build();

        adRequest.loadFeedListAd(this);

    }


    @Override
    public void onAdLoaded(List<AdView> adViewList) {
        AdView adView = adViewList.get(0);
        View view = adView.getView();
        mExpressAdContainer.addView(view);
        adView.render();
    }

    @Override
    public void onAdClicked(AdView adView) {
        LogControl.i(TAG, "onAdClicked enter");
    }

    @Override
    public void onAdDismissed(AdView adView) {
        LogControl.i(TAG, "onAdDismissed enter");
    }

    @Override
    public void onADExposed(AdView adView) {
        LogControl.i(TAG, "onADExposed enter");
    }

    @Override
    public void onVideoLoad() {

    }

    @Override
    public void onVideoPause() {

    }

    @Override
    public void onVideoStart() {

    }

    @Override
    public void onAdError(AdError adError) {
        LogControl.i(TAG, "onAdError enter , adError = " + adError);
    }

    private void initView() {
        mExpressAdContainer = (FrameLayout) findViewById(R.id.express_ad_container);
    }
}
