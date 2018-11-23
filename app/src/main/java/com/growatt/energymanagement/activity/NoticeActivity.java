package com.growatt.energymanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.NotificationMsg;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;


/**
 * 通知
 */
public class NoticeActivity extends BasicActivity {

    private TextView reportMsg;
    private TextView occurrenceTime;

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
        ((TextView) findViewById(R.id.warn_msg)).setText(changeName(intent.getStringExtra("devType")));
        ((TextView) findViewById(R.id.warn_time)).setText(new SimpleDateFormat("MM月dd日 HH:mm"
                , getResources().getConfiguration().locale)
                .format(intent.getLongExtra("time", 0)));
        findViewById(R.id.warn_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoticeActivity.this, WarnListActivity.class));
            }
        });
        findViewById(R.id.report_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoticeActivity.this, ReportDetailActivity.class));
            }
        });

        reportMsg = findViewById(R.id.report_msg);
        occurrenceTime = findViewById(R.id.occurrenceTime);

        InternetUtils.notice(LoginMsg.uniqueId, 1, "report", "");
    }

    private String changeName(String s) {
        if (s == null) return "";
        if (s.equals("ammeter")) return "电表";
        return "逆变器告警";
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ooo(NotificationMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        }else {
            NotificationMsg.ReportEntry reportEntry = msg.reportList.get(0);
            if (reportEntry == null) return;
            occurrenceTime.setText(reportEntry.occurrenceTime);
//            reportMsg.setText("");
        }
    }
}
