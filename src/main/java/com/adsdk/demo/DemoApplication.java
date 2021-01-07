package com.adsdk.demo;

import android.support.multidex.MultiDexApplication;

import com.adsdk.demo.pkg.sdk.client.AdClientContext;

/**
 * ----------开始对接前，请务必阅读一下注意事项----------
 * <p>
 * 请在SDK集成后，发版前给到我们回测包测试。至少给我们留一天的时间测试
 * <p>
 * 回测包必须要出我们的广告 或改配置 或写死数据
 * <p>
 * 我们会帮您测试曝光点击数据是否正常 以免出现计费错误
 *
 * Android 开发文档 密码：123321
 * https://www.showdoc.cc/juhesdk?page_id=3607292880354211
 *
 */
public class DemoApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        //demo中 我们所有的请求已经做过线程优化处理 请勿在子线程中请求
        //请求广告不需要单独放到线程中去执行，内部是非阻塞实现;

        //只能在主进程中初始化 不支持多进程
        AdClientContext.init(this,"testoaid");

    }
}
