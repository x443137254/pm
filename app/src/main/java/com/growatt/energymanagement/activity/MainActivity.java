package com.growatt.energymanagement.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.fragment.AnalyzeFragment;
import com.growatt.energymanagement.fragment.EleFragment;
import com.growatt.energymanagement.fragment.EnergyFragment;
import com.growatt.energymanagement.fragment.HomeFragment;
import com.growatt.energymanagement.fragment.PadPersonFragment;
import com.growatt.energymanagement.msgs.AllAreaMsg;
import com.growatt.energymanagement.msgs.GetAddressMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.PopMsg;
import com.growatt.energymanagement.msgs.UpdateMsg;
import com.growatt.energymanagement.msgs.UpdateProgressMsg;
import com.growatt.energymanagement.msgs.WeatherMsg;
import com.growatt.energymanagement.service.DownloadService;
import com.growatt.energymanagement.utils.CommentUtils;
import com.growatt.energymanagement.utils.InternetUtils;
import com.growatt.energymanagement.utils.LocationUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

/**
 * 主页面框架，包括个人中心页面以及4个一级页面
 */
public class MainActivity extends BasicActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private FragmentManager mFragmentManager;
    private HomeFragment homeFragment;
    private EleFragment eleFragment;

    private EnergyFragment energyFragment;
    private AnalyzeFragment analyzeFragment;

    public static boolean isPad;

    private BroadcastReceiver mReceiver;
    private Fragment padPersonFragment;
    private TextView account;
    private TextView company;
    private DrawerLayout drawer;

    private AlertDialog dialog;
    private ProgressBar updateProgress;
    private String city;
    private TextView weather;
    private TextView temperature;
    private TextView wind_speed;
    private String uniqueId;
    private String nick;
    private String accountText;
    private boolean hasMsg;
    private String companyName;
    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPad = CommentUtils.isPad(this);

        if (isPad) setContentView(R.layout.activity_home_pad);
        else setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        readCache();
        init();

        mRadioGroup = findViewById(R.id.tab_radio_group);
        mRadioGroup.check(R.id.radio_home);
        mRadioGroup.setOnCheckedChangeListener(this);
        mFragmentManager = getSupportFragmentManager();
        if (mFragmentManager.getFragments().size() == 0) {
            mFragmentManager.beginTransaction().add(R.id.fl, homeFragment).commit();
        }

        if (isPad){
            weather = findViewById(R.id.weather);
            temperature = findViewById(R.id.temperature);
            wind_speed = findViewById(R.id.wind_speed);
        }


        if (CommentUtils.checkPermission(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 888)) {
            LocationUtils.getAddress(this);
        }

        if (!isPad && getIntent().getBooleanExtra("isFirst",false)){
            pop();
        }

    }

    private void readCache() {
        SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        uniqueId = sp.getString("uniqueId","");
        if (uniqueId.equals("")){
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        nick = sp.getString("nick","");
        accountText = sp.getString("account","");
        hasMsg = sp.getBoolean("hasMsg",false);
        companyName = sp.getString("companyName","");
        LoginMsg.uniqueId = uniqueId;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocationUtils.getAddress(this);
        }
    }

    private void init() {
        homeFragment = new HomeFragment();
        eleFragment = new EleFragment();
        energyFragment = new EnergyFragment();
        analyzeFragment = new AnalyzeFragment();
        if (!isPad) {
            findViewById(R.id.user_item).setOnClickListener(this);
            findViewById(R.id.info_item).setOnClickListener(this);
            findViewById(R.id.conf_item).setOnClickListener(this);
            findViewById(R.id.setting_bar).setOnClickListener(this);
        }
        if (isPad) {
            setBackground();
            padPersonFragment = new PadPersonFragment();
        }
        account = findViewById(R.id.draw_account);
        company = findViewById(R.id.draw_company);
        drawer = findViewById(R.id.drawer);



        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (uniqueId.equals("")) {
                    account.setText(getResources().getString(R.string.click_login));
                    company.setText("");
                } else {
                    company.setText(companyName);
                    if (!nick.equals("")){
                        account.setText(nick);
                    }else {
                        account.setText(accountText);
                    }
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    private void pop() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_tip_add_device,null);
        if (!MainActivity.isPad) {
            PopupWindow pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        }else {
            new AlertDialog.Builder(this).setView(view).show();
        }
    }

    private void setBackground() {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        View view = findViewById(R.id.fl);
        if (dm.widthPixels > dm.heightPixels) {
            view.setBackgroundResource(R.mipmap.bg_h_pad);
        } else view.setBackgroundResource(R.mipmap.bg_v_pad);
    }

    /**
     * n
     * 注册屏幕旋转广播
     */
    private void registerTurnOverReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                if (metrics.widthPixels > metrics.heightPixels) {
                    Toast.makeText(context, "横", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "竖", Toast.LENGTH_SHORT).show();
                }
            }
        };
        registerReceiver(mReceiver, new IntentFilter("android.intent.action.CONFIGURATION_CHANGED"));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_home:
                mFragmentManager.beginTransaction().replace(R.id.fl, homeFragment).commit();
                break;
            case R.id.radio_ele:
                mFragmentManager.beginTransaction().replace(R.id.fl, eleFragment).commit();
                break;
            case R.id.radio_energy:
                mFragmentManager.beginTransaction().replace(R.id.fl, energyFragment).commit();
                break;
            case R.id.radio_analyze:
                mFragmentManager.beginTransaction().replace(R.id.fl, analyzeFragment).commit();
                break;
            case R.id.radio_person:
                mFragmentManager.beginTransaction().replace(R.id.fl, padPersonFragment).commit();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showPop(PopMsg msg) {
        drawer.openDrawer(Gravity.START);
        if (uniqueId.equals("")) {
            account.setText(getResources().getString(R.string.click_login));
            company.setText("");
        } else {
            account.setText(accountText);
            company.setText(companyName);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_item:
                if (uniqueId.equals("")) {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(this, PersonActivity.class));
                }
                drawer.closeDrawer(Gravity.START);
                break;
            case R.id.info_item:
                if (uniqueId.equals("")) {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(this, InfoMaintainActivity.class));
                }
                drawer.closeDrawer(Gravity.START);
                break;
            case R.id.conf_item:
                if (uniqueId.equals("")) {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(this, PriceConfActivity.class));
                }
                drawer.closeDrawer(Gravity.START);
                break;
            case R.id.setting_bar:
                startActivity(new Intent(this, SettingActivity.class));
                drawer.closeDrawer(Gravity.START);
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void alerUpdateDialog(final UpdateMsg msg) {
        if (!msg.isBig) return;
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update, null);
        view.findViewById(R.id.update_dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        TextView version = view.findViewById(R.id.update_dialog_version_text);
        final TextView updateButton = view.findViewById(R.id.update_dialog_bt);
        TextView updateDesc = view.findViewById(R.id.update_dialog_desc);
        final TextView updateNextTime = view.findViewById(R.id.update_dialog_next_time);
        updateProgress = view.findViewById(R.id.update_dialog_progress);

        String s;
        if (msg.isBig) {
            s = "新版本：" + msg.info.version_name_big + "， 大小：" + msg.info.file_size_big;
        } else {
            s = "新版本：" + msg.info.version_name_small + "， 大小：" + msg.info.file_size_small;
        }
        version.setText(s);

        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String des;
        if (language.contains("zh")) {
            des = msg.info.cnLog;
        } else if (language.contains("en")) {
            des = msg.info.enLog;
        } else {
            des = msg.info.defaultLog;
        }
        updateDesc.setText(des);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNextTime.setText(getResources().getString(R.string.update_background));
                updateButton.setText(getResources().getString(R.string.updating));
                updateButton.setOnClickListener(null);
                if (CommentUtils.checkPermission(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 9999)) {
                    startDownload(msg.info.download_url);
                }
            }
        });

        updateNextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog = new AlertDialog.Builder(this).setView(view).create();
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void startDownload(String url) {
        Intent intent = new Intent(MainActivity.this, DownloadService.class);
        intent.setAction(url);
        startService(intent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setProgress(UpdateProgressMsg msg) {
        if (updateProgress != null) updateProgress.setProgress(msg.progress);
        if (msg.progress >= 100) dialog.cancel();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getLocalCity(GetAddressMsg msg) {
        city = msg.city;
        InternetUtils.allArea();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAllCity(AllAreaMsg msg) {
        if (msg.originalString.equals("")) return;
        int index = msg.originalString.indexOf(city);
        String adCode = msg.originalString.substring(index - 16, index - 10);
        InternetUtils.weather(adCode);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void showWeather(WeatherMsg msg) {
        if (!msg.city.equals("") && isPad) {
            String s = msg.winddirection + msg.windpower + "级";
            wind_speed.setText(s);
            s = msg.temperature + "°";
            temperature.setText(s);
            weather.setText(msg.weather);
        }
    }
    public void jumpToEle() {
        mRadioGroup.check(R.id.radio_ele);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                eleFragment.jumpTo();
            }
        }, 200);
    }
}
