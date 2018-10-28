package com.growatt.energymanagement.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.adapters.DeviceClaLisAdapter;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.utils.InternetUtils;

/**
 * 能耗-设备管理
 */
public class DeviceClassifyActivity extends BasicActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_classify);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.device_classify_back).setOnClickListener(this);
        ((ListView)findViewById(R.id.device_classify_list)).setAdapter(new DeviceClaLisAdapter(this));
        String devType = getIntent().getStringExtra("devType");
        if (devType == null || devType.equals("") || devType.equals("null")) return;
        InternetUtils.devsDetailInfo(LoginMsg.uniqueId,devType);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.device_classify_back:
                finish();
                break;
        }
    }
}
