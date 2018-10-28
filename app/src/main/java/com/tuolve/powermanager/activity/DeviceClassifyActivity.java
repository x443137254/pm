package com.tuolve.powermanager.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.tuolve.powermanager.R;
import com.tuolve.powermanager.adapters.DeviceClaLisAdapter;
import com.tuolve.powermanager.msgs.LoginMsg;
import com.tuolve.powermanager.utils.InternetUtils;

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
