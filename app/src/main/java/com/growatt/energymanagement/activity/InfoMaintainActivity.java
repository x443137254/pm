package com.growatt.energymanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.growatt.energymanagement.R;
import com.growatt.energymanagement.msgs.AllAreaMsg;
import com.growatt.energymanagement.msgs.CountryDataMsg;
import com.growatt.energymanagement.msgs.EditPowerStationMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.PowerStationMsg;
import com.growatt.energymanagement.utils.CommentUtils;
import com.growatt.energymanagement.utils.InternetUtils;
import com.growatt.energymanagement.utils.LocationUtils;
import com.growatt.energymanagement.utils.RegionUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人-电站信息维护
 */
public class InfoMaintainActivity extends BasicActivity implements View.OnClickListener {

    private TextView country;
    private TextView subsidy;
    private TextView latitude;
    private TextView designCompany;
    private TextView name;
    private TextView setDate;
    private TextView power;
    private TextView timeArea;
    private TextView city;

    private final int MODIFY_NAME = 102;
    private final int MODIFY_DESIGN = 103;
    private final int MODIFY_POWER = 104;
    private final int MODIFY_TIME_AREA = 105;
    private final int MODIFY_SUBSIDY = 106;
    private OptionsPickerView<String> pvOptions;

    private List<String> provinceList;
    private List<List<String>> cityList;
    private List<List<List<String>>> areaList;

    private List<String> countryList;
    private List<List<String>> foreignCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_maintain);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.info_back).setOnClickListener(this);
        findViewById(R.id.power_station_name).setOnClickListener(this);
        findViewById(R.id.install_date).setOnClickListener(this);
        findViewById(R.id.design_firm).setOnClickListener(this);
        findViewById(R.id.design_rate).setOnClickListener(this);
        findViewById(R.id.country).setOnClickListener(this);
        findViewById(R.id.subsidy_item).setOnClickListener(this);
        findViewById(R.id.time_area_item).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.city_item).setOnClickListener(this);
        InternetUtils.powerStation(LoginMsg.uniqueId);
        country = findViewById(R.id.station);
        subsidy = findViewById(R.id.subsidy);
        latitude = findViewById(R.id.Latitude);
        designCompany = findViewById(R.id.designCompany);
        name = findViewById(R.id.name);
        setDate = findViewById(R.id.setDate);
        power = findViewById(R.id.power);
        timeArea = findViewById(R.id.timeArea);
        city = findViewById(R.id.city);

        String temp = LocationUtils.latitude + "\n" + LocationUtils.longitude;
        latitude.setText(temp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_back:
                finish();
                break;
            case R.id.power_station_name:
                startActivityForResult(new Intent(this, ModifyActivity.class), MODIFY_NAME);
                break;
            case R.id.design_firm:
                startActivityForResult(new Intent(this, ModifyActivity.class), MODIFY_DESIGN);
                break;
            case R.id.design_rate:
                startActivityForResult(new Intent(this, ModifyActivity.class), MODIFY_POWER);
                break;
            case R.id.subsidy_item:
                startActivityForResult(new Intent(this, ModifyActivity.class), MODIFY_SUBSIDY);
                break;
            case R.id.time_area_item:
//                startActivityForResult(new Intent(this, ModifyActivity.class), MODIFY_TIME_AREA);
                if (pvOptions != null) {
                    pvOptions.show();
                    return;
                }
                final List<String> data = new ArrayList<>();
                String[] array = getResources().getStringArray(R.array.time_area);
                Collections.addAll(data, array);
                pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        final String tx = data.get(options1);
                        InfoMaintainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timeArea.setText(tx);
                            }
                        });
                    }
                })
                        .setTitleText("请选择时区")
                        .setTitleBgColor(0xff032d3a)
                        .setTitleColor(0xffffffff)
                        .setSubmitColor(0xffffffff)
                        .setCancelColor(0xff058ef0)
                        .setBgColor(0xff032d3a)
                        .setTitleSize(22)
                        .setTextColorCenter(0xffffffff)
                        .build();
                pvOptions.setPicker(data);
                pvOptions.show();
                break;
            case R.id.install_date:
                selectTime();
                break;
            case R.id.country:
                if (countryList == null) {
                    showProgressDialog();
                    InternetUtils.countryData();
                }else {
                    CommentUtils.showPickView(this, countryList, country, "请选择国家");
                }
                break;
            case R.id.save:
                save();
                break;
            case R.id.city_item:
                String s = country.getText().toString();
                if (s.equals("中国")) {
                    if (provinceList != null) {
                        selectCity();
                    }
                    else {
                        showProgressDialog();
                        InternetUtils.allArea();
                    }
                } else {
                    if (countryList != null) {
                        int index = 0;
                        for (int i = 0; i < countryList.size(); i++) {
                            if (countryList.get(i).equals(country.getText().toString())){
                                index = i;
                                break;
                            }
                        }
                        CommentUtils.showPickView(this, foreignCityList.get(index), city, "请选择城市");
                    }else {
                        Toast.makeText(this, "请先选择国家", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void selectCity() {
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = cityList.get(options1).get(options2);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        city.setText(tx);
                    }
                });
            }
        })
                .setTitleText("请选择城市")
                .setTitleBgColor(0xff032d3a)
                .setTitleColor(0xffffffff)
                .setSubmitColor(0xffffffff)
                .setCancelColor(0xff058ef0)
                .setBgColor(0xff032d3a)
                .setTitleSize(22)
                .setTextColorCenter(0xffffffff)
                .build();
        pvOptions.setPicker(provinceList, cityList);
        pvOptions.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAllCity(AllAreaMsg msg) {
        disMissProgressDialog();
        provinceList = msg.provinceList;
        cityList = msg.cityList;
        areaList = msg.areaList;
        selectCity();
    }

    private void save() {
        String name = this.name.getText().toString();
        String setDate = this.setDate.getText().toString();
        String designCompany = this.designCompany.getText().toString();
        String s = this.power.getText().toString();
        int power = Integer.parseInt(s.substring(0, s.length() - 1));
        String country = this.country.getText().toString();
        String city = this.city.getText().toString();
        String timeArea = this.timeArea.getText().toString().substring(3);
        double subsidy = Double.parseDouble(this.subsidy.getText().toString().substring(1));
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("setDate", setDate);
        map.put("designCompany", designCompany);
        map.put("power", power);
        map.put("country", country);
        map.put("city", city);
        map.put("timeArea", timeArea);
        map.put("subsidy", subsidy);
        InternetUtils.editPowerStation(map);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void das(PowerStationMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            String temp;
            name.setText(msg.name);
            setDate.setText(msg.setDate);
            designCompany.setText(msg.designCompany);
            temp = msg.power + "W";
            power.setText(temp);
            country.setText(msg.country);
            city.setText(msg.city);
            temp = "GMT" + msg.timeArea;
            timeArea.setText(temp);
//            temp = msg.latitude + "\n" + msg.longitude;
//            latitude.setText(temp);
            temp = "￥" + msg.subsidy;
            subsidy.setText(temp);
        }
    }

    private void selectTime() {
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
                setDate.setText(format.format(date));
            }
        })
                .setTitleText("请选择")
                .setSubmitColor(0xffffffff)
                .setBackgroundId(0x00000000)
                .setBgColor(0xff032D3B)
                .setTitleBgColor(0xff032D3B)
                .setTitleColor(0xffffffff)
                .setTextColorCenter(0xffffffff)
                .setTextColorOut(0x66ffffff)
                .build();

        pvTime.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void daws(CountryDataMsg msg) {
        disMissProgressDialog();
        if (msg.code.equals("1")) {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            countryList = msg.countryList;
            foreignCityList = msg.cityList;
            CommentUtils.showPickView(this, msg.countryList, country, "请选择国家");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 1000) return;
        String s = data.getStringExtra("data");
        switch (requestCode) {
            case MODIFY_NAME:
                name.setText(s);
                break;
            case MODIFY_DESIGN:
                designCompany.setText(s);
                break;
            case MODIFY_POWER:
                try {
                    int d = Integer.parseInt(s);
                    s = String.valueOf(d) + "W";
                    power.setText(s);
                } catch (Exception e) {
                    Toast.makeText(this, getResources().getString(R.string.input_not_legal), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case MODIFY_TIME_AREA:
                s = "GMT" + s;
                timeArea.setText(s);
                break;
            case MODIFY_SUBSIDY:
                try {
                    float d = Float.parseFloat(s);
                    s = "￥" + String.format(getResources().getConfiguration().locale, "%.2f", d);
                    subsidy.setText(s);
                } catch (Exception e) {
                    Toast.makeText(this, getResources().getString(R.string.input_not_legal), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void d3aws(EditPowerStationMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.modify_success), Toast.LENGTH_SHORT).show();
        }
    }
}
