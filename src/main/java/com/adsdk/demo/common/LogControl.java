package com.adsdk.demo.common;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.adsdk.demo.DemoApplication;

public class LogControl {

    static final String MSG_PREFIX = "[SdkDemo]";

    public static void i(String tag, String msg) {
        Log.i(tag, MSG_PREFIX + " " + msg);
        if(!TextUtils.isEmpty(msg) ){
            String tmp = msg.toUpperCase();
            if(tmp.contains("ONADLOADED")){
                showToast("广告加载成功");
            }else if(tmp.contains("ONADEXPOSURE")){
                showToast("广告曝光成功");
            }else if(tmp.contains("ONADERROR")){
                showToast("广告错误");
            }
        }
    }

    public static void showToast(String msg){
        Toast.makeText(DemoApplication.getAppContext(),msg,Toast.LENGTH_SHORT).show();
    }

}
