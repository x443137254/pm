package com.growatt.energymanagement.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.growatt.energymanagement.activity.MainActivity;
import com.growatt.energymanagement.R;
import com.growatt.energymanagement.activity.AddDeviceActivity;
import com.growatt.energymanagement.activity.DeviceClassifyActivity;
import com.growatt.energymanagement.activity.EnergyConsumeDetailActivity;
import com.growatt.energymanagement.msgs.AreaDevsStateMsg;
import com.growatt.energymanagement.msgs.AreaEleRankMsg;
import com.growatt.energymanagement.msgs.DevRunningStateMsg;
import com.growatt.energymanagement.msgs.DevTypeEleCostMsg;
import com.growatt.energymanagement.msgs.EleCostMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.utils.ChartUtil;
import com.growatt.energymanagement.utils.InternetUtils;
import com.growatt.energymanagement.view.CircleProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 能耗
 */
public class EnergyFragment extends Fragment implements View.OnClickListener {

    private LinearLayout mZoneConsumeList;
    private LinearLayout mConsumeOrderList;
    private TextView timePick01;
    private TextView timePick02;
    private TextView timePick03;
    private LineChart chart_1;

    private int timeType_1 = 1;
    private int timeType_2 = 3;
    private int timeType_3 = 3;
    private String time_1 = "";
    private String time_2 = "";
    private String time_3 = "";
    private LinearLayout listContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (MainActivity.isPad) {
            Resources resources = getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            if (dm.widthPixels > dm.heightPixels) {
                return inflater.inflate(R.layout.fragment_energy_pad_h, container, false);
            } else return inflater.inflate(R.layout.fragment_energy_pad_v, container, false);
        } else return inflater.inflate(R.layout.fragment_energy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioGroup mCutRadioGroup = view.findViewById(R.id.cut_group);
        RadioGroup mTimeGroup1 = view.findViewById(R.id.ele_trend_radio_group);
        RadioGroup mTimeGroup2 = view.findViewById(R.id.time_group);
        RadioGroup mTimeGroup3 = view.findViewById(R.id.time_group3);
        mZoneConsumeList = view.findViewById(R.id.zone_consume_list);
        mConsumeOrderList = view.findViewById(R.id.consume_order_contain);
        chart_1 = view.findViewById(R.id.line_chart_1);
        timePick01 = view.findViewById(R.id.time_picker_01);
        timePick02 = view.findViewById(R.id.time_picker_02);
        timePick03 = view.findViewById(R.id.time_picker_03);

        mCutRadioGroup.check(R.id.radio_device);
        InternetUtils.devRunningState(LoginMsg.uniqueId);
        mTimeGroup1.check(R.id.ele_trend_radio_now);
        mTimeGroup2.check(R.id.time_month);
        mTimeGroup3.check(R.id.time3_month);
        String format = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale).format(new Date());
        if (time_1.equals("")) {
            time_1 = format;
        }
        if (time_2.equals("")) {
            time_2 = format;
        }
        if (time_3.equals("")) {
            time_3 = format;
        }
        timePick01.setText(time_1);
        setTextEnable(false);
        timePick02.setText(time_2.substring(0, 7));
        timePick03.setText(time_3.substring(0, 7));
        InternetUtils.eleCost(LoginMsg.uniqueId, timeType_1, time_1.substring(0, 4) + time_1.substring(5, 7) + time_1.substring(8, 10));
        InternetUtils.areaEleRank(LoginMsg.uniqueId, timeType_2, time_2.substring(0, 4) + time_2.substring(5, 7));
        InternetUtils.devTypeEleCost(LoginMsg.uniqueId, timeType_3, time_3.substring(0, 4) + time_3.substring(5, 7));

        mTimeGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.ele_trend_radio_now:
                        timeType_1 = 1;
                        timePick01.setText(time_1);
                        setTextEnable(false);
                        InternetUtils.eleCost(LoginMsg.uniqueId, timeType_1, time_1.substring(0, 4) + time_1.substring(5, 7) + time_1.substring(8, 10));
                        break;
                    case R.id.ele_trend_radio_day:
                        timeType_1 = 2;
                        timePick01.setText(time_1);
                        setTextEnable(true);
                        InternetUtils.eleCost(LoginMsg.uniqueId, timeType_1, time_1.substring(0, 4) + time_1.substring(5, 7) + time_1.substring(8, 10));
                        break;
                    case R.id.ele_trend_radio_mon:
                        timeType_1 = 3;
                        timePick01.setText(time_1.substring(0, 7));
                        setTextEnable(true);
                        InternetUtils.eleCost(LoginMsg.uniqueId, timeType_1, time_1.substring(0, 4) + time_1.substring(5, 7));
                        break;
                    case R.id.ele_trend_radio_year:
                        timeType_1 = 4;
                        timePick01.setText(time_1.substring(0, 4));
                        setTextEnable(true);
                        InternetUtils.eleCost(LoginMsg.uniqueId, timeType_1, time_1.substring(0, 4));
                        break;
                }
            }
        });

        mTimeGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.time_day:
                        timeType_2 = 2;
                        timePick02.setText(time_2);
                        InternetUtils.areaEleRank(LoginMsg.uniqueId, timeType_2, time_2.substring(0, 4) + time_2.substring(5, 7) + time_2.substring(8, 10));
                        break;
                    case R.id.time_month:
                        timeType_2 = 3;
                        timePick02.setText(time_2.substring(0, 7));
                        InternetUtils.areaEleRank(LoginMsg.uniqueId, timeType_2, time_2.substring(0, 4) + time_2.substring(5, 7));
                        break;
                    case R.id.time_year:
                        timeType_2 = 4;
                        timePick02.setText(time_2.substring(0, 4));
                        InternetUtils.areaEleRank(LoginMsg.uniqueId, timeType_2, time_2.substring(0, 4));
                        break;
                }
            }
        });

        mTimeGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.time3_day:
                        timeType_3 = 2;
                        timePick03.setText(time_3);
                        InternetUtils.devTypeEleCost(LoginMsg.uniqueId, timeType_3, time_3.substring(0, 4) + time_3.substring(5, 7) + time_3.substring(8, 10));
                        break;
                    case R.id.time3_month:
                        timeType_3 = 3;
                        timePick03.setText(time_3.substring(0, 7));
                        InternetUtils.devTypeEleCost(LoginMsg.uniqueId, timeType_3, time_3.substring(0, 4) + time_3.substring(5, 7));
                        break;
                    case R.id.time3_year:
                        timeType_3 = 4;
                        timePick03.setText(time_3.substring(0, 4));
                        InternetUtils.devTypeEleCost(LoginMsg.uniqueId, timeType_3, time_3.substring(0, 4));
                        break;
                }
            }
        });

        mCutRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_device) {
                    InternetUtils.devRunningState(LoginMsg.uniqueId);
                } else {
                    InternetUtils.areaDevsState(LoginMsg.uniqueId);
                }
            }
        });
        timePick01.setOnClickListener(this);
        timePick02.setOnClickListener(this);
        timePick03.setOnClickListener(this);

        listContainer = view.findViewById(R.id.list_container);
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

    private void showChart1(List<Entry> line) {
        if (line == null || line.size() <= 0) {
            chart_1.clear();
            return;
        }
        chart_1.setDrawGridBackground(false);
        chart_1.setDrawBorders(false);
        chart_1.setDragEnabled(false);
        chart_1.setTouchEnabled(false);
        chart_1.animateX(1000);
        chart_1.getDescription().setEnabled(false);

        XAxis xAxis = chart_1.getXAxis();
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
                switch (timeType_1) {
                    case 1:
                        return (int) value + "时";
                    case 2:
                        return (int) value + "日";
                    case 3:
                        return (int) value + "月";
                    case 4:
                        return (int) value + "月";
                }
                return null;
            }
        });

        YAxis leftYAxis = chart_1.getAxisLeft();
        leftYAxis.setDrawGridLines(true);
        leftYAxis.setTextColor(0xff0070a3);
        leftYAxis.setTextSize(10);
        leftYAxis.setLabelCount(4);
        leftYAxis.setGridColor(0xff1B363F);
        leftYAxis.setAxisLineColor(getResources().getColor(R.color.colorText_01));

        YAxis rightYAxis = chart_1.getAxisRight();
        rightYAxis.setEnabled(false);

        Legend legend = chart_1.getLegend();
        legend.setTextColor(0xffffffff);
        legend.setTextSize(12);
        legend.setEnabled(false);

        LineDataSet dataSet_1 = new LineDataSet(line, "功率");

        ChartUtil.lineSet(getContext(), dataSet_1, 0xFF00FF72, LineDataSet.Mode.CUBIC_BEZIER, R.drawable.line_shape_green);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet_1);
        LineData data = new LineData(dataSets);
        chart_1.setData(data);
    }

    private void addDeviceRunningItem(final String s, int sum, int run, int ele) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_cate_manage, listContainer, false);
        TextView name = view.findViewById(R.id.device_name);
        TextView total = view.findViewById(R.id.total_num);
        TextView running = view.findViewById(R.id.running_num);
        TextView eleCost = view.findViewById(R.id.ele_cost);
        CircleProgressBar runningPercent = view.findViewById(R.id.running_percent);
        name.setText(s);
        total.setText(String.valueOf(sum));
        running.setText(String.valueOf(run));
        eleCost.setText(String.valueOf(ele));
        runningPercent.setProgress(run * 100 / sum);
        if (ele > 0) {
            ImageView imageView = view.findViewById(R.id.anim_01);
            imageView.setImageResource(R.drawable.anim_02);
            AnimationDrawable animation = (AnimationDrawable) imageView.getDrawable();
            animation.start();
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DeviceClassifyActivity.class);
                switch (s) {
                    case "空调":
                        intent.putExtra("devType", "airCondition");
                        break;
                    case "插座":
                        intent.putExtra("devType", "socket");
                        break;
                    case "充电桩":
                        intent.putExtra("devType", "chargePile");
                        break;
                    case "温控器":
                        intent.putExtra("devType", "thermostat");
                        break;
                    case "shineBoost":
                        intent.putExtra("devType", "shineBoost");
                        break;
                }
                startActivity(intent);
            }
        });
        listContainer.addView(view);
    }

    private void addConsumeOrderItem(int r, String t, int n, int total) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_consume_order, mConsumeOrderList, false);
        TextView progress = view.findViewById(R.id.consume_order_list_item);
        TextView title = view.findViewById(R.id.consume_order_list_title);
        TextView num = view.findViewById(R.id.consume_order_list_data);
        TextView rank = view.findViewById(R.id.consume_order);
        if (r == 1) {
            rank.setBackgroundResource(R.drawable.order_bg_1);
            rank.setText(String.valueOf(r));
        } else if (r == 2) {
            rank.setBackgroundResource(R.drawable.order_bg_2);
            rank.setText(String.valueOf(r));
        } else if (r == 3) {
            rank.setBackgroundResource(R.drawable.order_bg_3);
            rank.setText(String.valueOf(r));
        }
        title.setText(t);
        String s1 = n + "kWh";
        num.setText(s1);
        int widthPixels;
        if (MainActivity.isPad && getResources().getDisplayMetrics().widthPixels > getResources().getDisplayMetrics().heightPixels) {
            widthPixels = getActivity().getResources().getDisplayMetrics().widthPixels / 3;
        } else widthPixels = getActivity().getResources().getDisplayMetrics().widthPixels;
        int width = (int) (widthPixels * n * 0.45f / total);
        progress.setWidth(width);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (MainActivity.isPad) {
//                    View dialog = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_energy_consume_detail, null);
//                    ListView mDetailList = dialog.findViewById(R.id.energy_consume_detail_list);
//                    mDetailList.setAdapter(new EnergyDetailListAdapter(getContext()));
//                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(dialog).create();
//                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
//                    alertDialog.show();
//                } else {
//                    Intent intent = new Intent(getActivity(), EnergyConsumeDetailActivity.class);
//                    intent.putExtra("path",path);
//                    startActivity(intent);
//                }
//            }
//        });
        mConsumeOrderList.addView(view);
    }

    private void addZoneConsumeItem(String s, double total, double data, final String path) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_zone_consume, mZoneConsumeList, false);
        TextView progress = view.findViewById(R.id.zone_consume_list_item);
        TextView title = view.findViewById(R.id.zone_consume_list_title);
        TextView num = view.findViewById(R.id.zone_consume_list_data);
        title.setText(s);
        String s1 = (int) data + "kWh";
        num.setText(s1);
        int width = (int) (getActivity().getWindowManager().getDefaultDisplay().getWidth() * 0.6f * data / total);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EnergyConsumeDetailActivity.class);
                intent.putExtra("path",path);
                startActivity(intent);
            }
        });
        mZoneConsumeList.addView(view);
        progress.setWidth(width);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.air_conditioner:
                if (MainActivity.isPad) {
                    DeviceManageFragment deviceManageFragment = new DeviceManageFragment();
                    deviceManageFragment.setBack(EnergyFragment.this);
                    getFragmentManager().beginTransaction().replace(R.id.fl, deviceManageFragment).commit();
                } else startActivity(new Intent(getActivity(), DeviceClassifyActivity.class));
                break;
            case R.id.illumination:
                break;
            case R.id.charge_station:
                break;
            case R.id.add_device:
                if (MainActivity.isPad) {
                    Context context = getContext();
                    View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_device, null);
                    AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                    dialog.show();
                } else startActivity(new Intent(getActivity(), AddDeviceActivity.class));
                break;
            case R.id.time_picker_01:
                pickTime(timePick01);
                break;
            case R.id.time_picker_02:
                pickTime(timePick02);
                break;
            case R.id.time_picker_03:
                pickTime(timePick03);
                break;
        }
    }

    private void pickTime(final TextView textView) {
        TimePickerView pvTime = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
                if (textView == timePick01) {
                    time_1 = format.format(date);
                    if (timeType_1 == 2) {
                        textView.setText(time_1);
                        InternetUtils.eleCost(LoginMsg.uniqueId, timeType_1, time_1.substring(0, 4) + time_1.substring(5, 7) + time_1.substring(8, 10));
                    } else if (timeType_1 == 3) {
                        textView.setText(time_1.substring(0, 7));
                        InternetUtils.eleCost(LoginMsg.uniqueId, timeType_1, time_1.substring(0, 4) + time_1.substring(5, 7));
                    } else {
                        textView.setText(time_1.substring(0, 4));
                        InternetUtils.eleCost(LoginMsg.uniqueId, timeType_1, time_1.substring(0, 4));
                    }
                } else if (textView == timePick02) {
                    time_2 = format.format(date);
                    if (timeType_2 == 2) {
                        textView.setText(time_2);
                        InternetUtils.areaEleRank(LoginMsg.uniqueId, timeType_2, time_2.substring(0, 4) + time_2.substring(5, 7) + time_2.substring(8, 10));
                    } else if (timeType_2 == 3) {
                        textView.setText(time_2.substring(0, 7));
                        InternetUtils.areaEleRank(LoginMsg.uniqueId, timeType_2, time_2.substring(0, 4) + time_2.substring(5, 7));
                    } else {
                        textView.setText(time_2.substring(0, 4));
                        InternetUtils.areaEleRank(LoginMsg.uniqueId, timeType_2, time_2.substring(0, 4));
                    }
                } else {
                    time_3 = format.format(date);
                    if (timeType_3 == 2) {
                        textView.setText(time_3);
                        InternetUtils.devTypeEleCost(LoginMsg.uniqueId, timeType_3, time_3.substring(0, 4) + time_3.substring(5, 7) + time_3.substring(8, 10));
                    } else if (timeType_3 == 3) {
                        textView.setText(time_3.substring(0, 7));
                        InternetUtils.devTypeEleCost(LoginMsg.uniqueId, timeType_3, time_3.substring(0, 4) + time_3.substring(5, 7));
                    } else {
                        textView.setText(time_3.substring(0, 4));
                        InternetUtils.devTypeEleCost(LoginMsg.uniqueId, timeType_3, time_3.substring(0, 4));
                    }
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
    public void sdad(AreaEleRankMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            if (msg.list == null) return;
            mZoneConsumeList.removeAllViews();
            AreaEleRankMsg.AreaEleRankBean bean;
            double eleTotal = 0;
            for (int i = 0; i < msg.list.size(); i++) {
                bean = msg.list.get(i);
                eleTotal += bean.ele;
            }
            addZoneConsumeItem("全部", eleTotal, eleTotal,msg.list.get(0).areaPath);
            for (int i = 0; i < msg.list.size(); i++) {
                bean = msg.list.get(i);
                addZoneConsumeItem(bean.areaName, eleTotal, bean.ele,bean.areaPath);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sqdad(DevTypeEleCostMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            if (msg.list == null || msg.list.size() <= 0) return;
            mConsumeOrderList.removeAllViews();
            Map<String, Integer> map = msg.list.get(0);
            String key = map.keySet().iterator().next();
            int eleTotal = map.get(key);
            for (int i = 0; i < msg.list.size(); i++) {
                map = msg.list.get(i);
                key = map.keySet().iterator().next();
                addConsumeOrderItem(i + 1, switchName(key), map.get(key), eleTotal);
            }
        }
    }

    private String switchName(String devName) {
        switch (devName) {
            case "airCondition":
                return "空调";
            case "socket":
                return "插座";
            case "chargePile":
                return "充电桩";
            case "thermostat":
                return "温控器";
            case "shineBoost":
                return "shineBoost";
        }
        return "";
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sdar(DevRunningStateMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            listContainer.removeAllViews();
            if (msg.airCondition != null) {
                addDeviceRunningItem("空调", msg.airCondition.num_all, msg.airCondition.num_runing, msg.airCondition.ele_cost);
            }
            if (msg.chargePile != null) {
                addDeviceRunningItem("充电桩", msg.chargePile.num_all, msg.chargePile.num_runing, msg.chargePile.ele_cost);
            }
            if (msg.socket != null) {
                addDeviceRunningItem("插座", msg.socket.num_all, msg.socket.num_runing, msg.socket.ele_cost);
            }
            if (msg.shineBoost != null) {
                addDeviceRunningItem("shineBoost", msg.shineBoost.num_all, msg.shineBoost.num_runing, msg.shineBoost.ele_cost);
            }
            if (msg.thermostat != null) {
                addDeviceRunningItem("温控器", msg.thermostat.num_all, msg.thermostat.num_runing, msg.thermostat.ele_cost);
            }
        }
    }

    private void setTextEnable(boolean enable) {
        timePick01.setEnabled(enable);
        if (enable) timePick01.setAlpha(1.0f);
        else timePick01.setAlpha(0.3f);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sda5r(AreaDevsStateMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            listContainer.removeAllViews();
            if (msg.list == null) return;
            AreaDevsStateMsg.ConsumeItem item;
            for (int i = 0; i < msg.list.size(); i++) {
                item = msg.list.get(i);
                addDeviceRunningItem(item.areaName, item.num_all, item.num_running, item.ele_cost);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sqdvvad(EleCostMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            showChart1(msg.list);
        }
    }
}
