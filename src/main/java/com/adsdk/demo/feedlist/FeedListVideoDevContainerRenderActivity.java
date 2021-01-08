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
import android.view.Window;
import android.view.WindowManager;
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
 *
 */
public class FeedListVideoDevContainerRenderActivity extends Activity implements AbsListView.OnScrollListener {

    private static final String TAG = "FeedListVideoDev_TAG";
    private CustomAdapter mAdapter;
    private AQuery mAQuery;

    private List<NativeAdData> mAds = new ArrayList<>();

    private H mHandler = new H();

    private static final int AD_COUNT = 10;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_feedlist_native_ad_listview);
        initView();

        VideoSettings videoSettings = new VideoSettings.Builder()

                //设置视频广告在预览页自动播放的网络条件：
                //VideoOption.AutoPlayPolicy.WIFI    表示只在WiFi下自动播放；
                //VideoOption.AutoPlayPolicy.ALWAYS  表示始终自动播放，不区分当前网络；
                //VideoOption.AutoPlayPolicy.NEVER   表示始终都不自动播放，不区分当前网络，但在WiFi时会预下载视频资源；
                //默认为始终自动播放；模板渲染视频、插屏2.0视频、自渲染2.0视频都可使用
                .setAutoPlayPolicy(VideoSettings.AutoPlayPolicy.ALWAYS)//

                //设置视频广告在预览页自动播放时是否静音，默认为true，静音自动播放
                .setAutoPlayMuted(true)

                //设置视频广告在在预览页播放过程中是否显示进度条，默认为true，显示进度条
                .setNeedProgressBar(true)

                //用户在预览页点击clickableViews或视频区域(setEnableUserControl设置为false)时是否跳转到详情页，默认为true，跳转到详情页；
                .setEnableDetailPage(true)

                //设置是否允许用户在预览页点击视频播放器区域控制视频的暂停或播放，默认为false，
                //用户点击时的表现与点击clickableViews一致；
                //如果为true，用户点击时将收到NativeADMediaListener.onVideoClicked回调，
                //而不是NativeADEventListener.onADClicked回调，因为此时并不是广告点击。
                .setEnableUserControl(false)

                //如果选择开发者实现封面，必须要调用onVideoAdExposure，否则没有曝光
                .setContainerRender(VideoSettings.ContainerRender.DEV)

                .build();

        //一定要传 getApplicationContext
        adRequest = new AdRequest.Builder(getApplicationContext())
                .setCodeId(getIntent().getStringExtra("codid"))
                .setAdRequestCount(AD_COUNT)
                .setVideoSettings(videoSettings)
                .build();

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
                ad.recycle();
            }
        }
        mAds = null;

        if (adRequest != null) {
            adRequest.recycle();
        }

    }

    private void initView() {
        mAQuery = new AQuery(this);

        List<NormalItem> list = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            list.add(new NormalItem("No." + i + " Init Data"));
        }
        mAdapter = new CustomAdapter(this, list);

        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(this);
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
                        convertView = View.inflate(mContext, R.layout.activity_feedlist_native_list_left_icon_item_video_dev_render, null);
                        holder.videoCoverContainer = convertView.findViewById(R.id.video_cover_container);
                        holder.adVideoContainer = convertView.findViewById(R.id.ad_video_container);
                        holder.bindViewContainer = convertView.findViewById(R.id.bind_view_container);
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

        private void setContainersVisbility(ViewHolder viewHolder, boolean showADContainer) {
            viewHolder.videoCoverContainer.setVisibility(showADContainer ? View.GONE : View.VISIBLE);
            viewHolder.adVideoContainer.setVisibility(showADContainer ? View.VISIBLE : View.GONE);
        }

        private View initItemView(int position, final View convertView, final ViewHolder holder, int type) {
            if (type == TYPE_DATA) {
                holder.title.setText(((NormalItem) mData.get(position)).getTitle());
            } else if (type == TYPE_AD) {
                final NativeAdData ad = (NativeAdData) mData.get(position);
                final AQuery aQuery = mAQuery.recycle(convertView);

                //
                Log.i(TAG, "ad.isVideoAd() = " + ad.isVideoAd());
                Log.i(TAG, "ad.getAppStatus() = " + ad.getAppStatus());
                Log.i(TAG, "ad.getAdPatternType() = " + ad.getAdPatternType());

                if (!ad.isBindedMediaView()) {
                    setContainersVisbility(holder, false);
                    mAQuery.id(R.id.video_cover).image(ad.getImageUrl(), false, true, 0, 0,
                            new BitmapAjaxCallback() {
                                @Override
                                protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                                    if (iv.getVisibility() == View.VISIBLE) {
                                        iv.setImageBitmap(bm);
                                        Log.i(TAG, "cover exposured");
                                        // 如果视频封面图所在容器是开发者自己提供的，必需调用onVideoADExposured进行封面图曝光，且曝光时的容器必需可见
                                        ad.onVideoAdExposured(iv);
                                    }
                                }
                            });

                    holder.videoCoverContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setContainersVisbility(holder, true);
                            bindView(holder, convertView, aQuery, ad);
                            updateAdAction(holder.download, ad);
                        }
                    });
                } else {
                    bindView(holder, convertView, aQuery, ad);
                }

                return convertView;
            }

            return convertView;
        }

    }

    void bindView(ViewHolder holder, View convertView, AQuery logoAQ, NativeAdData ad) {
        logoAQ.id(R.id.img_logo).image(TextUtils.isEmpty(ad.getIconUrl()) ? ad.getImageUrl() : ad.getIconUrl(), false, true);
        holder.name.setText(ad.getTitle());
        holder.desc.setText(ad.getDesc());
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(holder.download);
        clickableViews.add(holder.poster);
        clickableViews.add(holder.desc);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(0, 0);

        ViewGroup bindViewContainer = holder.bindViewContainer;

        // 重要 ！！！ bindView之前一定要绑定视图到Activity
        // 重要 ！！！ bindView之前一定要绑定视图到Activity
        // 重要 ！！！ bindView之前一定要绑定视图到Activity
        ad.attach(FeedListVideoDevContainerRenderActivity.this);

        ad.bindView(bindViewContainer, null, fl, clickableViews, new NativeAdListener() {

            // 这里不推荐使用 NativeAdListenerExt 如在列表中数据和View可能会出现绑定错位

//            @Override
//            public void onADStatusChanged(int appStatus) {
//                Log.i(TAG, "onADStatusChanged , appStatus = " + appStatus);
//            }

//            @Override
//            public void onLoadApkProgress(int Progress) {
//                Log.i(TAG, "onLoadApkProgress , Progress = " + Progress);
//            }

            @Override
            public void onADExposed() {
                Log.i(TAG, "bindView onADExposed enter");
            }

            @Override
            public void onADClicked() {
                Log.i(TAG, "bindView onADClicked enter");
            }

            @Override
            public void onAdError(AdError adError) {
                Log.i(TAG, "bindView onADError enter , error = " + adError);
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

        if (ad.isVideoAd()) {
            logoAQ.id(R.id.img_poster).visibility(View.INVISIBLE);

            ad.bindMediaView(holder.mediaAdView, new NativeAdMediaListener() {
                @Override
                public void onVideoInit() {
                    Log.i(TAG, "bindMediaView onVideoInit");
                }

                @Override
                public void onVideoLoading() {
                    Log.i(TAG, "bindMediaView onVideoLoading");
                }

                @Override
                public void onVideoReady() {
                    Log.i(TAG, "bindMediaView onVideoReady");
                }

                @Override
                public void onVideoLoaded(int videoDuration) {
                    Log.i(TAG, "bindMediaView onVideoInit");
                }

                @Override
                public void onVideoStart() {
                    Log.i(TAG, "bindMediaView onVideoStart");
                }

                @Override
                public void onVideoPause() {
                    Log.i(TAG, "bindMediaView onVideoPause");
                }

                @Override
                public void onVideoResume() {
                    Log.i(TAG, "bindMediaView onVideoResume");
                }

                @Override
                public void onVideoCompleted() {
                    Log.i(TAG, "bindMediaView onVideoCompleted");
                }

                @Override
                public void onVideoError(AdError var1) {
                    Log.i(TAG, "bindMediaView onVideoError");
                }

                @Override
                public void onVideoStop() {
                    Log.i(TAG, "bindMediaView onVideoStop");
                }

                @Override
                public void onVideoClicked() {
                    Log.i(TAG, "bindMediaView onVideoClicked");
                }
            });

        } else {
            logoAQ.id(R.id.img_poster).visibility(View.VISIBLE);
        }
    }

    static class ViewHolder {
        public ViewGroup videoCoverContainer;
        public ViewGroup adVideoContainer;
        public ViewGroup bindViewContainer;
        public TextView title;
        public TextView name;
        public TextView desc;
        public ImageView logo;
        public ImageView poster;
        public MediaAdView mediaAdView;
        public Button download;
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

    public void updateAdAction(Button button, NativeAdData ad) {

        if (!ad.isAppAd()) {
            button.setText("浏览");
        } else {
            button.setText("下载");
        }

    }

}
