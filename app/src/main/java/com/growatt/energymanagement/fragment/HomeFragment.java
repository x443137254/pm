package com.growatt.energymanagement.fragment;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
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
import com.growatt.energymanagement.activity.DeviceClassifyActivity;
import com.growatt.energymanagement.activity.MainActivity;
import com.growatt.energymanagement.activity.NoticeActivity;
import com.growatt.energymanagement.activity.WarnListActivity;
import com.growatt.energymanagement.msgs.EnergyTendencyMsg;
import com.growatt.energymanagement.msgs.GreenBenifitMsg;
import com.growatt.energymanagement.msgs.HomeMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.NoticeListMsg;
import com.growatt.energymanagement.msgs.NotificationMsg;
import com.growatt.energymanagement.msgs.PopMsg;
import com.growatt.energymanagement.msgs.WeatherMsg;
import com.growatt.energymanagement.utils.ChartUtil;
import com.growatt.energymanagement.utils.InternetUtils;
import com.growatt.energymanagement.view.CircleProgressBar;
import com.growatt.energymanagement.view.GradientTextView;

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
 * 首页-总览
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    private TextView tv_trend;
    private LineChart mChart;
    private GradientTextView shapeText;
    private TextView location;
    private TextView windText;
    private TextView weatherText;
    private View newsPoint;

    private TextView ele_in;
    private TextView ele_out;
    private TextView ele_cost;
    private TextView ele_earnings;
    private TextView photovoltaicRunningPower;
    private TextView photovoltaicTheroyPower;
    private TextView devicesPower;
    private TextView gridPower;
    private TextView running_percent;
    private TextView power_percent;
    private TextView inverter;
    private TextView aircondition;
    private TextView socket;
    private TextView ameter;
    private TextView TCE;
    private TextView plants;
    private TextView CO2;
    private TextView lightPower;
    private TextView loadPower;
    private TextView eleNetPower;
    private CircleProgressBar c1;
    private CircleProgressBar c2;
    private CircleProgressBar c3;

    private int timeType = 3;
    private String time = "";

    private View rootView;
    private ImageView circle1;
    private ImageView circle2;
    private ImageView floatLeft;
    private ImageView floatRight;
    private TextView warnNum;

    private List<NoticeListMsg.NoticeListBean> noticelist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (MainActivity.isPad) {
            Resources resources = getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            if (dm.widthPixels > dm.heightPixels) {
                return inflater.inflate(R.layout.fragment_home_pad_h, container, false);
            } else return inflater.inflate(R.layout.fragment_home_pad_v, container, false);
        } else return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        getData();
    }

    private void getData() {
        InternetUtils.notice(LoginMsg.uniqueId,1,"warning","");
        InternetUtils.greenBenifit(LoginMsg.uniqueId);
        InternetUtils.home(LoginMsg.uniqueId);
        InternetUtils.noticeList(LoginMsg.uniqueId,0);
        if (MainActivity.isPad) return;
        if (LoginMsg.cid != 0 && LoginMsg.hasMsg) {
            newsPoint.setVisibility(View.VISIBLE);
        } else {
            newsPoint.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        c1 = view.findViewById(R.id.pb_home);
        c2 = view.findViewById(R.id.pb_home2);
        c3 = view.findViewById(R.id.load_power_circle);
        if (!MainActivity.isPad) view.findViewById(R.id.iv_person).setOnClickListener(this);
        view.findViewById(R.id.iv_notice).setOnClickListener(this);
        if (!MainActivity.isPad) view.findViewById(R.id.warn_bar).setOnClickListener(this);
        tv_trend = view.findViewById(R.id.trend_time);
        tv_trend.setOnClickListener(this);
        mChart = view.findViewById(R.id.cc_home);
        location = view.findViewById(R.id.location);
        windText = view.findViewById(R.id.wind_text);
        weatherText = view.findViewById(R.id.weather_text);
        shapeText = view.findViewById(R.id.shape_text);
        shapeText.setShapeColors(0xff08ffce, 0xff00c0ff);
        newsPoint = view.findViewById(R.id.tip_notice_point);
        ele_in = view.findViewById(R.id.ele_in);
        ele_out = view.findViewById(R.id.ele_out);
        ele_cost = view.findViewById(R.id.ele_cost);
        ele_earnings = view.findViewById(R.id.ele_earnings);
        photovoltaicRunningPower = view.findViewById(R.id.photovoltaicRunningPower);
        photovoltaicTheroyPower = view.findViewById(R.id.photovoltaicTheroyPower);
        devicesPower = view.findViewById(R.id.devicesPower);
        gridPower = view.findViewById(R.id.gridPower);
        running_percent = view.findViewById(R.id.running_percent);
        power_percent = view.findViewById(R.id.power_percent);
        inverter = view.findViewById(R.id.inverter);
        aircondition = view.findViewById(R.id.aircondition);
        socket = view.findViewById(R.id.socket);
        ameter = view.findViewById(R.id.ameter);
        TCE = view.findViewById(R.id.TCE);
        plants = view.findViewById(R.id.plants);
        CO2 = view.findViewById(R.id.CO2);
        lightPower = view.findViewById(R.id.light_power);
        loadPower = view.findViewById(R.id.load_power);
        eleNetPower = view.findViewById(R.id.ele_net_power);
        warnNum = view.findViewById(R.id.warn_num);
        RadioGroup timeTypes = view.findViewById(R.id.time_type_group);
        timeTypes.check(R.id.time_type_month);
        showChart(R.id.time_type_month);
        timeTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                showChart(checkedId);
            }
        });

        setAnimation();

        view.findViewById(R.id.inverter_item).setOnClickListener(this);
        view.findViewById(R.id.ammeter_item).setOnClickListener(this);
        view.findViewById(R.id.airCondition_item).setOnClickListener(this);
        view.findViewById(R.id.socket_item).setOnClickListener(this);
    }

    private void showChart(int id) {
        if (time.equals("")) {
            time = new SimpleDateFormat("yyyy-MM-dd",getResources().getConfiguration().locale).format(new Date());
        }
        switch (id) {
            case R.id.time_type_day:
                timeType = 2;
                if (tv_trend.getVisibility() == View.GONE) {
                    tv_trend.setVisibility(View.VISIBLE);
                }
                tv_trend.setText(time.substring(0,7));
                InternetUtils.energyTendency(LoginMsg.uniqueId, timeType, time.substring(0, 4) + time.substring(5, 7));
                break;
            case R.id.time_type_month:
                timeType = 3;
                if (tv_trend.getVisibility() == View.GONE) {
                    tv_trend.setVisibility(View.VISIBLE);
                }
                tv_trend.setText(time.substring(0,4));
                InternetUtils.energyTendency(LoginMsg.uniqueId, timeType, time.substring(0, 4));
                break;
            case R.id.time_type_year:
                timeType = 4;
                tv_trend.setVisibility(View.GONE);
                InternetUtils.energyTendency(LoginMsg.uniqueId, timeType, time.substring(0, 4));
                break;
        }
    }

    private void setAnimation() {
        Animation animation1 = AnimationUtils.loadAnimation(getContext(), R.anim.anim_01);
        Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.anim_01);
        rootView.findViewById(R.id.big_circle).setAnimation(animation1);
        circle1 = rootView.findViewById(R.id.circle_01);
        circle1.setAnimation(animation2);
        circle2 = rootView.findViewById(R.id.circle_02);
        circle2.setAnimation(animation2);
        floatLeft = rootView.findViewById(R.id.float_left_1);
        floatRight = rootView.findViewById(R.id.float_right_1);
        floatLeft.setImageResource(R.drawable.anim_01);
        floatRight.setImageResource(R.drawable.anim_02);
        AnimationDrawable animation3 = (AnimationDrawable) floatLeft.getDrawable();
        AnimationDrawable animation4 = (AnimationDrawable) floatRight.getDrawable();
        LinearInterpolator lir = new LinearInterpolator();
        animation1.setInterpolator(lir);
        animation2.setInterpolator(lir);
        animation1.start();
        animation2.start();
        animation3.start();
        animation4.start();

    }

    private void showChart(
            List<Entry> photovoltaicList,
            List<Entry> chargerList,
            List<Entry> gridList,
            List<Entry> costList) {

        mChart.setDrawGridBackground(false);
        mChart.setDrawBorders(false);
        mChart.setDragEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.animateX(800);
        mChart.getDescription().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
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
        leftYAxis.setAxisLineColor(getResources().getColor(R.color.colorText_01));

        YAxis rightYAxis = mChart.getAxisRight();
        rightYAxis.setEnabled(false);

        Legend legend = mChart.getLegend();
        legend.setTextColor(0xffffffff);
        legend.setTextSize(12);

        List<ILineDataSet> dataSets = new ArrayList<>();
        if (photovoltaicList != null && photovoltaicList.size() > 0) {
            LineDataSet dataSet_1 = new LineDataSet(photovoltaicList, "光伏产出");
            ChartUtil.lineSet(getContext(), dataSet_1, 0xFF00FF7C, LineDataSet.Mode.LINEAR, 0);
            dataSets.add(dataSet_1);
        }
        if (costList != null && costList.size() > 0) {
            LineDataSet dataSet_2 = new LineDataSet(costList, "用户消耗");
            ChartUtil.lineSet(getContext(), dataSet_2, 0xFFFBD216, LineDataSet.Mode.LINEAR, 0);
            dataSets.add(dataSet_2);
        }
        if (gridList != null && gridList.size() > 0) {
            LineDataSet dataSet_3 = new LineDataSet(gridList, "电网取电");
            ChartUtil.lineSet(getContext(), dataSet_3, 0xFFFE6539, LineDataSet.Mode.LINEAR, 0);
            dataSets.add(dataSet_3);
        }
        if (chargerList != null && chargerList.size() > 0) {
            LineDataSet dataSet_4 = new LineDataSet(chargerList, "来自电池");
            ChartUtil.lineSet(getContext(), dataSet_4, 0xFFAB8CFF, LineDataSet.Mode.LINEAR, 0);
            dataSets.add(dataSet_4);
        }

        LineData data = new LineData(dataSets);
        mChart.setData(data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_person:
                EventBus.getDefault().post(new PopMsg());
                break;
            case R.id.iv_notice:
                if (MainActivity.isPad) {
                    MsgFragment fragment = new MsgFragment();
                    fragment.setBackData(HomeFragment.this);
                    FragmentManager manager = getFragmentManager();
                    if (manager != null) {
                        FragmentTransaction transaction = manager.beginTransaction();
                        Bundle bundle = new Bundle();
                        if (noticelist != null && noticelist.size() > 0){
                            NoticeListMsg.NoticeListBean bean = noticelist.get(0);
                            bundle.putString("devType", bean.type);
                            bundle.putLong("time", bean.time);
                            long t = bean.time;
                            for (int i = 1; i < noticelist.size(); i++) {
                                bean = noticelist.get(i);
                                if (t < bean.time) {
                                    t = bean.time;
                                    bundle.putString("devType", bean.type);
                                    bundle.putLong("time", bean.time);
                                }
                            }
                        }
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.fl, fragment).commit();
                    }
                } else {
                    Intent intent = new Intent(getActivity(), NoticeActivity.class);
                    if (noticelist != null && noticelist.size() > 0){
                        NoticeListMsg.NoticeListBean bean = noticelist.get(0);
                        intent.putExtra("devType", bean.type);
                        intent.putExtra("time", bean.time);
                        long t = bean.time;
                        for (int i = 1; i < noticelist.size(); i++) {
                            bean = noticelist.get(i);
                            if (t < bean.time) {
                                t = bean.time;
                                intent.putExtra("devType", bean.type);
                                intent.putExtra("time", bean.time);
                            }
                        }
                    }
                    startActivity(intent);
                    if (newsPoint.getVisibility() == View.VISIBLE) {
                        LoginMsg.hasMsg = false;
                        newsPoint.setVisibility(View.INVISIBLE);
                    }
                }
                break;
            case R.id.trend_time:
                TimePickerView pvTime = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",getResources().getConfiguration().locale);
                        time = format.format(date);
                        if (timeType == 2) {
                            tv_trend.setText(time);
                            InternetUtils.energyTendency(LoginMsg.uniqueId, timeType, time.substring(0, 4) + time.substring(5, 7));
                        } else {
                            tv_trend.setText(time.substring(0, 7));
                            InternetUtils.energyTendency(LoginMsg.uniqueId, timeType, time.substring(0, 4));
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
                break;
            case R.id.warn_bar:
                startActivity(new Intent(getContext(), WarnListActivity.class));
                break;
            case R.id.inverter_item:
                MainActivity activity = (MainActivity) getActivity();
                if (activity == null) break;
                activity.jumpToEle();
                break;
            case R.id.ammeter_item:
                Intent intent2 = new Intent(getContext(), DeviceClassifyActivity.class);
                intent2.putExtra("devType","ammeter");
                startActivity(intent2);
                break;
            case R.id.airCondition_item:
                Intent intent3 = new Intent(getContext(), DeviceClassifyActivity.class);
                intent3.putExtra("devType","airCondition");
                startActivity(intent3);
                break;
            case R.id.socket_item:
                Intent intent4 = new Intent(getContext(), DeviceClassifyActivity.class);
                intent4.putExtra("devType","socket");
                startActivity(intent4);
                break;
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void showWarnNum(NotificationMsg msg) {
//        if (msg.warnList != null){
//            String s = String.valueOf(msg.warnList.size());
//            warnNum.setText(s);
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showWarnNum(NoticeListMsg msg) {
        if (msg.code.equals("0")){
            if (msg.list == null)  warnNum.setText("0");
            else {
                warnNum.setText(String.valueOf(msg.list.size()));
                noticelist = msg.list;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showChart(EnergyTendencyMsg msg) {
        if (msg.code.equals("0")) {
            showChart(msg.photovoltaicList, msg.chargerList, msg.gridList, msg.costList);
        } else {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void showWeather(WeatherMsg msg) {
        if (!msg.city.equals("") && !MainActivity.isPad) {
            location.setText(msg.city);
            String s = "风速：" + msg.winddirection + msg.windpower + "级";
            windText.setText(s);
            s = msg.temperature + "°" + msg.weather;
            weatherText.setText(s);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showGreenBenifit(GreenBenifitMsg msg) {
        if (msg.code.equals("0")) {
            TCE.setText(msg.tce);
            plants.setText(msg.plants);
            CO2.setText(msg.co2);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showHomeData(HomeMsg msg) {
        if (msg.code.equals("1")){
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
            return;
        }
        shapeText.setText(String.format(getResources().getConfiguration().locale,"%.1f",msg.ele_out_total));
        ele_in.setText(String.valueOf((int) msg.ele_in));
        ele_out.setText(String.valueOf(msg.ele_out));
        ele_cost.setText(String.valueOf(msg.ele_cost));
        ele_earnings.setText(String.valueOf(msg.ele_earnings));
        photovoltaicRunningPower.setText(String.valueOf(msg.photovoltaicRuntimePower));
        photovoltaicTheroyPower.setText(String.valueOf(msg.photovoltaicTheroyPower));
        devicesPower.setText(String.valueOf(msg.devicesPower));
        gridPower.setText(String.valueOf(msg.photovoltaicRuntimePower));
        inverter.setText(String.valueOf(msg.inverter));
        aircondition.setText(String.valueOf(msg.aircondition));
        socket.setText(String.valueOf(msg.socket));
        ameter.setText(String.valueOf(msg.ameter));
        loadPower.setText(String.valueOf(msg.devicesPower));
        lightPower.setText(String.valueOf(msg.photovoltaicRuntimePower));
        eleNetPower.setText(String.valueOf(msg.gridPower));
        int p = (int) (msg.photovoltaicRuntimePower * 100 / msg.photovoltaicTheroyPower);
        String s = p + "%";
        c1.setProgress(p);
        running_percent.setText(s);
        p = (int) (msg.photovoltaicRuntimePower * 100 / msg.devicesPower);
        s = p + "%";
        c2.setProgress(p);
        power_percent.setText(s);
        p = (int) (msg.gridPower * 100 / msg.devicesPower);
        c3.setProgress(p);

        if (msg.photovoltaicRuntimePower <= 0) {
            circle1.clearAnimation();
            floatLeft.setImageResource(R.mipmap.rate_float_00);
        }
        if (msg.gridPower <= 0) {
            circle2.clearAnimation();
            floatRight.setImageResource(R.mipmap.float_power_00);
        }
    }

}
