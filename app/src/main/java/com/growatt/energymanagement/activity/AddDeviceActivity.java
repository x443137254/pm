package com.growatt.energymanagement.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;

import com.growatt.energymanagement.R;


/**
 *能耗-添加设备
 */
public class AddDeviceActivity extends BasicActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.add_device_back).setOnClickListener(this);
        findViewById(R.id.air_condition).setOnClickListener(this);
        findViewById(R.id.illumination).setOnClickListener(this);
        findViewById(R.id.charge).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_device_back:
                finish();
                break;
        }
    }
}
