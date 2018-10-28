package com.tuolve.powermanager.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.tuolve.powermanager.R;
import com.tuolve.powermanager.adapters.NoticeListAdapter;

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
        ((ListView)findViewById(R.id.notice_list)).setAdapter(new NoticeListAdapter(this));
    }
}
