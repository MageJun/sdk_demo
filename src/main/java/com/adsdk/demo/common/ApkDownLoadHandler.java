package com.adsdk.demo.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.adsdk.demo.DemoApplication;
import com.analytics.sdk.client.AdDownloadConfirmListener;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdExtras;
import com.analytics.sdk.client.data.AdAppInfoData;

/**
 * 描述：
 */

public class ApkDownLoadHandler {
    private static final String TAG = "ADLHTAG";

    public static AdDownloadConfirmListener makeAdDownloadConfirmListener(){
        return new AdDownloadConfirmListener() {
            @Override
            public void onDownloadConfirm(final Activity activity, final int scenes, final Controller controller) {

                controller.loadAppInfo(new ApkInfoLoadListener() {
                    @Override
                    public void onApkInfo(AdAppInfoData adAppInfoData, AdExtras adExtras) {
                        Log.i(TAG,"onApkInfo enter appName = "+adAppInfoData.getAppName()
                                +", authorName = "+adAppInfoData.getAuthorName()
                                +", versionName = "+adAppInfoData.getVersionName()
                                +", fileSize = "+adAppInfoData.getFileSize()
                                +",time = "+adAppInfoData.getTime()
                                +",icon = "+adAppInfoData.getIconUrl()
                                +", privacyAgreement = "+adAppInfoData.getPrivacyAgreement());
                        if(adAppInfoData.getPermissions()!=null){
                            Log.i(TAG,"permission = \n");
                            for (int i = 0;i<adAppInfoData.getPermissions().size();i++){
                                Log.i(TAG,adAppInfoData.getPermissions().get(i)+"\n");
                            }
                        }
                        handle(activity,scenes,adAppInfoData.getAppName(), controller,adExtras);
                    }

                    @Override
                    public void onApkInfoLoading() {
                        Toast.makeText(DemoApplication.sIntance,"onApkLoading",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onApkInfoLoadFailed(AdError adError) {

                    }
                });
            }
        };
    }
    public static AdDownloadConfirmListener makeAdDownloadConfirmListener(final String tag){
        return new AdDownloadConfirmListener() {
            @Override
            public void onDownloadConfirm(final Activity activity, final int scenes, final Controller controller) {

                controller.loadAppInfo(new ApkInfoLoadListener() {
                    @Override
                    public void onApkInfo(AdAppInfoData adAppInfoData, AdExtras adExtras) {
                        Log.i(TAG,tag+"_onApkInfo enter appName = "+adAppInfoData.getAppName()
                                +", authorName = "+adAppInfoData.getAuthorName()
                                +", versionName = "+adAppInfoData.getVersionName()
                                +", fileSize = "+adAppInfoData.getFileSize()
                                +",time = "+adAppInfoData.getTime()
                                +",icon = "+adAppInfoData.getIconUrl()
                                +", privacyAgreement = "+adAppInfoData.getPrivacyAgreement());
                        if(adAppInfoData.getPermissions()!=null){
                            Log.i(TAG,"permission = \n");
                            for (int i = 0;i<adAppInfoData.getPermissions().size();i++){
                                Log.i(TAG,adAppInfoData.getPermissions().get(i)+"\n");
                            }
                        }
                        handle(activity,scenes,adAppInfoData.getAppName(), controller,adExtras);
                    }

                    @Override
                    public void onApkInfoLoading() {
                        Toast.makeText(DemoApplication.sIntance,"onApkLoading",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onApkInfoLoadFailed(AdError adError) {

                    }
                });
            }
        };
    }


    private static void handle(Activity activity, int scenes, String appName, final AdDownloadConfirmListener.Controller controller, AdExtras adExtras){
        new AlertDialog.Builder(activity)
                .setTitle("应用信息")
                .setMessage("是否下载广告？"+appName)
                .setPositiveButton("立即下载", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        controller.onConfirm();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        controller.onCancel();
                    }
                }).setCancelable(false).show();
    }
}
