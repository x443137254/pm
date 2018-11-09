package com.growatt.energymanagement.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.adapters.NoticeListAdapter;

import java.text.SimpleDateFormat;


/**
 * 通知
 */
public class NoticeActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.notice_list_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        if (intent == null) return;
        ((TextView)findViewById(R.id.warn_msg)).setText(changeName(intent.getStringExtra("devType")));
        ((TextView)findViewById(R.id.warn_time)).setText(new SimpleDateFormat("MM月dd日 HH:mm"
                ,getResources().getConfiguration().locale)
                .format(intent.getLongExtra("time",0)));
        findViewById(R.id.warn_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoticeActivity.this,WarnListActivity.class));
            }
        });
    }

    private String changeName(String s){
        if (s == null) return "";
        if (s.equals("ammeter")) return "电表";
        return "逆变器告警";
    }
}
