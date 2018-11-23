package com.growatt.energymanagement.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.growatt.energymanagement.adapters.EnergyDetailListAdapter;
import com.growatt.energymanagement.msgs.AreaEleInfoMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.utils.ChartUtil;
import com.growatt.energymanagement.utils.CommentUtils;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 能耗-电量详情
 */
public class EnergyConsumeDetailActivity extends BasicActivity implements View.OnClickListener {

    private TextView timePicker01;
    private TextView timePicker02;
    private ImageView timeAdd;
    private String time_1 = "";
    private String time_2 = "";
    private String time_cur = "";
    private int timeType = 3;
    private String path;
    private LineChart mChart;
    private EnergyDetailListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy_consume_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.consume_detail_back).setOnClickListener(this);
        ListView detailList = findViewById(R.id.energy_consume_detail_list);
        adapter = new EnergyDetailListAdapter(this, timeType);
        detailList.setAdapter(adapter);

        path = getIntent().getStringExtra("path");
        mChart = findViewById(R.id.line_chart);
        timePicker01 = findViewById(R.id.time_picker_01);
        timePicker02 = findViewById(R.id.time_picker_02);
        timeAdd = findViewById(R.id.time_next);
        ImageView timeMinus = findViewById(R.id.time_last);
        timeAdd.setOnClickListener(this);
        timeMinus.setOnClickListener(this);
        timePicker02.setOnClickListener(this);
        time_cur = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale).format(new Date());
        time_1 = time_cur;
        time_2 = time_cur;
        timePicker01.setText(time_cur);
        timePicker02.setText(time_cur.substring(0, 7));

        RadioGroup radioGroup = findViewById(R.id.time_group);
        radioGroup.check(R.id.time_month);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.time_month) {
                    timeType = 3;
                    timePicker02.setText(time_2.substring(0, 7));
                    InternetUtils.areaEleInfo(LoginMsg.uniqueId, timeType, time_2.substring(0, 4) + time_2.substring(5, 7), path);
                } else {
                    timeType = 4;
                    timePicker02.setText(time_2.substring(0, 4));
                    InternetUtils.areaEleInfo(LoginMsg.uniqueId, timeType, time_2.substring(0, 4), path);
                }
            }
        });

        InternetUtils.areaEleInfo(LoginMsg.uniqueId, timeType, time_2.substring(0, 4) + time_2.substring(5, 7), path);
        InternetUtils.areaEleInfo(LoginMsg.uniqueId, 2,
                time_1.substring(0, 4) + time_1.substring(5, 7) + time_1.substring(8, 10),
                path);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.consume_detail_back:
                finish();
                break;
            case R.id.time_picker_02:
                selectTime();
                break;
            case R.id.time_last:
                minusDate();
                break;
            case R.id.time_next:
                plusDate();
                break;
        }
    }

    private void selectTime() {
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
                time_2 = format.format(date);
                if (timeType == 3) {
                    timePicker02.setText(time_2.substring(0, 7));
                    InternetUtils.areaEleInfo(LoginMsg.uniqueId, timeType, time_2.substring(0, 4) + time_2.substring(5, 7), path);
                } else {
                    timePicker02.setText(time_2.substring(0, 4));
                    InternetUtils.areaEleInfo(LoginMsg.uniqueId, timeType, time_2.substring(0, 4), path);
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

    private void plusDate() {
        int date = Integer.parseInt(CommentUtils.getNumFromString(time_1));
        int day = date % 100;
        int month = date / 100 % 100;
        int year = date / 10000;
        int temp = CommentUtils.maxDays(year, month);
        if (day < temp) {
            day += 1;
        } else {
            if (month < 12) {
                month += 1;
                day = 1;
            } else {
                year += 1;
                month = 1;
                day = 1;
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append(String.valueOf(year));
        builder.append("-");
        if (month < 10) {
            builder.append("0");
        }
        builder.append(String.valueOf(month));
        builder.append("-");
        if (day < 10) {
            builder.append("0");
        }
        builder.append(String.valueOf(day));
        time_1 = builder.toString();
        timePicker01.setText(time_1);

        InternetUtils.areaEleInfo(LoginMsg.uniqueId, 2,
                time_1.substring(0, 4) + time_1.substring(5, 7) + time_1.substring(8, 10),
                path);

        if (time_1.equals(time_cur)) {
            timeAdd.setImageResource(R.mipmap.next_noclick);
            timeAdd.setEnabled(false);
        } else {
            timeAdd.setImageResource(R.mipmap.next);
            timeAdd.setEnabled(true);
        }
    }

    private void minusDate() {
        int date = Integer.parseInt(CommentUtils.getNumFromString(time_1));
        int day = date % 100;
        int month = date / 100 % 100;
        int year = date / 10000;
        if (day > 1) {
            day -= 1;
        } else {
            if (month > 1) {
                month -= 1;
                day = CommentUtils.maxDays(year, month);
            } else {
                year -= 1;
                month = 12;
                day = CommentUtils.maxDays(year, month);
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append(String.valueOf(year));
        builder.append("-");
        if (month < 10) {
            builder.append("0");
        }
        builder.append(String.valueOf(month));
        builder.append("-");
        if (day < 10) {
            builder.append("0");
        }
        builder.append(String.valueOf(day));
        time_1 = builder.toString();
        timePicker01.setText(time_1);
        InternetUtils.areaEleInfo(LoginMsg.uniqueId, 2,
                time_1.substring(0, 4) + time_1.substring(5, 7) + time_1.substring(8, 10),
                path);
        timeAdd.setImageResource(R.mipmap.next);
        timeAdd.setEnabled(true);
    }

    private void showChart(List<Entry> line) {
        if (line == null || line.size() <= 0) {
            mChart.clear();
            return;
        }
        mChart.setDrawGridBackground(false);
        mChart.setDrawBorders(false);
        mChart.setDragEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.animateX(1000);
        mChart.getDescription().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setLabelCount(9);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(0xff0070a3);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(10);
        xAxis.setAxisLineColor(getResources().getColor(R.color.colorText_01));
        xAxis.setAxisLineWidth(2f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (int) value + "时";
            }
        });

        YAxis leftYAxis = mChart.getAxisLeft();
        leftYAxis.setDrawGridLines(true);
        leftYAxis.setTextColor(0xff0070a3);
        leftYAxis.setTextSize(10);
        leftYAxis.setLabelCount(4);
        leftYAxis.setGridColor(0xff1B363F);
        leftYAxis.setAxisLineColor(getResources().getColor(R.color.colorText_01));

        YAxis rightYAxis = mChart.getAxisRight();
        rightYAxis.setEnabled(false);

        Legend legend = mChart.getLegend();
        legend.setTextColor(0xffffffff);
        legend.setTextSize(12);
        legend.setEnabled(false);

        LineDataSet dataSet_1 = new LineDataSet(line, "");

        ChartUtil.lineSet(this, dataSet_1, 0xFFAB8CFF, LineDataSet.Mode.CUBIC_BEZIER, R.drawable.line_shape_purple);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet_1);
        LineData data = new LineData(dataSets);
        mChart.setData(data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sda(AreaEleInfoMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            if (msg.timeType == 2) {
                showChart(msg.list);
            } else {
                adapter.setList(msg.list,timeType);
            }
        }
    }
}
