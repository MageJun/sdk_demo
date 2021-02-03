package com.adsdk.demo.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adsdk.demo.R;
import com.analytics.sdk.common.view.ProgressWebView;

/**
 * 描述：
 * 时间： 2021/2/3.
 * 创建： WL
 */

public class PopuWindowHelper {

    private static final String testAppInfo = "" +
            "http://m.gdt.qq.com/lp/app_info_right?type=1&app_id=100228415&pkgName=com.smile.gifmaker";
    private static final String testAppInfo2 = "" +
            "http://192.168.11.242:8901/api/app_info_right?type=1";

    public static void showPopuWindow(final View decortView,final Context context){
        LinearLayout parent = new LinearLayout(context);
        final PopupWindow popupWindow = new PopupWindow(context);
        final View popuContentView = LayoutInflater.from(context).inflate(R.layout.jhsdk_dsp_download_window,parent);
        popupWindow.setContentView(popuContentView);
        View closeView = popuContentView.findViewById(R.id.close_view);
        final ProgressWebView webView = popuContentView.findViewById(R.id.webView);

        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        webView.setPageLoadListener(new ProgressWebView.c() {
            @Override
            public void a() {
                Toast.makeText(context, "加载成功", Toast.LENGTH_SHORT).show();
                popupWindow.showAtLocation(decortView, Gravity.BOTTOM, 0, 0);
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                webView.removeAllViews();
                webView.destroy();
            }
        });

        loadUrl(testAppInfo2,webView);

        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    public static void showPopuWindow2(final View decortView, final Context context){
        LinearLayout parent = new LinearLayout(context);
        final PopupWindow popupWindow = new PopupWindow(context);
        LinearLayout contentRoot = new LinearLayout(context);
        int rtPadding = UIHelper.dip2px(context,10);
        contentRoot.setPadding(rtPadding,rtPadding,rtPadding,rtPadding);
        contentRoot.setBackgroundColor(Color.WHITE);
        contentRoot.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        parent.addView(contentRoot,rootParams);

        RelativeLayout titleLayout = new RelativeLayout(context);
        LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                ,UIHelper.dip2px(context,50));
        TextView titleView = new TextView(context);
        titleView.setText("应用信息");
        titleView.setTextSize(18);
        titleView.setTextColor(Color.BLACK);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        titleLayout.addView(titleView,titleParams);
        ImageView closeView = new ImageView(context);
        RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(UIHelper.dip2px(context,30),
                UIHelper.dip2px(context,30));
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        closeView.setImageResource(R.drawable.jhsdk_ad_close2);

        titleLayout.addView(closeView,closeParams);
        contentRoot.addView(titleLayout,titleLayoutParams);

        FrameLayout webParent = new FrameLayout(context);
        LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                ,UIHelper.dip2px(context,300));
        final ProgressWebView webView = new ProgressWebView(context);
        FrameLayout.LayoutParams webParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        webParent.addView(webView,webParams);
        contentRoot.addView(webParent,parentParams);

        Button downLoad = new Button(context);
        downLoad.setTextSize(15);
        downLoad.setText("立即下载");
        downLoad.setTextColor(Color.WHITE);
        downLoad.setTypeface(Typeface.DEFAULT_BOLD);
        downLoad.setBackgroundResource(R.drawable.jhsdk_shap_download_bg);
        int padding = UIHelper.dip2px(context,10);
        downLoad.setPadding(padding,padding,padding,padding);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                ,UIHelper.dip2px(context,50));
        contentRoot.addView(downLoad,btnParams);

        ViewGroup.MarginLayoutParams mp = new ViewGroup.MarginLayoutParams(btnParams);
        mp.setMargins(UIHelper.dip2px(context,15),
                UIHelper.dip2px(context,3),
                UIHelper.dip2px(context,15),0);
        downLoad.setLayoutParams(new LinearLayout.LayoutParams(mp));

        popupWindow.setContentView(parent);

        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        webView.setPageLoadListener(new ProgressWebView.c() {
            @Override
            public void a() {
                Toast.makeText(context, "加载成功", Toast.LENGTH_SHORT).show();
                popupWindow.showAtLocation(decortView, Gravity.BOTTOM, 0, 0);
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                webView.removeAllViews();
                webView.destroy();
            }
        });

        loadUrl(testAppInfo,webView);

        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private static void loadUrl(String clickUrl ,ProgressWebView webView) {

        try {
            if (clickUrl.startsWith("http:") || clickUrl.startsWith("https:")) {
                webView.loadUrl(clickUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
