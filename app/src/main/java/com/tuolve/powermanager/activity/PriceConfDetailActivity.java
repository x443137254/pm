package com.tuolve.powermanager.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.tuolve.powermanager.R;
import com.tuolve.powermanager.msgs.UpdateElePriceMsg;
import com.tuolve.powermanager.utils.InternetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人-电价配置-详情编辑
 */
public class PriceConfDetailActivity extends BasicActivity implements View.OnClickListener {

    private GridLayout timeArray;
    private static int n = 0;
    private TextView effTime;
    private TextView isEff;
    private PopupWindow pop;
    private String year = "";
    private String month = "";
    private String day = "";
    private String hour = "";
    private String minute = "";
    private String second = "";
    private TextView priceName;
    private TextView price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_conf_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.price_conf_detail_back).setOnClickListener(this);
        findViewById(R.id.add_time).setOnClickListener(this);
        findViewById(R.id.eff_time_item).setOnClickListener(this);
        findViewById(R.id.is_eff_item).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
        effTime = findViewById(R.id.eff_time_text);
        isEff = findViewById(R.id.is_eff_text);
        timeArray = findViewById(R.id.time_array);
        priceName = findViewById(R.id.price_name);
        price = findViewById(R.id.price);
        priceName.setOnClickListener(this);
        price.setOnClickListener(this);
        Intent intent = getIntent();
        String s;
        s = intent.getStringExtra("priceName");
        priceName.setText(s);
        s = intent.getStringExtra("price");
        price.setText(s);
        s = intent.getStringExtra("time");
        String[] split = s.split(",");
        TextView textView;
        for (String aSplit : split) {
            textView = (TextView) LayoutInflater.from(this).inflate(R.layout.layout_text, timeArray, false);
            textView.setText(aSplit);
            timeArray.addView(textView);
        }
        s = intent.getStringExtra("effTime");
        effTime.setText(s);
        s = intent.getStringExtra("isEff");
        isEff.setText(s);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.price_conf_detail_back:
                finish();
                break;
            case R.id.add_time:
                startActivityForResult(new Intent(this, EditTimeActivity.class), 700);
                break;
            case R.id.eff_time_item:
                popEffTime();
                break;
            case R.id.is_eff_item:
                List<String> data = new ArrayList<>(2);
                data.add(getResources().getString(R.string.yes));
                data.add(getResources().getString(R.string.no));
                showPickView(data, isEff, getResources().getString(R.string.take_effect));
                break;
            case R.id.pop_cancel:
                pop.dismiss();
                break;
            case R.id.pop_confirm:
                pop.dismiss();
                if (year.equals("永久有效")) {
                    effTime.setText("永久有效");
                } else if (!year.equals("")
                        && !month.equals("")
                        && !day.equals("")
                        && !hour.equals("")
                        && !minute.equals("")
                        && !second.equals("")) {
                    String s = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
                    effTime.setText(s);
                }
                break;
            case R.id.price_name:
                startActivityForResult(new Intent(this, ModifyActivity.class), 600);
                break;
            case R.id.price:
                startActivityForResult(new Intent(this, ModifyActivity.class), 601);
                break;
            case R.id.save:
                int cid = getIntent().getIntExtra("cid", 0);
                String name = priceName.getText().toString();
                double priceText = Double.parseDouble(price.getText().toString());
                String effectiveTime = effTime.getText().toString();
                StringBuilder builder = new StringBuilder();
                TextView textView;
                for (int i = 0; i < timeArray.getChildCount(); i++) {
                    textView = (TextView) timeArray.getChildAt(i);
                    builder.append(textView.getText());
                    if (i == timeArray.getChildCount() - 1) continue;
                    builder.append(",");
                }
                String time = builder.toString();
                InternetUtils.updateElePrice(cid, name, effectiveTime, time, priceText);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ssde(UpdateElePriceMsg msg) {
        Toast.makeText(this, msg.msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出滚动选择器
     *
     * @param data     数据源
     * @param textView 显示的位置
     * @param title    选择器标题
     */
    private void showPickView(final List<String> data, final TextView textView, String title) {
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String tx = data.get(options1);
                textView.setText(tx);
            }
        })
                .setTitleText(title)
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
                    PriceConfDetailActivity.this.year = ((TextView) v).getText().toString();
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
        int maxDays = maxDays(Integer.parseInt(year), Integer.parseInt(month));
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
     * 计算当月一共有几天
     *
     * @param year  年份
     * @param month 月份
     * @return 天数
     */
    private int maxDays(int year, int month) {
        if (year <= 0 || month <= 0) return 0;
        if (month == 2) {
            if (year % 100 == 0 && year % 400 == 0) return 29;
            else if (year % 100 != 0 && year % 4 == 0) return 29;
            else return 28;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) return 30;
        else return 31;
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
                    Intent intent = new Intent(PriceConfDetailActivity.this, EditTimeActivity.class);
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
        } else if (requestCode == 600 && resultCode == 1000) {
            String s = data.getStringExtra("data");
            priceName.setText(s);
        } else if (requestCode == 601 && resultCode == 1000) {
            String s = data.getStringExtra("data");
            price.setText(s);
        }
    }
}
