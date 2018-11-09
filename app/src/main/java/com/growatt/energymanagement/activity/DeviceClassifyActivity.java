package com.growatt.energymanagement.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.adapters.DeviceClaLisAdapter;
import com.growatt.energymanagement.msgs.DevsDetailInfoMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 能耗-设备管理
 */
public class DeviceClassifyActivity extends BasicActivity {

    private TextView devTotal;
    private TextView consTotal;
    private DeviceClaLisAdapter adapter;
    private TabLayout tabs;

    private List<DevsDetailInfoMsg.Dev> all_list;
    private List<DevsDetailInfoMsg.Dev> running_list;
    private List<DevsDetailInfoMsg.Dev> error_list;
    private List<DevsDetailInfoMsg.Dev> sleeping_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_classify);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.device_classify_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String devType = getIntent().getStringExtra("devType");
        setActivityTitle(devType);
        adapter = new DeviceClaLisAdapter(this, all_list, devType);
        ((ListView) findViewById(R.id.device_classify_list)).setAdapter(adapter);
        if (devType == null || devType.equals("") || devType.equals("null")) return;
        InternetUtils.devsDetailInfo(LoginMsg.uniqueId, devType);
        devTotal = findViewById(R.id.dev_total);
        consTotal = findViewById(R.id.consume_total);
        tabs = findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String s = tab.getText().toString();
                if (s.contains("全部")) {
                    adapter.setList(all_list);
                } else if (s.contains("运行中")) {
                    adapter.setList(running_list);
                } else if (s.contains("待机")) {
                    adapter.setList(sleeping_list);
                } else if (s.contains("故障")) {
                    adapter.setList(error_list);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setActivityTitle(String devType) {
        if (devType == null) return;
        TextView textView = findViewById(R.id.title);
        switch (devType) {
            case "airCondition":
                textView.setText("空调");
                break;
            case "socket":
                textView.setText("插座");
                break;
            case "chargePile":
                textView.setText("充电桩");
                break;
            case "thermostat":
                textView.setText("温控器");
                break;
            case "shineBoost":
                textView.setText("shineBoost");
                break;
            default:
                textView.setText("");
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showList(DevsDetailInfoMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            String s = String.valueOf(msg.all.devsNum) + "台";
            devTotal.setText(s);
            s = String.valueOf(msg.all.totalEle) + "kWh";
            consTotal.setText(s);
            tabs.removeAllTabs();
            TabLayout.Tab tab_all = tabs.newTab();
            TabLayout.Tab tab_running = tabs.newTab();
            TabLayout.Tab tab_sleep = tabs.newTab();
            TabLayout.Tab tab_error = tabs.newTab();
            s = "全部" + msg.all.devsNum;
            tab_all.setText(s);
            s = "运行中" + msg.running.devsNum;
            tab_running.setText(s);
            s = "待机" + msg.sleeping.devsNum;
            tab_sleep.setText(s);
            s = "故障" + msg.error.devsNum;
            tab_error.setText(s);
            tabs.addTab(tab_all);
            tabs.addTab(tab_running);
            tabs.addTab(tab_sleep);
            tabs.addTab(tab_error);
            all_list = msg.all.devs;
            running_list = msg.running.devs;
            error_list = msg.error.devs;
            sleeping_list = msg.sleeping.devs;
            adapter.setList(all_list);
        }
    }
}
