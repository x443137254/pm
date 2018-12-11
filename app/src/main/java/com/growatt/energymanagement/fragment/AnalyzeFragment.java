package com.growatt.energymanagement.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.growatt.energymanagement.R;
import com.growatt.energymanagement.activity.MainActivity;
import com.growatt.energymanagement.activity.WarnListActivity;
import com.growatt.energymanagement.msgs.AmmetersMsg;
import com.growatt.energymanagement.msgs.AnalysisDataMsg;
import com.growatt.energymanagement.msgs.AnalysisInfoMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.QualityDataMsg;
import com.growatt.energymanagement.utils.ChartUtil;
import com.growatt.energymanagement.utils.CommentUtils;
import com.growatt.energymanagement.utils.InternetUtils;
import com.growatt.energymanagement.view.CircleProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/8/14.
 * 分析
 */

public class AnalyzeFragment extends Fragment implements View.OnClickListener, TabLayout.OnTabSelectedListener {

    private TextView timePicker;
    private LineChart chart2;
    private LineChart chart1;
    private TextView totalFee;
    private TextView totalEle;
    private TextView compare;
    private TextView comparePercent;
    private TextView curEle;
    private TextView lastEle;
    private TextView curFee;
    private TextView lastFee;
    private CircleProgressBar compareCircle;
    private ImageView compareImg;
    private TabLayout tabs;
    private List<AmmetersMsg.AmmeterDev> mTabList;
    private AmmetersMsg.AmmeterDev ammeterDev;
    private TextView timePicker01;
    private ImageView timeAdd;
    private ImageView timeMinus;

    private String time_1 = "";
    private String time_2 = "";
    private String time_cur = "";
    private int dataType = 1;
    private QualityDataMsg dataMsg;

    private int timeType = 1;
    private TextView fromGrid;
    private TextView fromGridPercent;
    private TextView fromBms;
    private TextView fromBmsPercent;
    private ProgressBar percentProgress;
    private DisplayMetrics dm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Resources resources = getResources();
        dm = resources.getDisplayMetrics();
        if (MainActivity.isPad && dm.widthPixels > dm.heightPixels) {
            return inflater.inflate(R.layout.fragment_analyze_pad_h, container, false);
        } else return inflater.inflate(R.layout.fragment_analyze, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        timePicker = view.findViewById(R.id.time_pick);
        timePicker.setOnClickListener(this);
        chart1 = view.findViewById(R.id.line_chart_1);
        chart2 = view.findViewById(R.id.line_chart_2);
        totalFee = view.findViewById(R.id.fee_total);
        totalEle = view.findViewById(R.id.ele_total);
        lastFee = view.findViewById(R.id.fee_last);
        compare = view.findViewById(R.id.compare);
        comparePercent = view.findViewById(R.id.compare_percent);
        curEle = view.findViewById(R.id.ele_curr);
        lastEle = view.findViewById(R.id.ele_last);
        curFee = view.findViewById(R.id.fee_curr);
        compareCircle = view.findViewById(R.id.percent_circle);
        compareImg = view.findViewById(R.id.compare_ic);
        tabs = view.findViewById(R.id.analyze_tab);
        timePicker01 = view.findViewById(R.id.time_picker_01);
        timeAdd = view.findViewById(R.id.time_next);
        timeAdd.setEnabled(false);
        timeMinus = view.findViewById(R.id.time_last);
        fromGrid = view.findViewById(R.id.from_grid);
        fromGridPercent = view.findViewById(R.id.from_grid_percent);
        fromBms = view.findViewById(R.id.from_bms);
        fromBmsPercent = view.findViewById(R.id.from_bms_percent);
        percentProgress = view.findViewById(R.id.progress_percent);

        view.findViewById(R.id.warn_type_1).setOnClickListener(this);
        view.findViewById(R.id.warn_type_2).setOnClickListener(this);
        view.findViewById(R.id.warn_type_3).setOnClickListener(this);

        tabs.addOnTabSelectedListener(this);
        timeAdd.setOnClickListener(this);
        timeMinus.setOnClickListener(this);
        time_cur = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale).format(new Date());
        time_1 = time_cur;
        time_2 = time_cur;
        timePicker01.setText(time_1);
        RadioGroup mQualityRadioGroup = view.findViewById(R.id.quality_data_group);
        mQualityRadioGroup.check(R.id.radio_button_1);
        mQualityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_button_1:
                        dataType = 1;
                        break;
                    case R.id.radio_button_2:
                        dataType = 2;
                        break;
                    case R.id.radio_button_3:
                        dataType = 3;
                        break;
                }
                showChart1();
            }
        });

        RadioGroup timeGroup = view.findViewById(R.id.time_group);
        timeGroup.check(R.id.time_cur);
        if (time_2 == null || time_2.equals("") || time_2.equals("null")) {
            time_2 = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale).format(new Date());
        }
        timePicker.setText(time_2);
        InternetUtils.analysisInfo(LoginMsg.uniqueId, timeType, time_2.substring(0, 4) + time_2.substring(5, 7) + time_2.substring(8, 10));
        timeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.time_cur:
                        timeType = 1;
                        if (timePicker.getVisibility() == View.GONE)
                            timePicker.setVisibility(View.VISIBLE);
                        timePicker.setText(time_2);
                        InternetUtils.analysisInfo(LoginMsg.uniqueId, timeType, time_2.substring(0, 4) + time_2.substring(5, 7) + time_2.substring(8, 10));
                        break;
                    case R.id.time_day:
                        timeType = 2;
                        if (timePicker.getVisibility() == View.GONE)
                            timePicker.setVisibility(View.VISIBLE);
                        timePicker.setText(time_2.substring(0, 7));
                        InternetUtils.analysisInfo(LoginMsg.uniqueId, timeType, time_2.substring(0, 4) + time_2.substring(5, 7));
                        break;
                    case R.id.time_month:
                        timeType = 3;
                        if (timePicker.getVisibility() == View.GONE)
                            timePicker.setVisibility(View.VISIBLE);
                        timePicker.setText(time_2.substring(0, 4));
                        InternetUtils.analysisInfo(LoginMsg.uniqueId, timeType, time_2.substring(0, 4));
                        break;
                    case R.id.time_year:
                        timeType = 4;
                        timePicker.setVisibility(View.GONE);
                        InternetUtils.analysisInfo(LoginMsg.uniqueId, timeType, time_2.substring(0, 4));
                        break;
                }
            }
        });
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void getData() {
        InternetUtils.ammeters(LoginMsg.uniqueId);
        InternetUtils.analysisData(LoginMsg.uniqueId);
    }

    private void showChart2(List<Entry> line_1, List<Entry> line_2, List<Entry> line_3) {
        chart2.setDrawGridBackground(false);
        chart2.setDrawBorders(false);
        chart2.setDragEnabled(false);
        chart2.setTouchEnabled(false);
        chart2.animateX(1000);
        chart2.getDescription().setEnabled(false);

        XAxis xAxis = chart2.getXAxis();
        xAxis.setLabelCount(12);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(0xff0070a3);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(10);
        xAxis.setAxisLineColor(getResources().getColor(R.color.colorText_01));
        xAxis.setAxisLineWidth(2f);

        if (timeType == 3 || timeType == 4){
            xAxis.setLabelCount(line_2.size());
        }

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                switch (timeType) {
                    case 1:
                        return (int) value + "时";
                    case 2:
                        return (int) value + "日";
                    case 3:
                        return (int) value + "月";
                    case 4:
                        return (int) value + "年";
                }
                return null;
            }
        });

        YAxis leftYAxis = chart2.getAxisLeft();
        leftYAxis.setDrawGridLines(true);
        leftYAxis.setTextColor(0xff0070a3);
        leftYAxis.setTextSize(10);
        leftYAxis.setGridColor(0xff1B363F);
        leftYAxis.setAxisLineColor(getResources().getColor(R.color.colorText_01));

        YAxis rightYAxis = chart2.getAxisRight();
        rightYAxis.setEnabled(false);

        Legend legend = chart2.getLegend();
        legend.setTextColor(0xffffffff);
        legend.setTextSize(12);

        List<ILineDataSet> dataSets = new ArrayList<>();

        if (line_1 != null && line_1.size() > 0) {
            LineDataSet dataSet_1 = new LineDataSet(line_1, "BMS电池功率");
            ChartUtil.lineSet(getContext(), dataSet_1, 0xFF00FF7C, LineDataSet.Mode.CUBIC_BEZIER, 0);
            dataSets.add(dataSet_1);
        }

        if (line_2 != null && line_2.size() > 0) {
            LineDataSet dataSet_2 = new LineDataSet(line_2, "总功率");
            ChartUtil.lineSet(getContext(), dataSet_2, 0xFF288DFF, LineDataSet.Mode.CUBIC_BEZIER, 0);
            dataSets.add(dataSet_2);
        }

        if (line_2 != null && line_2.size() > 0) {
            LineDataSet dataSet_3 = new LineDataSet(line_3, "电网功率");
            ChartUtil.lineSet(getContext(), dataSet_3, 0xFF805EFF, LineDataSet.Mode.CUBIC_BEZIER, 0);
            dataSets.add(dataSet_3);
        }

        LineData data = new LineData(dataSets);
        chart2.setData(data);

    }

    private void showChart1() {
        if (dataMsg == null) {
            chart1.clear();
            chart1.getAxisLeft().removeAllLimitLines();
            chart1.refreshDrawableState();
            return;
        }
        List<Entry> line;
        float limit;
        switch (dataType) {
            case 1:
                line = dataMsg.currentList;
                limit = (float) dataMsg.current_warm;
                break;
            case 2:
                line = dataMsg.voltage1List;
                limit = (float) dataMsg.voltage1_warm;
                break;
            case 3:
                line = dataMsg.powerList;
                limit = (float) dataMsg.power_warm;
                break;
            default:
                line = dataMsg.currentList;
                limit = (float) dataMsg.current_warm;
                break;
        }

        if (line == null || line.size() <= 0) {
            chart1.clear();
            chart1.getAxisLeft().removeAllLimitLines();
            return;
        }
        chart1.setDrawGridBackground(false);
        chart1.setDrawBorders(false);
        chart1.setDragEnabled(true);
        chart1.setTouchEnabled(true);
        chart1.animateX(1500);
        chart1.getDescription().setEnabled(false);

        XAxis xAxis = chart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(0xff0070a3);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(10);
        xAxis.setAxisLineColor(getResources().getColor(R.color.colorText_01));
        xAxis.setAxisLineWidth(2);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int hour = (int) (value / 3600);
                int minute = (int) (value % 3600 / 60);
                int second = (int) (value % 60);
                StringBuilder builder = new StringBuilder();
                if (hour < 10) {
                    builder.append("0");
                }
                builder.append(String.valueOf(hour));
                builder.append(":");
                if (minute < 10) {
                    builder.append("0");
                }
                builder.append(String.valueOf(minute));
                builder.append(":");
                if (second < 10) {
                    builder.append("0");
                }
                builder.append(String.valueOf(second));
                return builder.toString();
            }
        });

        YAxis leftYAxis = chart1.getAxisLeft();
        leftYAxis.setDrawGridLines(true);
        leftYAxis.setTextColor(0xff0070a3);
        leftYAxis.setTextSize(10);
        leftYAxis.setGridColor(0xff1B363F);
        leftYAxis.setAxisLineColor(getResources().getColor(R.color.colorText_01));
        leftYAxis.removeAllLimitLines();
        LimitLine limitLine = new LimitLine(limit, "报警值");
        limitLine.setLineColor(0xFFFF5252);
        limitLine.setTextColor(Color.WHITE);
        leftYAxis.addLimitLine(limitLine);

        YAxis rightYAxis = chart1.getAxisRight();
        rightYAxis.setEnabled(false);

        Legend legend = chart1.getLegend();
        legend.setTextColor(0xffffffff);
        legend.setTextSize(12);

        LineDataSet dataSet_1 = new LineDataSet(line, "当前值");

        ChartUtil.lineSet(getContext(), dataSet_1, 0xFF00FF7C, LineDataSet.Mode.CUBIC_BEZIER, R.drawable.line_shape_green);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet_1);
        LineData data = new LineData(dataSets);
        chart1.setData(data);

        float max = dataSet_1.getYMax();
        if (limit > max) max = limit;
        leftYAxis.setAxisMaximum(max * 1.2f);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_pick:
                selectTime();
                break;
            case R.id.time_last:
                minusDate();
                break;
            case R.id.time_next:
                plusDate();
                break;
            case R.id.warn_type_1:
                jumpToWarnList(1);
                break;
            case R.id.warn_type_2:
                jumpToWarnList(2);
                break;
            case R.id.warn_type_3:
                jumpToWarnList(3);
                break;
        }
    }

    private void jumpToWarnList(int i) {
        Intent intent = new Intent(getContext(), WarnListActivity.class);
        intent.putExtra("type",i);
        startActivity(intent);
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
        if (ammeterDev != null) {
            InternetUtils.qualityData(ammeterDev.sn, time_1.substring(0, 4) + time_1.substring(5, 7) + time_1.substring(8, 10));
        }
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
        if (ammeterDev != null){
            InternetUtils.qualityData(ammeterDev.sn, time_1.substring(0, 4) + time_1.substring(5, 7) + time_1.substring(8, 10));
        }
        timeAdd.setImageResource(R.mipmap.next);
        timeAdd.setEnabled(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ade(AnalysisDataMsg msg) {
        if (msg.code.equals("0")) {
            totalFee.setText(String.format(getResources().getConfiguration().locale, "%.1f", msg.benifit_total));
            totalEle.setText(String.format(getResources().getConfiguration().locale, "%.1f", msg.ele_total));
            curEle.setText(String.format(getResources().getConfiguration().locale, "%.1f", msg.ele_curr));
            curFee.setText(String.format(getResources().getConfiguration().locale, "%.1f", msg.cost_curr));
            lastEle.setText(String.format(getResources().getConfiguration().locale, "%.1f", msg.ele_last));
            lastFee.setText(String.format(getResources().getConfiguration().locale, "%.1f", msg.cost_last));
            double d = msg.ele_curr - msg.ele_last;
            if (d > 0) compareImg.setImageResource(R.mipmap.increase);
            else compareImg.setImageResource(R.mipmap.reduce);
            compare.setText(String.format(getResources().getConfiguration().locale, "%.1f", d));
            String text = new DecimalFormat("#.0").format(msg.ele_curr * 100 / msg.ele_last) + "%";
            comparePercent.setText(text);
            compareCircle.setProgress((int) (msg.ele_curr * 100 / msg.ele_last));
        } else Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void aqwde(AmmetersMsg msg) {
        if (msg.code.equals("0")) {
            if (msg.list == null || msg.list.size() <= 0) return;
            tabs.removeAllTabs();
            mTabList = msg.list;
            ammeterDev = msg.list.get(0);
            InternetUtils.qualityData(ammeterDev.sn, time_1.substring(0, 4) + time_1.substring(5, 7) + time_1.substring(8, 10));
            for (int i = 0; i < msg.list.size(); i++) {
                TabLayout.Tab tab = tabs.newTab();
                tab.setText(msg.list.get(i).areaName);
                tabs.addTab(tab);
            }

        } else Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        ammeterDev = mTabList.get(tab.getPosition());
        InternetUtils.qualityData(ammeterDev.sn, time_1.substring(0, 4) + time_1.substring(5, 7) + time_1.substring(8, 10));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void wde(QualityDataMsg msg) {
        if (msg.code.equals("0")) {
            dataMsg = msg;
            showChart1();
        } else Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
    }

    private void selectTime() {
        TimePickerView pvTime = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
                time_2 = format.format(date);
                if (timeType == 1) {
                    timePicker.setText(time_2);
                    InternetUtils.analysisInfo(LoginMsg.uniqueId, timeType, time_2.substring(0, 4) + time_2.substring(5, 7) + time_2.substring(8, 10));
                } else if (timeType == 2) {
                    timePicker.setText(time_2.substring(0, 7));
                    InternetUtils.analysisInfo(LoginMsg.uniqueId, timeType, time_2.substring(0, 4) + time_2.substring(5, 7));
                } else {
                    timePicker.setText(time_2.substring(0, 4));
                    InternetUtils.analysisInfo(LoginMsg.uniqueId, timeType, time_2.substring(0, 4));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void wdwe(AnalysisInfoMsg msg) {
        if (msg.code.equals("0")) {
            showChart2(msg.bmsList, msg.allList, msg.gridList);

            String s1 = msg.value_grid + "";
            String s2 = msg.value_bms + "";
            if (timeType == 1) {
                s1 = s1 + "W";
                s2 = s2 + "W";
            } else {
                s1 = s1 + "kWh";
                s2 = s2 + "kWh";
            }
            fromBms.setText(s2);
            fromGrid.setText(s1);

            int temp = msg.value_bms * 100 / msg.value_all;
            s1 = "来自电池（" + temp + "%）";
            fromBmsPercent.setText(s1);
            percentProgress.setProgress(temp);
            temp = msg.value_grid * 100 / msg.value_all;
            s1 = "来自电网（" + temp + "%）";
            fromGridPercent.setText(s1);
        } else Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
    }
}
