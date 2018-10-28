package com.growatt.energymanagement.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.growatt.energymanagement.R;


/**
 * 能耗-设备管理-设备详情
 */
public class EnergyDetailActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.Energy_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RadioGroup mRadioGroup = findViewById(R.id.radio_group);
        mRadioGroup.check(R.id.radio_month);
    }
}
