package com.growatt.energymanagement.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.growatt.energymanagement.R;
import com.growatt.energymanagement.msgs.GenerateElectricityMsg;
import com.growatt.energymanagement.utils.ChartUtil;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 发电-设备详情
 */
public class DeviceDetailActivity extends BasicActivity implements View.OnClickListener {

    private int timeType = 2;
    private String time = "";
    private String[] info = {"PACR","PACS","PACT","IPV1","IPV2","IPV3","VPV1","VPV2","VPV3","PAC"};
    private String[] infoName = {
            "输出功率R相",
            "输出功率S相",
            "输出功率T相",
            "第一路输入电流",
            "第二路输入电流",
            "第三路输入电流",
            "第一路输入电压",
            "第二路输入电压",
            "第三路输入电压",
            "PV功率"};
    private int n = 9;  //记录选中第几个info
    private TextView infoText;
    private TextView timeTypeText;
    private Intent intent;
    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.device_detail_back).setOnClickListener(this);
        initData();
        findViews();
    }

    private void findViews() {
        mChart = findViewById(R.id.chart);
        infoText = findViewById(R.id.info_name);
        timeTypeText = findViewById(R.id.time_type_text);
        infoText.setOnClickListener(this);
        timeTypeText.setOnClickListener(this);
        RadioGroup timeTypes = findViewById(R.id.time_type_group);
        timeTypes.check(R.id.time_type_day);
        switchTimeType(R.id.time_type_day);
        timeTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switchTimeType(checkedId);
            }
        });
    }

    private void switchTimeType(int id) {
        if (time.equals("")) {
            time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        switch (id) {
            case R.id.time_type_day:
                timeType = 2;
                if (timeTypeText.getVisibility() == View.GONE) {
                    timeTypeText.setVisibility(View.VISIBLE);
                }
                timeTypeText.setText(time.substring(0,7));
                InternetUtils.generateElectricity(intent.getStringExtra("devId"), timeType, info[n],time.substring(0, 4) + time.substring(5, 7));
                break;
            case R.id.time_type_month:
                timeType = 3;
                if (timeTypeText.getVisibility() == View.GONE) {
                    timeTypeText.setVisibility(View.VISIBLE);
                }
                timeTypeText.setText(time.substring(0,4));
                InternetUtils.generateElectricity(intent.getStringExtra("devId"), timeType,info[n], time.substring(0, 4));
                break;
            case R.id.time_type_year:
                timeType = 4;
                timeTypeText.setVisibility(View.GONE);
                InternetUtils.generateElectricity(intent.getStringExtra("devId"), timeType,info[n], time.substring(0, 4));
                break;
        }
    }

    private void initData() {
        intent = getIntent();
        ((TextView)findViewById(R.id.power)).setText(intent.getStringExtra("power"));
        ((TextView)findViewById(R.id.ele_day)).setText(intent.getStringExtra("ele_day"));
        ((TextView)findViewById(R.id.ele_total)).setText(intent.getStringExtra("ele_total"));
        String areaName = intent.getStringExtra("areaName");
        ((TextView)findViewById(R.id.area_name)).setText(areaName);
        String devName = intent.getStringExtra("devName");
        ((TextView)findViewById(R.id.device_name)).setText(devName);
        String installTime = intent.getStringExtra("installTime");
        if (installTime != null && !installTime.equals("null")){
            ((TextView)findViewById(R.id.install_time)).setText(installTime);
        }
        String s = areaName + devName;
        ((TextView)findViewById(R.id.title)).setText(s);
        ImageView imageView = findViewById(R.id.status_circle);
        TextView textView = findViewById(R.id.running_status);
        if (intent.getStringExtra("status").equals("0")){
            imageView.setImageResource(R.mipmap.status_3);
            textView.setText("离线");
            textView.setTextColor(0xff555555);
        }else {
            imageView.setImageResource(R.mipmap.status_2);
            textView.setText("在线");
            textView.setTextColor(0xff00cc00);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.device_detail_back:
                finish();
                break;
            case R.id.info_name:
                dropMenu();
                break;
            case R.id.time_type_text:
                selectTime();
                break;
        }
    }

    private void selectTime() {
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",getResources().getConfiguration().locale);
                time = format.format(date);
                if (timeType == 2) {
                    timeTypeText.setText(time);
                    InternetUtils.generateElectricity(intent.getStringExtra("devId"), timeType, info[n],time.substring(0, 4) + time.substring(5, 7));
                } else {
                    timeTypeText.setText(time.substring(0, 7));
                    InternetUtils.generateElectricity(intent.getStringExtra("devId"), timeType,info[n], time.substring(0, 4));
                }
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

    private void dropMenu() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_drop_menu,null);
        LinearLayout layout = view.findViewById(R.id.linear_layout);
        final PopupWindow popupWindow = new PopupWindow(view,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView textView;
        for (int i = 0; i < info.length; i++) {
            textView = new TextView(this);
            final String name = infoName[i];
            textView.setText(name);
            textView.setPadding(10,2,10,2);
            textView.setTextColor(0xFFFFFFFF);
            textView.setBackgroundColor(0xff022632);
            textView.setTextSize(15);
            layout.addView(textView);
            final int a = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    infoText.setText(name);
                    n = a;
                    if (time.equals("")) {
                        time = new SimpleDateFormat("yyyy-MM-dd",getResources().getConfiguration().locale).format(new Date());
                    }
                    if (timeType == 2) {
                        timeTypeText.setText(time);
                        InternetUtils.generateElectricity(intent.getStringExtra("devId"), timeType, info[a],time.substring(0, 4) + time.substring(5, 7));
                    } else {
                        timeTypeText.setText(time.substring(0, 7));
                        InternetUtils.generateElectricity(intent.getStringExtra("devId"), timeType,info[a], time.substring(0, 4));
                    }
                }
            });
        }
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(infoText);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sss(GenerateElectricityMsg msg) {
        if (msg.code.equals("0")) {
            showChart(msg.list);
        } else {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showChart(List<Entry> list) {

        mChart.setDrawGridBackground(false);
        mChart.setDrawBorders(false);
        mChart.setDragEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.animateX(800);
        mChart.getDescription().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
//        xAxis.setLabelCount(12);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(0xff0070a3);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(10);
        xAxis.setAxisLineColor(getResources().getColor(R.color.colorText_01));
        xAxis.setAxisLineWidth(2f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                switch (timeType) {
                    case 2:
                        return (int)value + "日";
                    case 3:
                        return (int)value + "月";
                    case 4:
                        return (int) value + "年";
                    default:
                        return "";
                }
            }
        });

        YAxis leftYAxis = mChart.getAxisLeft();
        leftYAxis.setDrawGridLines(true);
        leftYAxis.setTextColor(0xff0070a3);
        leftYAxis.setTextSize(10);
        leftYAxis.setGridColor(0xff1B363F);
        leftYAxis.setAxisMinimum(0f);
        leftYAxis.setAxisLineColor(getResources().getColor(R.color.colorText_01));

        YAxis rightYAxis = mChart.getAxisRight();
        rightYAxis.setEnabled(false);

        Legend legend = mChart.getLegend();
        legend.setEnabled(false);
//        legend.setTextColor(0xffffffff);
//        legend.setTextSize(12);

        List<ILineDataSet> dataSets = new ArrayList<>();
        if (list != null && list.size() > 0) {
            LineDataSet dataSet_1 = new LineDataSet(list, "");
            ChartUtil.lineSet(this, dataSet_1, 0xFF00FF72,LineDataSet.Mode.CUBIC_BEZIER,R.drawable.line_shape_green);
            dataSets.add(dataSet_1);
        }
        LineData data = new LineData(dataSets);
        mChart.setData(data);
    }
}
