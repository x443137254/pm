package com.growatt.energymanagement.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
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
import com.growatt.energymanagement.msgs.AddElePriceMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.utils.CommentUtils;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人-电价配置-添加电价
 */
public class AddPriceActivity extends BasicActivity implements View.OnClickListener {

    private EditText priceNameEdit;
    private EditText priceEdit;
    private PopupWindow pop;
    private TextView effectiveTime;
    private TextView priceClassify;
    private TextView isEffective;
    private String year = "";
    private String month = "";
    private String day = "";
    private String hour = "";
    private String minute = "";
    private String second = "";
    private static int n = 0;
    private GridLayout timeArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_price);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.add_price_back).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.price_classify_item).setOnClickListener(this);
        findViewById(R.id.add_time).setOnClickListener(this);
        findViewById(R.id.effectiveTime).setOnClickListener(this);
        findViewById(R.id.is_effective).setOnClickListener(this);
        priceClassify = findViewById(R.id.price_classify_text);
        priceNameEdit = findViewById(R.id.price_name);
        priceEdit = findViewById(R.id.price);
        effectiveTime = findViewById(R.id.effective_time_text);
        isEffective = findViewById(R.id.is_effective_text);
        timeArray = findViewById(R.id.time_array);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_price_back:
                finish();
                break;
            case R.id.save:
                String priceName = priceNameEdit.getText().toString();
                String price = priceEdit.getText().toString();
                String effTime = effectiveTime.getText().toString();
                int isEff = 0;
                if (isEffective.getText().toString().equals("否")) isEff = 1;
                StringBuilder builder = new StringBuilder();
                TextView textView;
                for (int i = 0; i < timeArray.getChildCount(); i++) {
                    textView = (TextView) timeArray.getChildAt(i);
                    builder.append(textView.getText());
                    if (i == timeArray.getChildCount() - 1) continue;
                    builder.append(",");
                }
                InternetUtils.addElePrice(LoginMsg.uniqueId,priceName,builder.toString(),Double.parseDouble(price),effTime,isEff);
                break;
            case R.id.price_classify_item:
                List<String> data = new ArrayList<>();
                data.add("谷时");
                data.add("平时");
                data.add("峰时");
                data.add("尖时");
                CommentUtils.showPickView(this,data,priceClassify,"电价类型");
                break;
            case R.id.add_time:
                startActivityForResult(new Intent(this, EditTimeActivity.class), 700);
                break;
            case R.id.effectiveTime:
                popEffTime();
                break;
            case R.id.is_effective:
                List<String> data1 = new ArrayList<>(2);
                data1.add(getResources().getString(R.string.yes));
                data1.add(getResources().getString(R.string.no));
                CommentUtils.showPickView(this,data1,isEffective,"是否生效");
                break;
            case R.id.pop_cancel:
                pop.dismiss();
                break;
            case R.id.pop_confirm:
                pop.dismiss();
                if (year.equals("永久有效")) {
                    effectiveTime.setText("永久有效");
                } else if (!year.equals("")
                        && !month.equals("")
                        && !day.equals("")
                        && !hour.equals("")
                        && !minute.equals("")
                        && !second.equals("")) {
                    String s = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
                    effectiveTime.setText(s);
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dsd(AddElePriceMsg msg){
        Toast.makeText(this, msg.msg, Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * 弹出有效时间选择器
     */
    private void popEffTime() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.layout_pop_effe_time, null);
        if (!MainActivity.isPad) {
            pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pop.setOutsideTouchable(true);
            pop.setTouchable(true);
            pop.setFocusable(true);
            pop.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
            final WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.5f;
            getWindow().setAttributes(lp);
            pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    lp.alpha = 1;
                    getWindow().setAttributes(lp);
                }
            });
        } else {
            new AlertDialog.Builder(this).setView(view).show();
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
            textView = new TextView(this);
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
                    AddPriceActivity.this.year = ((TextView) v).getText().toString();
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
            textView = new TextView(this);
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
            textView = new TextView(this);
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
            textView = new TextView(this);
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
            textView = new TextView(this);
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
            textView = new TextView(this);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 700) {
            TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.layout_text, timeArray, false);
            final String s = data.getStringExtra("time");
            textView.setText(s);
            timeArray.addView(textView);
            final int m = n;
            textView.setTag(m);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddPriceActivity.this, EditTimeActivity.class);
                    intent.putExtra("time", s);
                    startActivityForResult(intent, m);
                }
            });
            n++;
        } else if (resultCode == 701) {
            if (requestCode == 700) return;
            View view = null;
            int count = timeArray.getChildCount();
            for (int i = 0; i < count; i++) {
                view = timeArray.getChildAt(i);
                if ((Integer) view.getTag() == requestCode) break;
            }
            if (view == null) return;
            timeArray.removeView(view);
        } else if (resultCode == 702) {
            TextView textView = null;
            int count = timeArray.getChildCount();
            for (int i = 0; i < count; i++) {
                textView = (TextView) timeArray.getChildAt(i);
                if ((Integer) textView.getTag() == requestCode) break;
            }
            if (textView == null) return;
            String s = data.getStringExtra("time");
            textView.setText(s);
        }
    }
}
