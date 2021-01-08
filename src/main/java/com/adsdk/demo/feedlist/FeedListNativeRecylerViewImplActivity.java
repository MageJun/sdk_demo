package com.adsdk.demo.feedlist;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.R;
import com.adsdk.demo.common.LogControl;
import com.adsdk.demo.common.RecyclerViewMoreUtil;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.NativeAdData;
import com.analytics.sdk.client.NativeAdListener;
import com.analytics.sdk.client.feedlist.FeedListNativeAdListener;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * demo中 我们所有的请求已经做过线程优化处理 请勿在子线程中请求 ！！！
 * <p>
 * 视频信息流请参考
 *
 * @see FeedListVideoDevContainerRenderActivity
 */

public class FeedListNativeRecylerViewImplActivity extends Activity implements RecyclerViewMoreUtil.RefreshDataListener {
    private static final String TAG = FeedListNativeRecylerViewImplActivity.class.getSimpleName();
    public static final int MAX_ITEMS = 20;
    public static final int AD_COUNT = 5;    // 加载广告的条数，取值范围为[1, 10]
    public int FIRST_AD_POSITION = 1; // 第一条广告的位置
    public int ITEMS_PER_AD = 3;     // 每间隔10个条目插入一条广告

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private CustomAdapter mAdapter;
    private List<NormalItem> mNormalDataList = new ArrayList<NormalItem>();
    private List<NormalItem> newsList = new ArrayList<>();
    private List<NativeAdData> mAdViewList;
    private HashMap<NativeAdData, Integer> mAdViewPositionMap = new HashMap<>();
    private RecyclerViewMoreUtil util;
    private AdRequest adRequest;
    private AQuery mAQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(GlobalConfig.RConfig.FEEDLIST_ACTIVITY_LAYOUT_ID);
        util = new RecyclerViewMoreUtil();
        mRecyclerView = findViewById(GlobalConfig.RConfig.FEEDLIST_ACTIVITY_RECYCLER_VIEW_ID);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new CustomAdapter(mNormalDataList);
        mRecyclerView.setAdapter(mAdapter);
        util.init(this, mRecyclerView, mAdapter, this);
        mAQuery = new AQuery(this);
        initData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        FIRST_AD_POSITION = 0;

        // recycle 不是必须的 内部弱引用实现
        // 如果确保广告不会再次展示和点击才可以手动调用 recycle
        // 否则会无法点击或者点击异常

        for (Iterator<Map.Entry<NativeAdData, Integer>> iter = mAdViewPositionMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<NativeAdData, Integer> me = iter.next();
            NativeAdData nativeAdData = me.getKey();
            nativeAdData.recycle();
        }
    }

    private void initData() {
        for (int i = 0; i < MAX_ITEMS; ++i) {
            mNormalDataList.add(new NormalItem("No." + i + " Normal Data"));
            newsList.add(new NormalItem("No." + i + " Normal Data"));
        }
        mAdapter.addItem(newsList);
        newsList.clear();
        requestFeedList();
    }

    /*
      ----------注意事项----------

      1、如果开发者需要缓存自渲染信息流数据，必须对缓存数据进行有效期的控制(缓存时长具体和运营沟通，一般最长支持45分钟)，一旦超过最大缓存时长则不能再次进行曝光；

      2、开发者通过自渲染数据NativeAdData构建完广告View之后，

         <<<<<<<必须要调用bindView接口>>>>>>>，否则会导致无点击事件等异常，

      3、同时开发者需要将 bindView 接口所返回的 View 进行展示，

         <<<<<<<而不是开发者自己构建的广告 View>>>>>>>;

      4、如果使用的是广点通广告，开发者使用 NativeAdData 所构造出的广告 View，这个View及其子View中

         <<<<<<<不能包含 com.qq.e.ads.nativ.widget.NativeAdContainer>>>>>>>，否则会影响点击和计费

      5、自渲染信息流发起请求示例，构建builder直接使用applicationContext

      6、AdRequest 每次请求对应一个新的 new AdRequest 本次请求一旦释放，将不能再次使用，必须重新 new 否则会出现运行时异常.
     */
    private void requestFeedList() {
        AdRequest adRequest = new AdRequest
                .Builder(getApplicationContext())
                .setCodeId(GlobalConfig.ChannelId.FEED_LIST_NATIVE)
                .setAdRequestCount(3)
                .build();
        adRequest.loadFeedListNativeAd(new FeedListNativeAdListener() {
            @Override
            public void onAdLoaded(List<NativeAdData> adViewList) {
                onAdLoadCompleted(adViewList);
            }

            @Override
            public void onAdError(com.analytics.sdk.client.AdError adError) {
                Log.i(TAG, "onAdError enter , adError = " + adError);
            }
        });

    }

    @Override
    public boolean loadMore() {
        initData();
        return true;
    }

    public void onAdError(AdError adError) {
        LogControl.i(TAG, "onAdError enter , adError = " + adError);
    }

    public void onAdLoadCompleted(final List<NativeAdData> adList) {
        LogControl.i(TAG, "onAdLoaded enter , list size = " + adList);
        mAdViewList = adList;
        LogControl.i(TAG, "onAdLoaded enter , size = " + mAdViewList.size());
        for (int i = 0; i < mAdViewList.size(); i++) {
            int position = FIRST_AD_POSITION + ITEMS_PER_AD * i;
            if (position < mNormalDataList.size()) {
                NativeAdData nativeAdData = mAdViewList.get(i);

                mAdViewPositionMap.put(nativeAdData, position); // 把每个广告在列表中位置记录下来
                mAdapter.addADViewToPosition(position, mAdViewList.get(i));
            }
        }
        FIRST_AD_POSITION = mNormalDataList.size();
        mAdapter.notifyDataSetChanged();
    }

    public class NormalItem {
        private String title;

        public NormalItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    /**
     * RecyclerView的Adapter
     */
    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        static final int TYPE_DATA = 0;
        static final int TYPE_AD = 1;
        private List<Object> mData;

        public CustomAdapter(List list) {
            mData = list;
        }

        // 把返回的NativeExpressADView添加到数据集里面去
        public void addADViewToPosition(int position, NativeAdData nativeAdData) {
            if (position >= 0 && position < mData.size() && nativeAdData != null) {
                mData.add(position, nativeAdData);
            }
        }

        // 移除ADView的时候是一条一条移除的
        public void removeADView(int position, NativeAdData adView) {
            mData.remove(position);
            mAdapter.notifyItemRemoved(position); // position为adView在当前列表中的位置
            mAdapter.notifyItemRangeChanged(0, mData.size() - 1);
        }

        @Override
        public int getItemCount() {
            if (mData != null) {
                return mData.size();
            } else {
                return 0;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return mData.get(position) instanceof NativeAdData ? TYPE_AD : TYPE_DATA;
        }

        @Override
        public void onBindViewHolder(final CustomViewHolder customViewHolder, final int position) {
            int type = getItemViewType(position);
            if (TYPE_AD == type) {
                final NativeAdData nativeAdData = (NativeAdData) mData.get(position);
                Log.i(TAG, "bindView enter , position = " + position + " , nativeAdData = " + nativeAdData);

                bindView(customViewHolder, nativeAdData);

            } else {
                customViewHolder.title.setText(((NormalItem) mData.get(position)).getTitle());
            }
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            int layoutId = (viewType == TYPE_AD) ? GlobalConfig.RConfig.FEEDLIST_ACTIVITY_RECYCLER_VIEW_ITEM_AD_ID : GlobalConfig.RConfig.FEEDLIST_ACTIVITY_RECYCLER_VIEW_ITEM_NORMAL_ID;
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, null);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        public void addItem(List<NormalItem> list) {
            mData.addAll(list);
            notifyDataSetChanged();
        }

        private void bindView(CustomViewHolder customViewHolder, NativeAdData ad) {
            Log.i(TAG, "bindView enter");
            int childCount = customViewHolder.container.getChildCount();
            boolean hasChild = (childCount != 0);

            View convertView = null;
            if (!hasChild) {
                Log.i(TAG, "create convertView");
                convertView = View.inflate(FeedListNativeRecylerViewImplActivity.this, R.layout.activity_feedlist_native_list_item_ad, null);
            } else {
                convertView = customViewHolder.container.getChildAt(0);
                Log.i(TAG, "convertView exist , " + convertView);
            }

            TextView title = convertView.findViewById(R.id.text_title2);
            TextView desc = convertView.findViewById(R.id.text_desc2);
            ImageView imgPoster = convertView.findViewById(R.id.img_poster);

            AQuery logoAQ = mAQuery.recycle(convertView);

            title.setText(ad.getTitle());
            desc.setText(ad.getDesc());

            Log.i(TAG, "title = " + title + " , desc = " + desc);
            Log.i(TAG, "ad.title = " + ad.getTitle() + " , ad.Desc = " + ad.getDesc());

            logoAQ.id(R.id.img_poster).image(ad.getImageUrl(), false, true, 0, 0,
                    new BitmapAjaxCallback() {
                        @Override
                        protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                            if (iv.getVisibility() == View.VISIBLE) {
                                Log.i(TAG, "callback image");
                                iv.setImageBitmap(bm);
                            }
                        }
                    });

            //  NATIVE_2IMAGE_2TEXT = 1;
            //  NATIVE_VIDEO = 2;
            //  NATIVE_3IMAGE = 3;
            //  NATIVE_1IMAGE_2TEXT = 4;

            Log.i(TAG, "ad.getAdPatternType() = " + ad.getAdPatternType());


            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(title);
            clickableViews.add(desc);
            clickableViews.add(imgPoster);

            //传(0, 0)隐藏广点通LOGO 不传默认显示在右下角
            FrameLayout.LayoutParams adlogoLayoutParams = new FrameLayout.LayoutParams(0, 0);

            // 重要 ！！！ 绑定视图到Activity
            // 重要 ！！！ 绑定视图到Activity
            // 重要 ！！！ 绑定视图到Activity
            // 内部是弱引用实现 ad.recycle() 不是必须
            ad.attach(FeedListNativeRecylerViewImplActivity.this);

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

            if (hasChild) {
                customViewHolder.container.removeAllViews();
            }

            if (result.getParent() != null) {
                ((ViewGroup) result.getParent()).removeView(result);
            }

            customViewHolder.container.addView(result);

        }

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView title;
            public ViewGroup container;

            public CustomViewHolder(View view) {
                super(view);
                title = view.findViewById(GlobalConfig.RConfig.FEEDLIST_ACTIVITY_RECYCLER_VIEW_TITLE_ID);
                container = view.findViewById(GlobalConfig.RConfig.FEEDLIST_ACTIVITY_RECYCLER_VIEW_AD_CONTAINER_ID);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(),title.getText().toString(),Toast.LENGTH_LONG).show();
            }
        }
    }

}
