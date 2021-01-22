/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.adsdk.demo.common;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.R;


/**
 * Control on privacy-related dialog boxes.
 */
public class ProtocolDialog extends Dialog {
    private static final String ACTION_SIMPLE_PRIVACY = "com.huawei.hms.ppskit.ACTION.SIMPLE_PRIVACY";

    private static final String ACTION_OAID_SETTING = "com.huawei.hms.action.OAID_SETTING";

    private Context mContext;

    private TextView titleTv;

    private TextView protocolTv;

    private Button confirmButton;

    private Button cancelButton;

    private LayoutInflater inflater;

    private ProtocolDialogCallback mCallback;

    /**
     * Privacy protocol dialog box callback interface.
     */
    public interface ProtocolDialogCallback {
        void agree();
        void cancel();
    }

    /**
     * Constructor.
     *
     * @param context context
     */
    public ProtocolDialog(@NonNull Context context) {
        // Customize a dialog box style.
        super(context, R.style.dialog);
        mContext = context;
    }

    /**
     * Set a dialog box callback.
     *
     * @param callback callback
     */
    public void setCallback(ProtocolDialogCallback callback) {
        mCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window dialogWindow = getWindow();
        dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);

        inflater = LayoutInflater.from(mContext);
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.dialog_protocol, null);
        setContentView(rootView);

        titleTv = findViewById(R.id.uniform_dialog_title);
        titleTv.setText(mContext.getString(R.string.protocol_title));
        protocolTv = findViewById(R.id.protocol_center_content);

        initButtonBar(rootView);
    }

    /**
     * Initialize the page of the button bar.
     *
     * @param rootView rootView
     */
    private void initButtonBar(LinearLayout rootView) {
        confirmButton = rootView.findViewById(R.id.base_okBtn);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences =
                    mContext.getSharedPreferences(GlobalConfig.SP_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(GlobalConfig.SP_PROTOCOL_KEY, 1).commit();
                dismiss();

                if (mCallback != null) {
                    mCallback.agree();
                }
            }
        });

        cancelButton = rootView.findViewById(R.id.base_cancelBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mCallback != null) {
                    mCallback.cancel();
                }
            }
        });
    }

}
