package com.adsdk.demo.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.adsdk.demo.GlobalConfig;


public class DevMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalConfig.RConfig.MAIN_ACTIVITY_DEV_LAYOUT_ID);

        this.findViewById(GlobalConfig.RConfig.MAIN_ACTIVITY_BTN_ID).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "xx", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
