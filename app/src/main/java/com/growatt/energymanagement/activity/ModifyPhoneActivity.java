package com.growatt.energymanagement.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;

import com.growatt.energymanagement.R;


/**
 *
 * 个人-个人资料-修改手机
 */
public class ModifyPhoneActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_phone);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.modify_phone_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
