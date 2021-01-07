package com.adsdk.demo.common;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adsdk.demo.R;

/**
 * 描述：
 * 时间： 2021/1/5.
 * 创建： WL
 */

public class BaseActivity extends Activity {

    private EditText editText;

    private TextView baseTitle;

    private ViewGroup baseContentLayout;

    private View baseTitleLayout;

    private View editLayout;

    private Button baseBtn;

    private View progressBar;
    protected Context mBaseContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_activity);
        mBaseContext = this;
        baseContentLayout = findViewById(R.id.base_content_layout);
        baseTitle = findViewById(R.id.base_title);
        baseTitleLayout =findViewById(R.id.base_title_layout);
        editLayout = findViewById(R.id.base_edit_layout);
        progressBar = findViewById(R.id.base_progress_bar);
        baseBtn = findViewById(R.id.base_btn);
        editLayout.setVisibility(View.GONE);
        baseBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void setContentView(int layoutResID) {
        setContentLayoutView(layoutResID);
    }

    public void setContentLayoutView(int rid) {
        try{
            LayoutInflater.from(this).inflate(rid,baseContentLayout);
            editText = findViewById(R.id.posId);
            if(editText!=null){
                editText.setText(getIntent().getStringExtra("codid"));
            }
        }catch (Exception e){

        }

    }

    protected  void setTitle(String title){
        baseTitle.setText(title);
    }

    protected  void invisiTitle(){
        baseTitleLayout.setVisibility(View.GONE);
    }
    protected  void showTitle(){
        baseTitleLayout.setVisibility(View.VISIBLE);
    }

    protected  void showEditLayout(){
        editLayout.setVisibility(View.VISIBLE);
    }

    protected  void showBaseBtn(){
        baseBtn.setVisibility(View.VISIBLE);
    }

    protected  void invisibleEditLayout(){
        editLayout.setVisibility(View.GONE);
        baseBtn.setVisibility(View.GONE);
    }

    protected void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void invisibleProgress(){
        progressBar.setVisibility(View.GONE);
    }

    public void onBaseClick(View view){
        switch (view.getId()){
            case R.id.base_btn:
                onBaseBtnClick();
                break;
        }
    }
    protected void onBaseBtnClick(){

    }

    protected void loadSuccess(){
        Toast.makeText(mBaseContext,"广告请求成功",Toast.LENGTH_SHORT).show();
    }
    protected void exposureSuccess(){
        Toast.makeText(mBaseContext,"广告曝光成功",Toast.LENGTH_SHORT).show();
    }

}
