package com.tuolve.powermanager.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.tuolve.powermanager.R;
import com.tuolve.powermanager.adapters.WarmListAdapter;
import com.tuolve.powermanager.msgs.LoginMsg;
import com.tuolve.powermanager.utils.InternetUtils;

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
