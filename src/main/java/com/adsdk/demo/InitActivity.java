package com.adsdk.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adsdk.demo.common.ThreadExecutor;
import com.adsdk.demo.pkg.sdk.client.AdController;

/**
 *  demo中 我们所有的请求已经做过线程优化处理 请勿在子线程中请求 ！！！
 *
 *  不推荐自定义跳过 如需要自定义跳过参考
 *  👇👇👇👇👇👇👇
 *  @see SplashSkipViewActivity
 */
public class InitActivity extends Activity {

    static final String TAG = "Demo_TAG";

    private AdController adController;
    private EditText editText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        progressBar = findViewById(R.id.progressBar);
        editText = findViewById(R.id.posId);
    }


    public void onClick(View view){
        switch (view.getId()) {
            case R.id.btn_loadAndShow:
                progressBar.setVisibility(View.VISIBLE);
                ThreadExecutor.getMainThreadHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(InitActivity.this,"SDK初始化成功",Toast.LENGTH_LONG).show();
                    }
                },2000);


                break;
        }
    }


}
