package com.growatt.energymanagement.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.msgs.DevDetailInfoMsg;
import com.growatt.energymanagement.msgs.SettingMsg;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 能耗-设备管理-设备详情
 */
public class EnergyDetailActivity extends BasicActivity implements View.OnClickListener {

    private TextView title;
    private TextView runningStatus;
    private TextView power;
    private TextView current;
    private TextView volt;
    private TextView status;
    private TextView roomTemp;
    private TextView setTemp;
    private TextView sn;
    private TextView areaName;
    private TextView consumeCate;
    private ImageView swithButton;
    private String devId;
    private String devType;

    private int on = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.Energy_detail_back).setOnClickListener(this);
        RadioGroup mRadioGroup = findViewById(R.id.radio_group);
        mRadioGroup.check(R.id.radio_month);
        Intent intent = getIntent();
        if (intent == null) return;
        devId = intent.getStringExtra("devId");
        devType = intent.getStringExtra("devType");

        InternetUtils.devDetailInfo(devId, devType);

        title = findViewById(R.id.dev_name);
        runningStatus = findViewById(R.id.running_status);
        power = findViewById(R.id.power);
        current = findViewById(R.id.current);
        volt = findViewById(R.id.volt);
        status = findViewById(R.id.status);
        roomTemp = findViewById(R.id.room_temp);
        setTemp = findViewById(R.id.set_temp);
        sn = findViewById(R.id.sn);
        areaName = findViewById(R.id.area_name);
        consumeCate = findViewById(R.id.consume_cate);
        swithButton = findViewById(R.id.switch_bt);
        swithButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Energy_detail_back:
                finish();
                break;
            case R.id.switch_bt:
                if (on == 0){
                    swithButton.setImageResource(R.mipmap.detail_off);
                    runningStatus.setText("待机中");
                    on = 1;
                }else {
                    swithButton.setImageResource(R.mipmap.detail_on);
                    runningStatus.setText("运行中");
                    on = 0;
                }
                InternetUtils.setting(devId,devType,on);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sdfa(DevDetailInfoMsg msg){
        if (msg.code.equals("1")){
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        }else {
            power.setText(String.valueOf(msg.power));
            current.setText(String.valueOf(msg.current));
            volt.setText(String.valueOf(msg.voltage));
            roomTemp.setText(String.valueOf(msg.temp_room));
            setTemp.setText(String.valueOf(msg.temp_set));
            sn.setText(String.valueOf(msg.sn));
            areaName.setText(String.valueOf(msg.areaName));
            consumeCate.setText(String.valueOf(msg.classify));
            if (msg.onoff == 0){
                swithButton.setImageResource(R.mipmap.detail_on);
                runningStatus.setText("运行中");
                on = 0;
            }else {
                swithButton.setImageResource(R.mipmap.detail_off);
                runningStatus.setText("待机中");
                on = 1;
            }
            if (msg.online == 0){
                status.setText("在线");
            }else status.setText("离线");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sdfa(SettingMsg msg){
        if (msg.code.equals("1")){
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        }else {

        }
    }
}
