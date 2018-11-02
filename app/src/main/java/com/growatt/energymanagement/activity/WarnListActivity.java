package com.growatt.energymanagement.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.adapters.WarmListAdapter;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.NoticeListMsg;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 告警列表
 */
public class WarnListActivity extends BasicActivity {

    private WarmListAdapter adapter;

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
        adapter = new WarmListAdapter(this, null);
        ((ListView) findViewById(R.id.warm_list)).setAdapter(adapter);
        InternetUtils.noticeList(LoginMsg.uniqueId, getIntent().getIntExtra("type",0));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showWarnNum(NoticeListMsg msg) {
        if (msg.code.equals("0") && msg.list != null) {
            adapter.setList(msg.list);
        } else Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
    }
}
