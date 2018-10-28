package com.growatt.energymanagement.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.adapters.WarmListAdapter;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.utils.InternetUtils;

/**
 * 告警列表
 */
public class WarnListActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warm_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.warm_msg_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((ListView)findViewById(R.id.warm_list)).setAdapter(new WarmListAdapter(this));
        InternetUtils.notice(LoginMsg.uniqueId,1,"warning","inverter");
    }
}
