package com.adsdk.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.adsdk.demo.banner.BannerActivity;
import com.adsdk.demo.common.GridViewConfig;
import com.adsdk.demo.feedlist.FeedListActivity;
import com.adsdk.demo.feedlist.FeedListTopTextActivity;
import com.adsdk.demo.feedlist.FeedListVideoDevContainerRenderActivity;
import com.adsdk.demo.interstitial.InterstitialActivity;
import com.adsdk.demo.pkg.sdk.client.AdRequest;
import com.adsdk.demo.splash.SplashActivity;
import com.adsdk.demo.splash.SplashSkipViewActivity;
import com.adsdk.demo.video.FullScreenVideoActivity;
import com.adsdk.demo.video.RewardVideoActivity;
import com.qq.e.comm.managers.status.SDKStatus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private GridView gridView;
    public static  Activity MAIN_INSTANCE ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MAIN_INSTANCE = this;
        setContentView(GlobalConfig.RConfig.MAIN_ACTIVITY_LAYOUT_ID);

        setInfos();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

        } else {

        }

        initViews();

    }

    private void setInfos() {
        TextView textView = findViewById(R.id.info);
        String packageName = getPackageName();
        String versionName = null;
        String gdtSdkVersion = SDKStatus.getIntegrationSDKVersion();
        try {
            versionName = getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        textView.setText(
                "聚合广告SDK \n" +
                        "注：version:  " + AdRequest.getSdkVersion() + "\n" +
                        "意：PackageName : " + packageName + "\n" +/*+ "\n" +
                        "对：GDTSdkVersion:" + gdtSdkVersion*/
                        "核：VersionName : " + versionName );
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0) {
            if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
                initViews();
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initViews() {
        gridView = this.findViewById(GlobalConfig.RConfig.MAIN_ACTIVITY_BTN_GRIDVIEW_ID);
        List<GridViewConfig> list = new ArrayList<>();
        list.add(new GridViewConfig("初始化SDK", "", InitActivity.class, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this,InitActivity.class));
            }
        }));
        list.add(new GridViewConfig("开屏演示", GlobalConfig.ChannelId.SPLASH, SplashActivity.class));
//        list.add(new GridViewConfig("开屏Window演示", GlobalConfig.ChannelId.SPLASH, SplashActivity.class, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new AlertDialog.Builder(MainActivity.this)
//                        .setTitle("悬浮窗")
//                        .setMessage("需要打开悬浮窗权限!")
//                        .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
//                            @TargetApi(Build.VERSION_CODES.M)
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                new SplashWindow().showWindow(MainActivity.this);
//                            }
//                        })
//                        .setNegativeButton("不再提示", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }).setCancelable(false).show();
//
//            }
//        }));
        list.add(new GridViewConfig("开屏自定义跳过", GlobalConfig.ChannelId.SPLASH, SplashSkipViewActivity.class));
        list.add(new GridViewConfig("信息流模版演示", GlobalConfig.ChannelId.FEED_LIST, FeedListActivity.class));
        list.add(new GridViewConfig("信息流自渲染演示", GlobalConfig.ChannelId.FEED_LIST_NATIVE, FeedListTopTextActivity.class));
        list.add(new GridViewConfig("信息流自渲染视频(开发者定义封面)", GlobalConfig.ChannelId.FEED_LIST_NATIVE_VIDEO, FeedListVideoDevContainerRenderActivity.class));
        list.add(new GridViewConfig("横幅演示", GlobalConfig.ChannelId.BANNER, BannerActivity.class));
        list.add(new GridViewConfig("插屏演示", GlobalConfig.ChannelId.INTERSTITIAL, InterstitialActivity.class));
        list.add(new GridViewConfig("激励视频演示", GlobalConfig.ChannelId.VIDEO, RewardVideoActivity.class));
        list.add(new GridViewConfig("全屏视频演示", GlobalConfig.ChannelId.FULLSCREEN_VIDEO, FullScreenVideoActivity.class));
        GridAdapter gridAdapter = new GridAdapter();
        gridAdapter.addList(list);
        gridView.setAdapter(gridAdapter);
    }

    static class GridAdapter extends BaseAdapter {
        List<GridViewConfig> list = new ArrayList<>();
        private TextView title;
        private TextView codid;

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, final ViewGroup viewGroup) {
            View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_gridview_item, null);
            title = inflate.findViewById(R.id.gridView_item_text);
            codid = inflate.findViewById(R.id.gridView_item_codid);
            title.setText(list.get(i).getText());
            String adId = list.get(i).getCodeid().equals("") ? "无" : list.get(i).getCodeid();
            final String[] adIdArray = adId.split(",");
            codid.setText(adIdArray[0]);
            if (list.get(i).viewClickListener != null) {
                inflate.setOnClickListener(list.get(i).viewClickListener);
            } else {
                inflate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (adIdArray.length > 1) {
                            LinearLayout inflate1 = (LinearLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_item_popwin, null);
                            final PopupWindow popupWindow = new PopupWindow(viewGroup.getContext());
                            popupWindow.setFocusable(true);
                            popupWindow.setContentView(inflate1);
                            for (final String s1 : adIdArray) {
                                Button button = new Button(viewGroup.getContext());
                                button.setText(s1);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(viewGroup.getContext(), list.get(i).getActivity());
                                        intent.putExtra("codid", s1);
                                        viewGroup.getContext().startActivity(intent);
                                        popupWindow.dismiss();
                                    }
                                });
                                inflate1.addView(button);
                            }
                            popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                            popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                        } else {
                            Intent intent = new Intent(viewGroup.getContext(), list.get(i).getActivity());
                            intent.putExtra("codid", adIdArray[0]);
                            viewGroup.getContext().startActivity(intent);
                        }
                    }
                });
            }

            return inflate;
        }

        public void addList(List<GridViewConfig> list) {
            this.list.addAll(list);
            notifyDataSetChanged();
        }
    }
}
