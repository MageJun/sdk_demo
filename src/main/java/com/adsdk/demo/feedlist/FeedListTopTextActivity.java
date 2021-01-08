package com.adsdk.demo.feedlist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.adsdk.demo.R;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.NativeAdData;
import com.analytics.sdk.client.NativeAdListener;
import com.analytics.sdk.client.VideoSettings;
import com.analytics.sdk.client.feedlist.FeedListNativeAdListener;
import com.analytics.sdk.client.media.MediaAdView;
import com.analytics.sdk.client.media.NativeAdMediaListener;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * demo中 我们所有的请求已经做过线程优化处理 请勿在子线程中请求 ！！！
 * <p>
 * 视频信息流请参考
 *
 * @see FeedListVideoDevContainerRenderActivity
 */
public class FeedListTopTextActivity extends Activity implements AbsListView.OnScrollListener {

    private static final String TAG = FeedListTopTextActivity.class.getSimpleName();
    private CustomAdapter mAdapter;
    private AQuery mAQuery;

    private List<NativeAdData> mAds = new ArrayList<>();

    private H mHandler = new H();

    private static final int AD_COUNT = 3;
    private static final int ITEM_COUNT = 30;
    private static final int FIRST_AD_POSITION = 5;
    private static final int AD_DISTANCE = 10;
    private static final int MSG_REFRESH_LIST = 1;

    private int mLastVisibleItem = 0;
    private int mTotalItemCount = 0;
    private boolean mIsLoading = true;
    private AdRequest adRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedlist_native_ad_listview);
        initView();
        requestData();
        requestData();
        requestData();
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
                .setCodeId(getIntent().getStringExtra("codid"))
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
            public void onAdLoaded(List<NativeAdData> adViewList) {
                onADLoaded(adViewList);
            }

            @Override
            public void onAdError(com.analytics.sdk.client.AdError adError) {
                onNoAD(adError);
            }
        });

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
        if (mAds != null) {
            for (NativeAdData ad : mAds) {
                // recycle 不是必须的 内部弱引用实现
                // 如果确保广告不会再次展示和点击才可以手动调用 recycle
                // 否则会无法点击或者点击异常
                ad.recycle();
            }
        }
        mAds = null;
    }

    private void initView() {
        ListView listView = findViewById(R.id.listview);
        List<NormalItem> list = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            list.add(new NormalItem("No." + i + " Init Data"));
        }
        mAdapter = new CustomAdapter(this, list);
        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(this);
        mAQuery = new AQuery(this);
    }


    public void onADLoaded(List<NativeAdData> ads) {
        mIsLoading = false;
        if (mAds != null) {
            mAds.addAll(ads);
            Message msg = mHandler.obtainMessage(MSG_REFRESH_LIST, ads);
            mHandler.sendMessage(msg);
        }
    }

    public void onNoAD(com.analytics.sdk.client.AdError error) {
        mIsLoading = false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (!mIsLoading && scrollState == SCROLL_STATE_IDLE && mLastVisibleItem == mTotalItemCount) {
//            mAdManager.loadData(AD_COUNT);
            mIsLoading = true;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mLastVisibleItem = firstVisibleItem + visibleItemCount;
        mTotalItemCount = totalItemCount;
    }

    private class CustomAdapter extends BaseAdapter {

        private List<Object> mData;
        private Context mContext;
        private TreeSet mADSet = new TreeSet();

        private static final int TYPE_DATA = 0;
        private static final int TYPE_AD = 1;

        public CustomAdapter(Context context, List list) {
            mContext = context;
            mData = list;
        }

        public void addNormalItem(NormalItem item) {
            mData.add(item);
        }

        public void addAdToPosition(NativeAdData nativeAdData, int position) {
            if (position >= 0 && position < mData.size()) {
                mData.add(position, nativeAdData);
                mADSet.add(position);
            }
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return mADSet.contains(position) ? TYPE_AD : TYPE_DATA;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int type = getItemViewType(position);
            if (convertView == null || convertView.getTag() == null) {
                holder = new ViewHolder();
                switch (type) {
                    case TYPE_DATA:
                        convertView = View.inflate(mContext, R.layout.activity_feedlist_native_normal_list_item, null);
                        holder.title = convertView.findViewById(R.id.title);
                        break;
                    case TYPE_AD:
                        convertView = View.inflate(mContext, R.layout.activity_feedlist_native_list_left_icon_item_video_ad, null);
                        holder.logo = convertView.findViewById(R.id.img_logo);
                        holder.poster = convertView.findViewById(R.id.img_poster);
                        holder.name = convertView.findViewById(R.id.text_title);
                        holder.desc = convertView.findViewById(R.id.text_desc);
                        holder.download = convertView.findViewById(R.id.btn_download);
                        holder.mediaAdView = convertView.findViewById(R.id.sdk_adview);
                        break;
                }
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            convertView = initItemView(position, convertView, holder, type);

            convertView.setTag(holder);

            return convertView;
        }

        private View initItemView(int position, View convertView, final ViewHolder holder, int type) {
            if (type == TYPE_DATA) {
                holder.title.setText(((NormalItem) mData.get(position)).getTitle());
                convertView.setTag(holder);
            } else if (type == TYPE_AD) {
                final NativeAdData ad = (NativeAdData) mData.get(position);
                AQuery logoAQ = mAQuery.recycle(convertView);
                logoAQ.id(R.id.img_logo).image(TextUtils.isEmpty(ad.getIconUrl()) ? ad.getImageUrl() : ad.getIconUrl(), false, true);
                holder.name.setText(ad.getTitle());
                holder.desc.setText(ad.getDesc());

                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(holder.download);
                clickableViews.add(holder.poster);
                clickableViews.add(holder.desc);

                //  NATIVE_2IMAGE_2TEXT = 1;
                //  NATIVE_VIDEO = 2;
                //  NATIVE_3IMAGE = 3;
                //  NATIVE_1IMAGE_2TEXT = 4;

                Log.i(TAG, "ad.getAdPatternType() = " + ad.getAdPatternType());

                //传(0, 0)隐藏广点通LOGO
                FrameLayout.LayoutParams adlogoLayoutParams = new FrameLayout.LayoutParams(0, 0);

                // 重要 ！！！ 绑定视图到Activity
                // 重要 ！！！ 绑定视图到Activity
                // 重要 ！！！ 绑定视图到Activity
                ad.attach(FeedListTopTextActivity.this);

                View result = ad.bindView(convertView, null, adlogoLayoutParams, clickableViews, new NativeAdListener() {
                    @Override
                    public void onADExposed() {
                        Log.i(TAG, "onADExposed enter");
                    }

                    @Override
                    public void onADClicked() {
                        Log.i(TAG, "onADClicked enter");
                    }

                    @Override
                    public void onAdError(com.analytics.sdk.client.AdError adError) {
                        Log.i(TAG, "onADError enter , error = " + adError);
                    }

                });

                logoAQ.id(R.id.img_poster).image(ad.getImageUrl(), false, true, 0, 0,
                        new BitmapAjaxCallback() {
                            @Override
                            protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                                if (iv.getVisibility() == View.VISIBLE) {
                                    iv.setImageBitmap(bm);
                                }
                            }
                        });
                if(ad.isVideoAd()){
                    logoAQ.id(R.id.img_poster).visibility(View.INVISIBLE);

                    ad.bindMediaView(holder.mediaAdView, new NativeAdMediaListener() {
                        @Override
                        public void onVideoInit() {
                            Log.i(TAG,"bindMediaView onVideoInit");
                        }

                        @Override
                        public void onVideoLoading() {
                            Log.i(TAG,"bindMediaView onVideoLoading");
                        }

                        @Override
                        public void onVideoReady() {
                            Log.i(TAG,"bindMediaView onVideoReady");
                        }

                        @Override
                        public void onVideoLoaded(int videoDuration) {
                            Log.i(TAG,"bindMediaView onVideoInit");
                        }

                        @Override
                        public void onVideoStart() {
                            Log.i(TAG,"bindMediaView onVideoStart");
                        }

                        @Override
                        public void onVideoPause() {
                            Log.i(TAG,"bindMediaView onVideoPause");
                        }

                        @Override
                        public void onVideoResume() {
                            Log.i(TAG,"bindMediaView onVideoResume");
                        }

                        @Override
                        public void onVideoCompleted() {
                            Log.i(TAG,"bindMediaView onVideoCompleted");
                        }

                        @Override
                        public void onVideoError(AdError var1) {
                            Log.i(TAG,"bindMediaView onVideoError");
                        }

                        @Override
                        public void onVideoStop() {
                            Log.i(TAG,"bindMediaView onVideoStop");
                        }

                        @Override
                        public void onVideoClicked() {
                            Log.i(TAG,"bindMediaView onVideoClicked");
                        }
                    });

                } else {
                    logoAQ.id(R.id.img_poster).visibility(View.VISIBLE);
                }
                updateAdAction(holder.download, ad);

                result.setTag(holder);

                return result;
            }

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    static class ViewHolder {
        public TextView title;
        public TextView name;
        public TextView desc;
        public ImageView logo;
        public ImageView poster;
        public Button download;
        public MediaAdView mediaAdView;
    }

    class NormalItem {
        private String mTitle;

        public NormalItem(int index) {
            this("No." + index + " Normal Data");
        }

        public NormalItem(String title) {
            this.mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }

    }

    private class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_LIST:

                    int count = mAdapter.getCount();
                    for (int i = 0; i < ITEM_COUNT; i++) {
                        mAdapter.addNormalItem(new NormalItem(count + i));
                    }

                    List<NativeAdData> ads = (List<NativeAdData>) msg.obj;
                    if (ads != null && ads.size() > 0 && mAdapter != null) {
                        for (int i = 0; i < ads.size(); i++) {
                            mAdapter.addAdToPosition(ads.get(i), count + i * AD_DISTANCE + FIRST_AD_POSITION);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    break;

                default:
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
}
