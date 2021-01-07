package com.adsdk.demo;

/**
 * joker:
 * D1960006 dsp 开屏
 * joker:
 * D1960007 dsp 信息流
 * joker:
 * D1960008 api 开屏
 * joker:
 * D1960009 api 信息流
 * <p>
 * 自有广告 非广点通类型
 * <p>
 * D2110010	大图1200*800 或 1080*1920  暂时无填充
 * <p>
 * D2110009	大图800*1200
 * <p>
 * D2110008	大图1200*627
 * <p>
 * D2110011 三图  暂时无填充
 */
public final class GlobalConfig {

    /**
     * 在这里添加 你们的 广告位 id 可以直接测试
     * 注意记得要 同步修改 gradle#applicationId 改成你们的包名
     * <p>
     * 开发过程中可以用demo中的ID  填充率 100 %
     * 是通用广告位 不验证包名
     */
    public interface ChannelId {
        /**
         * 开屏
         *
         * @see com.adsdk.demo.splash.SplashActivity
         * @see com.adsdk.demo.splash.SplashSkipViewActivity
         */
        String SPLASH = "D2110001,D1960006,D1960008,D2330012";
        /**
         * 自渲染信息流
         *
         * @see com.adsdk.demo.feedlist.FeedListTopTextActivity                 ListView
         * @see com.adsdk.demo.feedlist.FeedListNativeRecylerViewImplActivity   RecylerView
         * @see com.adsdk.demo.feedlist.FeedListVideoDevContainerRenderActivity 信息流视频
         *
         * D2110016 穿山甲
         *
         */
        String FEED_LIST_NATIVE = "D2110010,D1960007,D1960009,D2110016,D2110015,D2110014";
        /**
         * 自渲染信息流(视频)
         *
         * @see com.adsdk.demo.feedlist.FeedListVideoDevContainerRenderActivity 信息流视频
         */
        String FEED_LIST_NATIVE_VIDEO = "D2110002,D2110016";

        /**
         * 全屏视频
         *
         * @see com.adsdk.demo.video.FullScreenVideoActivity
         */
        String FULLSCREEN_VIDEO = "D2110005";
        /**
         * 激励视频
         *
         * @see com.adsdk.demo.video.RewardVideoActivity
         */
        String VIDEO = "D2110004,D2120009,D2510007";
        /*
         * 插屏 （图视频混合，视频）
         */
        String INTERSTITIAL = "D2110003,D2110006";
        /*
         * 模版信息流
         */
        String FEED_LIST = "D2110007,D2110015,D2110014";
        /*
         * 横幅
         */
        String BANNER = "D2110023";

    }

    public interface RConfig {

        /**
         * 首页
         **/
        int MAIN_ACTIVITY_LAYOUT_ID = R.layout.activity_main;
        int MAIN_ACTIVITY_BTN_GRIDVIEW_ID = R.id.GridView;

        /**
         * 开发者首页
         **/
        int MAIN_ACTIVITY_DEV_LAYOUT_ID = R.layout.activity_dev_main;
        int MAIN_ACTIVITY_BTN_ID = R.id.test01;

        /**
         * 开屏
         **/
        int SPLASH_ACTIVITY_LAYOUT_ID = R.layout.activity_splashv2;
        int SPLASH_ACTIVITY_LAYOUT_AD_ID = R.id.splash_layout;
        int SPLASH_ACTIVITY_LAYOUT_LOAD_ONLY_ID = R.id.load_only_view;
        int SPLASH_ACTIVITY_LAYOUT_SHOW_ID = R.id.btn_show;

        /**
         * 激励视频
         **/
        int REWARD_VIDEO_ACTIVITY_LAYOUT_ID = R.layout.activity_reward_video;

        /**
         * 插屏
         **/
        int INTERSTITIAL_ACTIVITY_LAYOUT_ID = R.layout.activity_interstitial;

        /**
         * 横幅
         **/
        int BANNER_ACTIVITY_LAYOUT_ID = R.layout.activity_banner;
        int BANNER_ACTIVITY_AD_CONTAINER = R.id.bannerContainer;

        /**
         * 信息流
         **/
        int FEEDLIST_ACTIVITY_LAYOUT_ID = R.layout.activity_feedlist;
        int FEEDLIST_ACTIVITY_RECYCLER_VIEW_ID = R.id.recycler_view;
        int FEEDLIST_ACTIVITY_RECYCLER_VIEW_TITLE_ID = R.id.title;
        int FEEDLIST_ACTIVITY_RECYCLER_VIEW_AD_CONTAINER_ID = R.id.express_ad_container;
        int FEEDLIST_ACTIVITY_RECYCLER_VIEW_ITEM_AD_ID = R.layout.layout_feedlist_item_express_ad;
        int FEEDLIST_ACTIVITY_RECYCLER_VIEW_ITEM_NORMAL_ID = R.layout.layout_feedlist_item_data;
    }

}
