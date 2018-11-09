package com.growatt.energymanagement.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.growatt.energymanagement.activity.DeviceDetailActivity;
import com.growatt.energymanagement.msgs.AddCollectorMsg;
import com.growatt.energymanagement.msgs.AreaInfoMsg;
import com.growatt.energymanagement.msgs.GenerateElectricitysMsg;
import com.growatt.energymanagement.msgs.InvertersMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.OutputAndInputOfEleMsg;
import com.growatt.energymanagement.msgs.StatisticsDataMsg;
import com.growatt.energymanagement.msgs.StorageSystemDataMsg;
import com.growatt.energymanagement.utils.ChartUtil;
import com.growatt.energymanagement.utils.CommentUtils;
import com.growatt.energymanagement.utils.InternetUtils;
import com.growatt.energymanagement.view.CircleProgressBar;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 发电
 */
public class EleFragment extends Fragment implements View.OnClickListener {

    private View mRootView;
    private LinearLayout deviceList;
    private LineChart mChart;
    private PopupWindow addDevicePop;
    private EditText deviceSn;
    private EditText authCode;
    private List<AreaInfoMsg.AreaInfo> areaInfoList;
    private List<InvertersMsg.DevBean> devList;
    private TextView areaSelector1;
    private TextView areaSelector2;

    private final int MENU_1 = 1;
    private final int MENU_2 = 2;
    private TextView title;
    private LinearLayout container;
    private LinearLayout container_pad_1;
    private RelativeLayout container_pad_2;

    private View dynamicView_phone;
    private View dynamicView_pad_1;
    private View dynamicView_pad_2;
    private String sysType;
    private final String INVERTER = "INVERTER"; //光伏系统
    private final String BIG_HPS = "BIG_HPS";   //光储系统
    private final String BATTERY = "BATTERY";   //储能系统

    private int timeType = 1;
    private String time = "";
    private TextView timeText;
    private TextView power_grid;
    private TextView power_cost;
    private TextView power_pv;
    private TextView pv_in;
    private TextView pv_out;
    private TextView status;
    private TextView pv_in_percent;
    private TextView pv_out_percent;
    private TextView pv_total;
    private TextView power_cost_percent;
    private CircleProgressBar c1;
    private ImageView imageView6;
    private ImageView imageView7;
    private ImageView imageView8;
    private ImageView imageView1;
    private ImageView imageView2;
    private RadioGroup timeGroup;
    private TextView power_theory;
    private TextView ele_total;
    private TextView benifit_total;
    private TextView ele_cost;
    private TextView cost_pv;
    private TextView cost_grid;
    private TextView cost_pv_percent;
    private TextView cost_grid_percent;
    private CircleProgressBar c2;
    private ImageView imageView3;
    private ImageView imageView4;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (MainActivity.isPad && dm.widthPixels > dm.heightPixels) {
            mRootView = inflater.inflate(R.layout.fragment_ele_pad_h, container, false);
        } else mRootView = inflater.inflate(R.layout.fragment_ele, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.add_device).setOnClickListener(this);
        title = view.findViewById(R.id.title);
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (MainActivity.isPad && dm.widthPixels > dm.heightPixels) {
            container_pad_1 = view.findViewById(R.id.pad_container_left);
            container_pad_2 = view.findViewById(R.id.container_2);
        }else {
            container = view.findViewById(R.id.container);
        }
        deviceList = view.findViewById(R.id.device_list);
        areaSelector1 = view.findViewById(R.id.drop_menu_bt);
        areaSelector2 = view.findViewById(R.id.area_selector_2);
        areaSelector1.setOnClickListener(this);
        areaSelector2.setOnClickListener(this);
        InternetUtils.generateEleOverview(LoginMsg.uniqueId);
        InternetUtils.generateElectricitys(LoginMsg.uniqueId, "");
        InternetUtils.areaInfo(LoginMsg.uniqueId);
        InternetUtils.inverters(LoginMsg.uniqueId);
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
                    case 1:
                        return (int) value + "时";
                    case 2:
                        return (int) value + "日";
                    case 3:
                        return (int) value + "月";
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
//        legend.setEnabled(false);
        legend.setTextColor(0xffffffff);
        legend.setTextSize(12);

        List<ILineDataSet> dataSets = new ArrayList<>();
        if (list != null && list.size() > 0) {
            LineDataSet dataSet_1 = new LineDataSet(list, "功率");
            ChartUtil.lineSet(getContext(), dataSet_1, 0xFF00FF72, LineDataSet.Mode.CUBIC_BEZIER, R.drawable.line_shape_green);
            dataSets.add(dataSet_1);
        }
        LineData data = new LineData(dataSets);
        mChart.setData(data);
    }


    private void addDevice(String title, String status, String power, String powerDaily, final GenerateElectricitysMsg.Device device) {
        final Activity activity = getActivity();
        if (activity == null) return;
        View view = LayoutInflater.from(activity).inflate(R.layout.list_item_device_manage, deviceList, false);
        TextView titleText = view.findViewById(R.id.title);
        TextView statusText = view.findViewById(R.id.status);
        TextView powerText = view.findViewById(R.id.power);
        TextView powerDailyText = view.findViewById(R.id.power_daily);
        titleText.setText(title);
        if (status.equals("0")) {
            statusText.setTextColor(0xFF555555);
            statusText.setText(activity.getResources().getString(R.string.state_off));
        } else {
            statusText.setTextColor(0xFF00CC00);
            statusText.setText(activity.getResources().getString(R.string.state_nor));
        }
        powerText.setText(power);
        powerDailyText.setText(powerDaily);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isPad) {
                    View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_device_detail, null);
                    AlertDialog dialog = new AlertDialog.Builder(activity).setView(dialogView).create();
                    Window window = dialog.getWindow();
                    if (window != null) window.setBackgroundDrawable(new ColorDrawable());
                    dialog.show();
                } else {
                    Intent intent = new Intent(activity, DeviceDetailActivity.class);
                    intent.putExtra("areaName", device.areaName);
                    intent.putExtra("devName", device.devName);
                    intent.putExtra("installTime", device.installTime);
                    intent.putExtra("power", device.power);
                    intent.putExtra("ele_day", device.ele_day);
                    intent.putExtra("ele_total", device.ele_total);
                    intent.putExtra("status", device.status);
                    intent.putExtra("devId", device.devId);
                    intent.putExtra("sn", device.datalog_sn);
                    activity.startActivity(intent);
                }
            }
        });
        deviceList.addView(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_device:
                pop();
                break;
            case R.id.drop_menu_bt:
                dropMenu(MENU_1);
                break;
            case R.id.scan_qr_code:
                if (CommentUtils.checkPermission(getActivity(), new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 201)) {
                    startActivityForResult(new Intent(getActivity(), CaptureActivity.class), 200);
                }
                break;
            case R.id.pop_cancel:
                addDevicePop.dismiss();
                break;
            case R.id.pop_confirm:
                String deviceSnText = deviceSn.getText().toString();
                String authCodeText = authCode.getText().toString();
                if (checkTextEmpty(deviceSnText) && checkTextEmpty(authCodeText)) {
                    InternetUtils.addCollector(LoginMsg.uniqueId, deviceSnText, authCodeText);
                }
                break;
            case R.id.area_selector_2:
                dropMenu(MENU_2);
                break;
            case R.id.time_picker:
                selectTime();
                break;
        }
    }

    private void dropMenu(final int witch) {
        if (witch == MENU_1) {
            if (devList == null || devList.size() <= 0) return;
        } else if (witch == MENU_2) {
            if (areaInfoList == null || areaInfoList.size() <= 0) return;
        }
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int size = 0;
        if (witch == MENU_1) {
            layout.setLayoutParams(new ViewGroup.LayoutParams(areaSelector1.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
            size = devList.size();
        } else if (witch == MENU_2) {
            layout.setLayoutParams(new ViewGroup.LayoutParams(areaSelector2.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
            size = areaInfoList.size();
        }
        final PopupWindow popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView textView;
        for (int i = 0; i < size; i++) {
            textView = new TextView(getContext());
            textView.setPadding(10, 2, 10, 2);
            textView.setTextColor(0xFFFFFFFF);
            textView.setBackgroundColor(0xff022632);
            textView.setTextSize(15);
            layout.addView(textView);
            if (witch == MENU_1) {
                final InvertersMsg.DevBean bean = devList.get(i);
                textView.setText(bean.datalog_sn);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        changeView(bean);
                    }
                });
            } else {
                final AreaInfoMsg.AreaInfo info = areaInfoList.get(i);
                textView.setText(info.name);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        InternetUtils.generateElectricitys(LoginMsg.uniqueId, info.path);
                        areaSelector2.setText(info.name);
                    }
                });
            }
        }
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        if (witch == MENU_1) {
            popupWindow.showAsDropDown(areaSelector1);
        } else if (witch == MENU_2) {
            popupWindow.showAsDropDown(areaSelector2);
        }
    }

    private boolean checkTextEmpty(String s) {
        if (s == null || s.equals("null") || s.trim().length() <= 0) {
            FragmentActivity activity = getActivity();
            if (activity == null) return false;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "请填写完整信息", Toast.LENGTH_SHORT).show();
                }
            });
            return false;
        }
        return true;
    }

    private void pop() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_collect_device, null);
        if (!MainActivity.isPad) {
            addDevicePop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            addDevicePop.setOutsideTouchable(true);
            addDevicePop.setTouchable(true);
            addDevicePop.setFocusable(true);
            addDevicePop.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
            final WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = 0.5f;
            getActivity().getWindow().setAttributes(lp);
            addDevicePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    lp.alpha = 1;
                    getActivity().getWindow().setAttributes(lp);
                }
            });
        } else {
            new AlertDialog.Builder(getContext()).setView(view).show();
        }
        view.findViewById(R.id.scan_qr_code).setOnClickListener(this);
        view.findViewById(R.id.pop_cancel).setOnClickListener(this);
        view.findViewById(R.id.pop_confirm).setOnClickListener(this);
        deviceSn = view.findViewById(R.id.device_sn);
        authCode = view.findViewById(R.id.auth_code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                deviceSn.setText(content);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ds(AddCollectorMsg msg) {
        if (msg.code.equals("0")) {
            Toast.makeText(getContext(), "添加成功", Toast.LENGTH_SHORT).show();
            addDevicePop.dismiss();
        } else {
            Toast.makeText(getContext(), msg.data, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void d2s(GenerateElectricitysMsg msg) {
        if (msg.code.equals("0")) {
            if (msg.list != null) {
                for (int i = 0; i < msg.list.size(); i++) {
                    GenerateElectricitysMsg.Device device = msg.list.get(i);
                    addDevice(device.areaName + device.devName + "(" + device.datalog_sn + ")",
                            device.status,
                            "功率：" + device.power + "w",
                            "当日发电：" + device.ele_day + "kWh", device);
                }
            }
        } else {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void d1s(AreaInfoMsg msg) {
        if (msg.code.equals("0")) {
            areaInfoList = msg.list;
        } else {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getlist(InvertersMsg msg) {
        if (msg.code.equals("0")) {
            if (msg.devList == null || msg.devList.size() <= 0) return;
            devList = msg.devList;
            changeView(devList.get(0));
        } else {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        }
    }

    private void changeView(InvertersMsg.DevBean bean) {
        if (bean == null) return;
        areaSelector1.setText(bean.datalog_sn);
        InternetUtils.statisticsData(LoginMsg.uniqueId, bean.inverterId);
        InternetUtils.storageSystemData(LoginMsg.uniqueId, bean.inverterId);
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        switch (bean.type) {
            case "INVERTER":
                sysType = INVERTER;
                title.setText("光伏系统运行图");
                if (MainActivity.isPad && dm.widthPixels > dm.heightPixels) {
                    dynamicView_pad_1 = LayoutInflater.from(getContext()).inflate(R.layout.layout_inverter_block_pad_1, container, false);
                    dynamicView_pad_2 = LayoutInflater.from(getContext()).inflate(R.layout.layout_inverter_block_pad_2, container, false);
                } else {
                    dynamicView_phone = LayoutInflater.from(getContext()).inflate(R.layout.layout_inverter_block, container, false);
                }
                break;
            case "BIG_HPS":
                sysType = BIG_HPS;
                title.setText("光储系统运行图");
                dynamicView_phone = LayoutInflater.from(getContext()).inflate(R.layout.layout_big_hps_block, container, false);
                break;
            case "BATTERY":
                sysType = BATTERY;
                title.setText("储能系统运行图");
                dynamicView_phone = LayoutInflater.from(getContext()).inflate(R.layout.layout_battery_block, container, false);
                break;
        }

        if (MainActivity.isPad && dm.widthPixels > dm.heightPixels) {
            power_grid = dynamicView_pad_2.findViewById(R.id.power_grid);
            power_cost = dynamicView_pad_2.findViewById(R.id.power_cost);
            power_pv = dynamicView_pad_2.findViewById(R.id.power_pv);
            pv_in = dynamicView_pad_1.findViewById(R.id.pv_in);
            pv_out = dynamicView_pad_1.findViewById(R.id.pv_out);
            status = mRootView.findViewById(R.id.status);
            pv_in_percent = dynamicView_pad_1.findViewById(R.id.pv_in_percent);
            pv_out_percent = dynamicView_pad_1.findViewById(R.id.pv_out_percent);
            pv_total = dynamicView_pad_1.findViewById(R.id.pv_total);
            power_cost_percent = dynamicView_pad_2.findViewById(R.id.power_cost_percent);
            c1 = dynamicView_pad_1.findViewById(R.id.percent_circle_pv);
            imageView6 = dynamicView_pad_2.findViewById(R.id.anim_06);
            imageView7 = dynamicView_pad_2.findViewById(R.id.anim_07);
            imageView8 = dynamicView_pad_2.findViewById(R.id.anim_08);
            imageView1 = dynamicView_pad_1.findViewById(R.id.anim_01);
            imageView2 = dynamicView_pad_1.findViewById(R.id.anim_02);
            timeText = dynamicView_pad_1.findViewById(R.id.time_picker);
            mChart = dynamicView_pad_1.findViewById(R.id.lint_chart);
            timeGroup = dynamicView_pad_1.findViewById(R.id.time_group);
            power_theory = dynamicView_pad_1.findViewById(R.id.power_theory);
            ele_total = dynamicView_pad_1.findViewById(R.id.ele_total);
            benifit_total = dynamicView_pad_1.findViewById(R.id.benifit_total);
            ele_cost = dynamicView_pad_1.findViewById(R.id.ele_cost);
            cost_pv = dynamicView_pad_1.findViewById(R.id.cost_pv);
            cost_grid = dynamicView_pad_1.findViewById(R.id.cost_grid);
            cost_pv_percent = dynamicView_pad_1.findViewById(R.id.cost_pv_percent);
            cost_grid_percent = dynamicView_pad_1.findViewById(R.id.cost_grid_percent);
            c2 = dynamicView_pad_1.findViewById(R.id.circle_percent);
            imageView3 = dynamicView_pad_1.findViewById(R.id.anim_03);
            imageView4 = dynamicView_pad_1.findViewById(R.id.anim_04);
        }else {
            power_grid = dynamicView_phone.findViewById(R.id.power_grid);
            power_cost = dynamicView_phone.findViewById(R.id.power_cost);
            power_pv = dynamicView_phone.findViewById(R.id.power_pv);
            pv_in = dynamicView_phone.findViewById(R.id.pv_in);
            pv_out = dynamicView_phone.findViewById(R.id.pv_out);
            status = dynamicView_phone.findViewById(R.id.status);
            pv_in_percent = dynamicView_phone.findViewById(R.id.pv_in_percent);
            pv_out_percent = dynamicView_phone.findViewById(R.id.pv_out_percent);
            pv_total = dynamicView_phone.findViewById(R.id.pv_total);
            power_cost_percent = dynamicView_phone.findViewById(R.id.power_cost_percent);
            c1 = dynamicView_phone.findViewById(R.id.percent_circle_pv);
            imageView6 = dynamicView_phone.findViewById(R.id.anim_06);
            imageView7 = dynamicView_phone.findViewById(R.id.anim_07);
            imageView8 = dynamicView_phone.findViewById(R.id.anim_08);
            imageView1 = dynamicView_phone.findViewById(R.id.anim_01);
            imageView2 = dynamicView_phone.findViewById(R.id.anim_02);
            timeText = dynamicView_phone.findViewById(R.id.time_picker);
            mChart = dynamicView_phone.findViewById(R.id.lint_chart);
            timeGroup = dynamicView_phone.findViewById(R.id.time_group);
            power_theory = dynamicView_phone.findViewById(R.id.power_theory);
            ele_total = dynamicView_phone.findViewById(R.id.ele_total);
            benifit_total = dynamicView_phone.findViewById(R.id.benifit_total);
            ele_cost = dynamicView_phone.findViewById(R.id.ele_cost);
            cost_pv = dynamicView_phone.findViewById(R.id.cost_pv);
            cost_grid = dynamicView_phone.findViewById(R.id.cost_grid);
            cost_pv_percent = dynamicView_phone.findViewById(R.id.cost_pv_percent);
            cost_grid_percent = dynamicView_phone.findViewById(R.id.cost_grid_percent);
            c2 = dynamicView_phone.findViewById(R.id.circle_percent);
            imageView3 = dynamicView_phone.findViewById(R.id.anim_03);
            imageView4 = dynamicView_phone.findViewById(R.id.anim_04);
        }

        timeText.setOnClickListener(this);
        timeGroup.check(R.id.time_cur);
        if (time == null || time.equals("") || time.equals("null")) {
            time = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale).format(new Date());
        }
        timeText.setText(time);
        timeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.time_cur:
                        timeType = 1;
                        if (timeText.getVisibility() == View.GONE)
                            timeText.setVisibility(View.VISIBLE);
                        timeText.setText(time);
                        InternetUtils.outputAndInputOfEle(LoginMsg.uniqueId, timeType, time.substring(0, 4) + time.substring(5, 7) + time.substring(8, 10));
                        break;
                    case R.id.time_day:
                        timeType = 2;
                        if (timeText.getVisibility() == View.GONE)
                            timeText.setVisibility(View.VISIBLE);
                        timeText.setText(time.substring(0, 7));
                        InternetUtils.outputAndInputOfEle(LoginMsg.uniqueId, timeType, time.substring(0, 4) + time.substring(5, 7));
                        break;
                    case R.id.time_month:
                        timeType = 3;
                        if (timeText.getVisibility() == View.GONE)
                            timeText.setVisibility(View.VISIBLE);
                        timeText.setText(time.substring(0, 4));
                        InternetUtils.outputAndInputOfEle(LoginMsg.uniqueId, timeType, time.substring(0, 4));
                        break;
                    case R.id.time_year:
                        timeType = 4;
                        timeText.setVisibility(View.GONE);
                        InternetUtils.outputAndInputOfEle(LoginMsg.uniqueId, timeType, time.substring(0, 4));
                        break;
                }
            }
        });
        InternetUtils.outputAndInputOfEle(LoginMsg.uniqueId, timeType, time.substring(0, 4) + time.substring(5, 7) + time.substring(8, 10));

        if (MainActivity.isPad && dm.widthPixels > dm.heightPixels) {
            container_pad_1.removeViewAt(1);
            container_pad_1.addView(dynamicView_pad_1, 1);
            container_pad_2.removeViewAt(1);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            dynamicView_pad_2.setLayoutParams(params);
            container_pad_2.addView(dynamicView_pad_2, 1);
        }else {
            container.removeViewAt(1);
            container.addView(dynamicView_phone, 1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void d3231s(StatisticsDataMsg msg) {
        if (msg.code.equals("0")) {
            switch (sysType) {
                case INVERTER:
                    setInverterData(msg);
                    break;
                case BIG_HPS:
                    break;
                case BATTERY:
                    break;

            }
        } else {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void d31s(OutputAndInputOfEleMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            showChart(msg.list);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void d231s(StorageSystemDataMsg msg) {
        if (msg.code.equals("0")) {
            switch (sysType) {
                case INVERTER:
                    setInverterData(msg);
                    break;
                case BIG_HPS:
                    break;
                case BATTERY:
                    break;

            }
        } else {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        }
    }

    private void setInverterData(StorageSystemDataMsg msg) {

        Locale locale = getResources().getConfiguration().locale;
        String tempString;
        double parseDouble1 = Double.parseDouble(msg.power_grid);
        tempString = "电网功率：" + String.format(locale, "%.1f", parseDouble1) + "W";
        power_grid.setText(tempString);
        double parseDouble2 = Double.parseDouble(msg.power_cost);
        tempString = "用电功率：" + String.format(locale, "%.1f", parseDouble2) + "W";
        power_cost.setText(tempString);
        double parseDouble3 = Double.parseDouble(msg.power_pv);
        tempString = "PV功率：" + String.format(locale, "%.1f", parseDouble3) + "W";
        power_pv.setText(tempString);
        double d1 = Double.parseDouble(msg.pv_in);
        tempString = String.format(locale, "%.1f", d1);
        pv_in.setText(tempString);
        double d2 = Double.parseDouble(msg.pv_out);
        tempString = String.format(locale, "%.1f", d2);
        pv_out.setText(tempString);
        if (msg.status.equals("true")) status.setText("并网");
        else status.setText("离网");
        double total = d1 + d2;
        pv_total.setText(String.format(locale, "%.1f", total));
        tempString = String.valueOf(Math.round(d1 * 100 / total)) + "%";
        pv_in_percent.setText(tempString);
        tempString = String.valueOf(Math.round(d2 * 100 / total)) + "%";
        pv_out_percent.setText(tempString);
        c1.setProgress((int) (d2 * 100 / total));

        AnimationDrawable animation;
        if (parseDouble1 > 0) {
            imageView6.setImageResource(R.drawable.anim_08);
            animation = (AnimationDrawable) imageView6.getDrawable();
            animation.start();
        }
        if (parseDouble2 > 0) {
            imageView8.setImageResource(R.drawable.anim_10);
            animation = (AnimationDrawable) imageView8.getDrawable();
            animation.start();
        }
        if (parseDouble3 > 0) {
            imageView7.setImageResource(R.drawable.anim_09);
            animation = (AnimationDrawable) imageView7.getDrawable();
            animation.start();
        }
        if (d1 > 0) {
            imageView1.setImageResource(R.drawable.anim_03);
            animation = (AnimationDrawable) imageView1.getDrawable();
            animation.start();
        }
        if (d2 > 0) {
            imageView2.setImageResource(R.drawable.anim_04);
            animation = (AnimationDrawable) imageView2.getDrawable();
            animation.start();
        }
    }

    private void setInverterData(StatisticsDataMsg msg) {

        Locale locale = getResources().getConfiguration().locale;
        String tempString;
        tempString = String.format(locale, "%.1f", Double.parseDouble(msg.power_theory));
        power_theory.setText(tempString);
        tempString = String.format(locale, "%.1f", Double.parseDouble(msg.ele_total));
        ele_total.setText(tempString);
        tempString = String.format(locale, "%.1f", Double.parseDouble(msg.benifit_total));
        benifit_total.setText(tempString);
        double d = Double.parseDouble(msg.ele_cost);
        tempString = String.format(locale, "%.1f", d);
        ele_cost.setText(tempString);
        double d1 = Double.parseDouble(msg.cost_pv);
        tempString = String.format(locale, "%.1f", d1);
        cost_pv.setText(tempString);
        double d2 = Double.parseDouble(msg.cost_grid);
        tempString = String.format(locale, "%.1f", d2);
        cost_grid.setText(tempString);
        tempString = String.valueOf(Math.round(d2 * 100 / d)) + "%";
        cost_grid_percent.setText(tempString);
        tempString = String.valueOf(Math.round(d1 * 100 / d)) + "%";
        cost_pv_percent.setText(tempString);
        c2.setProgress((int) (d2 * 100));

        if (d1 > 0) {
            imageView3.setImageResource(R.drawable.anim_05);
            AnimationDrawable animation3 = (AnimationDrawable) imageView3.getDrawable();
            animation3.start();
        }
        if (d2 > 0) {
            imageView4.setImageResource(R.drawable.anim_06);
            AnimationDrawable animation4 = (AnimationDrawable) imageView4.getDrawable();
            animation4.start();
        }
    }

    private void selectTime() {
        TimePickerView pvTime = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
                time = format.format(date);
                if (timeType == 1) {
                    timeText.setText(time);
                    InternetUtils.outputAndInputOfEle(LoginMsg.uniqueId, timeType, time.substring(0, 4) + time.substring(5, 7) + time.substring(8, 10));
                } else if (timeType == 2) {
                    timeText.setText(time.substring(0, 7));
                    InternetUtils.outputAndInputOfEle(LoginMsg.uniqueId, timeType, time.substring(0, 4) + time.substring(5, 7));
                } else {
                    timeText.setText(time.substring(0, 4));
                    InternetUtils.outputAndInputOfEle(LoginMsg.uniqueId, timeType, time.substring(0, 4));
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
}
