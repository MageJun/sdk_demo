package com.adsdk.demo.common;

import android.view.View;

public class GridViewConfig {
    public String text;
    public String codeid;
    public Class activity;
    public View.OnClickListener viewClickListener;

    public GridViewConfig(String text, String codeid, Class activity) {
        this.text = text;
        this.codeid = codeid;
        this.activity = activity;
    }

    public GridViewConfig(String text, String codeid, Class activity, View.OnClickListener viewClickListener) {
        this.text = text;
        this.codeid = codeid;
        this.activity = activity;
        this.viewClickListener = viewClickListener;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCodeid() {
        return codeid;
    }

    public void setCodeid(String codeid) {
        this.codeid = codeid;
    }

    public Class getActivity() {
        return activity;
    }

    public void setActivity(Class activity) {
        this.activity = activity;
    }
}
