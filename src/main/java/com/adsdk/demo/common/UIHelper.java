package com.adsdk.demo.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.analytics.sdk.client.AdClientContext;

import java.util.concurrent.atomic.AtomicInteger;

public class UIHelper {

    static final String TAG = UIHelper.class.getSimpleName();

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static void setBackground(View view, Drawable drawable){
        ViewCompat.setBackground(view,drawable);
    }

    /**
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public static int dip2px(Context context, double dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * (double)density + 0.5D);
    }

    public static int getDenstiyDpi(Context context){
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    public static boolean isFullScreen(Activity activity){
        if(activity == null){
            return false;
        }
        if ((activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            return true;
        }
        return false;
    }

    public static ActivityState getActivityState() {
        throw new RuntimeException("not support");
    }

    public static String getScreenOrientationString(Context context){
        int orientation = getScreenOrientation(context);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏
            return "L";
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏
            return "P";
        }
        return "U";
    }

    public static boolean isActivityDestoryed(Context activity){
        if(activity!=null && activity instanceof Activity){

            Activity activityInstance = (Activity) activity;

            if(activityInstance.isFinishing()){
                return true;
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return activityInstance.isDestroyed();
            }

        }
        return false;
    }

    public static void showDialog(Activity activity, Dialog dlg) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

                if(activity!=null && dlg!=null && !activity.isDestroyed() && !dlg.isShowing()){
                    dlg.show();
                }
            } else {
                if(dlg!=null && !dlg.isShowing()){
                    dlg.show();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void dismissDialog(Activity activity, Dialog dialog){

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

                if(activity!=null && dialog!=null && !activity.isDestroyed() && dialog.isShowing()){
                    dialog.dismiss();
                }
            } else {
                if(dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }



    public static int getStatusBarHeight(Context context) {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        Log.e("getStatusBarHeight", result + "");
        return result;
    }


    public static class ActivityState {
        public static final int TRUE = 1;
        public static final int FALSE = 0;
        public static final int UNKNOW = 0;

        public long onCreateBeforeTime = UNKNOW;
        public long onCreateAfterTime = UNKNOW;
        public long onResumeBeforeTime = UNKNOW;
        public long onResumeAfterTime = UNKNOW;

        public long onDestoryBeforeTime = UNKNOW;
        public long onDestoryAfterTime = UNKNOW;

        public int mStopped;
        public int mFinished;
        public int mDestroyed;

        public ActivityState(){
            reset();
        }

        public long getCreateDestoryDiff(){
            if(onCreateBeforeTime != UNKNOW && onDestoryAfterTime != UNKNOW){
                return onDestoryAfterTime - onCreateBeforeTime;
            }
            return UNKNOW;
        }

        public long getCreateUsedTime(){
            if(onCreateBeforeTime != UNKNOW && onCreateAfterTime != UNKNOW){
                return onCreateAfterTime - onCreateBeforeTime;
            }
            return UNKNOW;
        }

        public long getResumeUsedTime(){
            if(onResumeBeforeTime != UNKNOW && onResumeAfterTime != UNKNOW){
                return (onResumeAfterTime - onResumeBeforeTime);
            }
            return UNKNOW;
        }

        public void reset(){
            onCreateBeforeTime = UNKNOW;
            onCreateAfterTime = UNKNOW;
            onResumeBeforeTime = UNKNOW;
            onResumeAfterTime = UNKNOW;
            mStopped = UNKNOW;
            mFinished = UNKNOW;
            mDestroyed = UNKNOW;
        }

    }

    //支持各个方向对齐的情况
    public static Rect getRectWithMeasured(int width, int height, int paddingVertical, int paddingHorizontal, int gravity){
        Context context = AdClientContext.getClientContext();
        int w = UIHelper.dip2px(context,width);
        int h = UIHelper.dip2px(context,height);
        int top, right, bottom, left;
        //int pt = UIHelper.dip2px(context,paddingTop);
        if((Gravity.LEFT & gravity) == Gravity.LEFT){
            left = UIHelper.dip2px(context, paddingHorizontal);
        }else {
            left = AdClientContext.displayWidth - UIHelper.dip2px(context, paddingHorizontal) - w;
        }
        right = left + w;
        if((Gravity.BOTTOM & gravity) == Gravity.BOTTOM){
            top = AdClientContext.displayHeight - UIHelper.dip2px(context, paddingVertical) - w;
        }else {
            top = UIHelper.dip2px(context, paddingVertical);
        }
        bottom = top + h;
        Rect rect = new Rect(left,top,right,bottom);
        return rect;
    }

    //支持各个方向对齐的情况——横屏
    public static Rect getHorizontalRectWithMeasured(int width, int height, int paddingVertical, int paddingHorizontal, int gravity){
        Context context = AdClientContext.getClientContext();
        int w = UIHelper.dip2px(context,width);
        int h = UIHelper.dip2px(context,height);
        int top, right, bottom, left;
        //int pt = UIHelper.dip2px(context,paddingTop);
        if((Gravity.LEFT & gravity) == Gravity.LEFT){
            left = UIHelper.dip2px(context, paddingHorizontal);
        }else {
            left = AdClientContext.displayHeight - UIHelper.dip2px(context, paddingHorizontal) ;
        }
        right = left + h;
        if((Gravity.BOTTOM & gravity) == Gravity.BOTTOM){
            top = AdClientContext.displayWidth - UIHelper.dip2px(context, paddingVertical) ;
        }else {
            top = UIHelper.dip2px(context, paddingVertical);
        }
        bottom = top + w;
        Rect rect = new Rect(left,top,right,bottom);
        Log.i(TAG,"getRectWithMeasured rect$left = "+rect.left+",top = "+rect.top+", right = "+right+",bottom = "+rect.bottom+", width = "+ AdClientContext.displayWidth+",height = "+ AdClientContext.displayHeight);
        return rect;
    }

    public static Rect getRectWithMeasured(int width, int height, int paddingTop, int gravity){
        return getRectWithMeasured(width,height,paddingTop,paddingTop,gravity);
    }


    public static boolean isViewCovered(final View view) {
        View currentView = view;

        Rect currentViewRect = new Rect();
        boolean partVisible = currentView.getGlobalVisibleRect(currentViewRect);
        boolean totalHeightVisible = (currentViewRect.bottom - currentViewRect.top) >= view.getMeasuredHeight();
        boolean totalWidthVisible = (currentViewRect.right - currentViewRect.left) >= view.getMeasuredWidth();
        boolean totalViewVisible = partVisible && totalHeightVisible && totalWidthVisible;
        if (!totalViewVisible)//if any part of the view is clipped by any of its parents,return true
            return true;

        while (currentView.getParent() instanceof ViewGroup) {
            ViewGroup currentParent = (ViewGroup) currentView.getParent();
            if (currentParent.getVisibility() != View.VISIBLE)//if the parent of view is not visible,return true
                return true;

            int start = indexOfViewInParent(currentView, currentParent);
            for (int i = start + 1; i < currentParent.getChildCount(); i++) {
                Rect viewRect = new Rect();
                view.getGlobalVisibleRect(viewRect);
                View otherView = currentParent.getChildAt(i);
                Rect otherViewRect = new Rect();
                otherView.getGlobalVisibleRect(otherViewRect);
                if (Rect.intersects(viewRect, otherViewRect))//if view intersects its older brother(covered),return true
                    return true;
            }
            currentView = currentParent;
        }
        return false;
    }


    private static int indexOfViewInParent(View view, ViewGroup parent) {
        int index;
        for (index = 0; index < parent.getChildCount(); index++) {
            if (parent.getChildAt(index) == view)
                break;
        }
        return index;
    }

}
