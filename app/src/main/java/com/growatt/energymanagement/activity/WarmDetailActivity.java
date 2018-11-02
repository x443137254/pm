package com.growatt.energymanagement.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.msgs.NoticeInfoMsg;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 告警详情
 */
public class WarmDetailActivity extends BasicActivity {

    private TextView sn;
    private TextView name;
    private TextView fault_num;
    private TextView fault_value;
    private TextView time;
    private TextView detail;
    private TextView suggest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warm_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.warm_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sn = findViewById(R.id.sn);
        name = findViewById(R.id.name);
        fault_num = findViewById(R.id.fault_num);
        fault_value = findViewById(R.id.fault_value);
        time = findViewById(R.id.time);
        detail = findViewById(R.id.detail);
        suggest = findViewById(R.id.suggest);
        Intent intent = getIntent();
        if (intent == null) return;
        InternetUtils.noticeInfo("warning",intent.getIntExtra("cid",0),intent.getStringExtra("devType"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getData(NoticeInfoMsg msg){
        if (msg.code.equals("1")){
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        }else {
            sn.setText(msg.sn);
            name.setText(changeName(msg.devType));
            fault_value.setText(String.valueOf(msg.event));
            time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"
                    ,getResources().getConfiguration().locale).format(new Date(msg.time)));
            suggest.setText(msg.solution);
            detail.setText(msg.name);
        }
    }

    private String changeName(String s){
        if (s.equals("ammeter")) return "电表";
        else return "逆变器";
    }
}
