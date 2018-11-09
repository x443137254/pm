package com.growatt.energymanagement.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.activity.AddPriceActivity;
import com.growatt.energymanagement.activity.EditTimeActivity;
import com.growatt.energymanagement.activity.MainActivity;
import com.growatt.energymanagement.activity.PriceConfDetailActivity;
import com.growatt.energymanagement.msgs.ElePriceInfoMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.utils.CommentUtils;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/10
 */

public class PriceConfFragment extends Fragment implements View.OnClickListener {

    private LinearLayout confList;
    private AlertDialog dialog;
    private TextView title;
    private TextView priceCate;
    private EditText priceName;
    private EditText price;
    private GridLayout timeArray;
    private TextView addTime;
    private TextView effText;
    private TextView isEffText;
    private PopupWindow pop;

    private String year = "";
    private String month = "";
    private String day = "";
    private String hour = "";
    private String minute = "";
    private String second = "";
    private static int n = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (LoginMsg.cid != 0) InternetUtils.elePriceInfo(LoginMsg.uniqueId);
        initDialog();
        return inflater.inflate(R.layout.fragment_price_conf_pad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.price_conf_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                title.setText("新增电价");
            }
        });
        confList = view.findViewById(R.id.price_conf_list);
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

    private void initDialog() {
        Context context = getContext();
        if (context == null) return;
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_price_pad, null);
        dialog = new AlertDialog.Builder(context).setView(dialogView).create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        title = dialogView.findViewById(R.id.title);
        priceCate = dialogView.findViewById(R.id.price_cate);
        priceName = dialogView.findViewById(R.id.price_name);
        price = dialogView.findViewById(R.id.price);
        timeArray = dialogView.findViewById(R.id.time_array);
        addTime = dialogView.findViewById(R.id.add_time);
        effText = dialogView.findViewById(R.id.eff_text);
        isEffText = dialogView.findViewById(R.id.is_eff_text);
        dialogView.findViewById(R.id.cancel).setOnClickListener(this);
        dialogView.findViewById(R.id.confirm).setOnClickListener(this);
        dialogView.findViewById(R.id.price_cate_bar).setOnClickListener(this);
        addTime.setOnClickListener(this);
        dialogView.findViewById(R.id.eff_bar).setOnClickListener(this);
        dialogView.findViewById(R.id.is_eff_bar).setOnClickListener(this);
    }

    /**
     * 添加电价配置条目
     *
     * @param title     电价名称
     * @param price     单价
     * @param time      起始时间
     * @param validTime 有效时间
     * @param isValid   是否有效
     */
    private void addConfItem(final int cid, final String title, final String price, final String time, final String validTime, final String isValid) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_price_conf_pad, confList, false);
        TextView titleView = view.findViewById(R.id.title);
        TextView priceView = view.findViewById(R.id.price);
        TextView startTimeView = view.findViewById(R.id.start_time);
        TextView validTimeView = view.findViewById(R.id.valid_time);
        TextView isValidView = view.findViewById(R.id.is_valid);
        titleView.setText(title);
        priceView.setText(price);
        startTimeView.setText(time);
        validTimeView.setText(validTime);
        isValidView.setText(isValid);
        confList.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PriceConfFragment.this.title.setText("电价配置");
                dialog.show();
//                Intent intent = new Intent(getContext(), PriceConfDetailActivity.class);
//                intent.putExtra("priceName", title);
//                intent.putExtra("price", price);
//                intent.putExtra("time", time);
//                intent.putExtra("effTime", validTime);
//                intent.putExtra("isEff", isValid);
//                intent.putExtra("cid", cid);
//                startActivity(intent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showList(ElePriceInfoMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            confList.removeAllViews();
            for (int i = 0; i < msg.elePriceList.size(); i++) {
                ElePriceInfoMsg.ElePrice elePrice = msg.elePriceList.get(i);
                String validTime = "永久有效";
                if (elePrice.effectiveTime != null && !elePrice.effectiveTime.equals(""))
                    validTime = elePrice.effectiveTime;
                String isValid = "是";
                if (elePrice.status == 1) isValid = "否";
                addConfItem(elePrice.cid, elePrice.name, String.valueOf(elePrice.price), elePrice.timeValue, validTime, isValid);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dialog.dismiss();
                break;
            case R.id.confirm:
                break;
            case R.id.price_cate_bar:
                List<String> data = new ArrayList<>();
                data.add("谷时");
                data.add("平时");
                data.add("峰时");
                data.add("尖时");
                CommentUtils.showPickView(getActivity(),data,priceCate,"电价类型");
                break;
            case R.id.add_time:
                startActivityForResult(new Intent(getContext(), EditTimeActivity.class), 700);
                break;
            case R.id.eff_bar:
                popEffTime();
                break;
            case R.id.is_eff_bar:
                List<String> data1 = new ArrayList<>(2);
                data1.add(getResources().getString(R.string.yes));
                data1.add(getResources().getString(R.string.no));
                CommentUtils.showPickView(getActivity(),data1,isEffText,"是否生效");
                break;
        }
    }

    private void popEffTime() {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_pop_effe_time, null);
        if (!MainActivity.isPad) {
            pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pop.setOutsideTouchable(true);
            pop.setTouchable(true);
            pop.setFocusable(true);
            pop.showAtLocation(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
            final WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = 0.5f;
            getActivity().getWindow().setAttributes(lp);
            pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    lp.alpha = 1;
                    getActivity().getWindow().setAttributes(lp);
                }
            });
        } else {
            new AlertDialog.Builder(getContext()).setView(view).show();
        }
        view.findViewById(R.id.pop_cancel).setOnClickListener(this);
        view.findViewById(R.id.pop_confirm).setOnClickListener(this);
        TabLayout tabs = view.findViewById(R.id.time_tabs);
        final LinearLayout timeContainer = view.findViewById(R.id.linear_layout);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                CharSequence text = tab.getText();
                if (text == null) return;
                String s = text.toString();
                switch (s) {
                    case "年":
                        addYearData(timeContainer);
                        break;
                    case "月":
                        addMonthData(timeContainer);
                        break;
                    case "日":
                        addDayData(timeContainer);
                        break;
                    case "时":
                        addHourData(timeContainer);
                        break;
                    case "分":
                        addMinuteData(timeContainer);
                        break;
                    case "秒":
                        addSecondData(timeContainer);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        addYearData(timeContainer);
    }


    /**
     * 添加年份数据
     *
     * @param container 列表容器
     */
    private void addYearData(final LinearLayout container) {
        if (container == null) return;
        container.removeAllViews();
        String[] years = {"永久有效", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025"};
        TextView textView;
        for (String year : years) {
            textView = new TextView(getContext());
            textView.setText(year);
            container.addView(textView);
            textView.setTag(container.getChildCount());
            setTextStyle(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = container.getChildCount();
                    for (int i = 0; i < count; i++) {
                        container.getChildAt(i).setBackground(null);
                    }
                    v.setBackgroundColor(0xff034356);
                    PriceConfFragment.this.year = ((TextView) v).getText().toString();
                }
            });
        }
    }

    /**
     * 添加月份数据
     *
     * @param container 列表容器
     */
    private void addMonthData(final LinearLayout container) {
        if (container == null) return;
        container.removeAllViews();
        if (year.equals("永久有效")) return;
        TextView textView;
        String s;
        for (int i = 1; i <= 12; i++) {
            textView = new TextView(getContext());
            s = i < 10 ? "0" + i + "月" : i + "月";
            textView.setText(s);
            container.addView(textView);
            textView.setTag(container.getChildCount());
            setTextStyle(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = container.getChildCount();
                    for (int i = 0; i < count; i++) {
                        container.getChildAt(i).setBackground(null);
                    }
                    v.setBackgroundColor(0xff034356);
                    month = ((TextView) v).getText().toString().substring(0, 2);
                }
            });
        }
    }

    /**
     * 添加小时数据
     *
     * @param container 列表容器
     */
    private void addHourData(final LinearLayout container) {
        if (container == null) return;
        container.removeAllViews();
        if (year.equals("永久有效")) return;
        TextView textView;
        String s;
        for (int i = 0; i < 24; i++) {
            textView = new TextView(getContext());
            s = i < 10 ? "0" + i : i + "";
            textView.setText(s);
            container.addView(textView);
            textView.setTag(container.getChildCount());
            setTextStyle(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = container.getChildCount();
                    for (int i = 0; i < count; i++) {
                        container.getChildAt(i).setBackground(null);
                    }
                    v.setBackgroundColor(0xff034356);
                    hour = ((TextView) v).getText().toString();
                }
            });
        }
    }

    /**
     * 添加分钟数据
     *
     * @param container 列表容器
     */
    private void addMinuteData(final LinearLayout container) {
        if (container == null) return;
        container.removeAllViews();
        if (year.equals("永久有效")) return;
        TextView textView;
        String s;
        for (int i = 0; i < 60; i++) {
            textView = new TextView(getContext());
            s = i < 10 ? "0" + i : i + "";
            textView.setText(s);
            container.addView(textView);
            textView.setTag(container.getChildCount());
            setTextStyle(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = container.getChildCount();
                    for (int i = 0; i < count; i++) {
                        container.getChildAt(i).setBackground(null);
                    }
                    v.setBackgroundColor(0xff034356);
                    minute = ((TextView) v).getText().toString();
                }
            });
        }
    }

    /**
     * 添加秒数据
     *
     * @param container 列表容器
     */
    private void addSecondData(final LinearLayout container) {
        if (container == null) return;
        container.removeAllViews();
        if (year.equals("永久有效")) return;
        TextView textView;
        String s;
        for (int i = 0; i < 60; i++) {
            textView = new TextView(getContext());
            s = i < 10 ? "0" + i : i + "";
            textView.setText(s);
            container.addView(textView);
            textView.setTag(container.getChildCount());
            setTextStyle(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = container.getChildCount();
                    for (int i = 0; i < count; i++) {
                        container.getChildAt(i).setBackground(null);
                    }
                    v.setBackgroundColor(0xff034356);
                    second = ((TextView) v).getText().toString();
                }
            });
        }
    }

    /**
     * 添加天数据
     *
     * @param container 列表容器
     */
    private void addDayData(final LinearLayout container) {
        if (container == null) return;
        container.removeAllViews();
        if (year.equals("") || month.equals("") || year.equals("永久有效")) return;
        int maxDays = CommentUtils.maxDays(Integer.parseInt(year), Integer.parseInt(month));
        TextView textView;
        String s;
        for (int i = 1; i <= maxDays; i++) {
            textView = new TextView(getContext());
            s = i < 10 ? "0" + i + "日" : i + "日";
            textView.setText(s);
            container.addView(textView);
            textView.setTag(container.getChildCount());
            setTextStyle(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = container.getChildCount();
                    for (int i = 0; i < count; i++) {
                        container.getChildAt(i).setBackground(null);
                    }
                    v.setBackgroundColor(0xff034356);
                    day = ((TextView) v).getText().toString().substring(0, 2);
                }
            });
        }
    }

    /**
     * 设置TextView的一些公共属性
     *
     * @param textView 目标textView
     */
    private void setTextStyle(TextView textView) {
        textView.setTextColor(0xffffffff);
        textView.setTextSize(15);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(40, 0, 0, 0);
    }
}
