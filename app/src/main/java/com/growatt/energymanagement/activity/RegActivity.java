package com.growatt.energymanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.growatt.energymanagement.R;
import com.growatt.energymanagement.msgs.AllAreaMsg;
import com.growatt.energymanagement.msgs.CountryDataMsg;
import com.growatt.energymanagement.msgs.GetCodeMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.RegistMsg;
import com.growatt.energymanagement.msgs.ThirdRegistMsg;
import com.growatt.energymanagement.msgs.UserNameExistMsg;
import com.growatt.energymanagement.utils.CommentUtils;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 注册
 */
public class RegActivity extends BasicActivity implements View.OnClickListener {

    private EditText phone;
    private EditText pwd;
    private EditText confirmPwd;
    private EditText companyName;
    private EditText address;
    private EditText install_sn;
    private TextView country;
    private TextView getCode;
    private TextView province;
    private TextView city;
    private TextView area;
    private TextView language;
    private CheckBox agreement;
    private boolean legal = true;
    private String account;
    private String password;
    private String company;
    private String addr;
    private String phoneCode;
    private String userType;
    private Timer timer;
    private int countDown = 60;
    private String CODE = "";
    private EditText phone_code;

    private List<String> provinceList;
    private List<List<String>> cityList;
    private List<List<List<String>>> areaList;

    private List<String> countryList;
    private List<List<String>> foreignCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MainActivity.isPad) setContentView(R.layout.activity_third_reg_pad);
        else setContentView(R.layout.activity_third_reg);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        initViews();
    }

    private void initViews() {
        phone = findViewById(R.id.phone);
        phone_code = findViewById(R.id.phone_code);
        getCode = findViewById(R.id.get_code);
        pwd = findViewById(R.id.pwd);
        confirmPwd = findViewById(R.id.reg_input_pwd_again);
        companyName = findViewById(R.id.company);
        address = findViewById(R.id.address);
        install_sn = findViewById(R.id.sn);
        country = findViewById(R.id.country);
        province = findViewById(R.id.province);
        city = findViewById(R.id.city);
        area = findViewById(R.id.area);
        language = findViewById(R.id.language);
        agreement = findViewById(R.id.agreement);
        findViewById(R.id.reg_back).setOnClickListener(this);
        findViewById(R.id.next_step).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.country).setOnClickListener(this);
        findViewById(R.id.select_city_bar).setOnClickListener(this);
        if (MainActivity.isPad) findViewById(R.id.back_to_login).setOnClickListener(this);
        getCode.setOnClickListener(this);
        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String s = phone.getText().toString();
                    if (s.equals("") || s.equals("null")) return;
                    InternetUtils.accountExist(s);
                }
            }
        });

//        RegionUtil regionUtil = new RegionUtil(this);
//        provinceList = regionUtil.getProvinceList();
//        cityList = new ArrayList<>();
//        areaList = new ArrayList<>();
//        for (int i = 0; i < provinceList.size(); i++) {
//            List<String> city_list = regionUtil.getCityList(i);
//            cityList.add(city_list);
//            List<List<String>> list = new ArrayList<>();
//            for (int j = 0; j < city_list.size(); j++) {
//                list.add(regionUtil.getAreaList(i, j));
//            }
//            areaList.add(list);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reg_back:
            case R.id.back_to_login:
            case R.id.cancel:
                finish();
                break;
            case R.id.next_step:
                checkLegal();
                if (!legal) return;
                if (!phoneCode.equals(CODE)) {
                    Toast.makeText(this, getResources().getString(R.string.code_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = getIntent();
                String appType  = intent.getStringExtra("appType");
                String thirdUnique  = intent.getStringExtra("thirdUnique");
                String address = province.getText().toString() + city.getText() + area.getText() + addr;
                if (thirdUnique != null && thirdUnique.length() > 0) {
                    InternetUtils.thirdRegist(appType,thirdUnique,account,account,company,
                            country.getText().toString(),language.getText().toString(),address,userType);
                }else {
                    InternetUtils.regist(password,account,country.getText().toString(),"中文",company,address,userType);
                }
                break;
            case R.id.country:
                if (countryList == null) {
                    showProgressDialog();
                    InternetUtils.countryData();
                }else {
                    CommentUtils.showPickView(this, countryList, country, "请选择国家");
                }
                break;
            case R.id.get_code:
                account = phone.getText().toString();
                if (account.equals("") || account.equals("null")) return;
                InternetUtils.sendCode(account);
                getCode.setEnabled(false);
                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        String s = countDown + "(s)";
                        if (countDown <= 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getCode.setEnabled(true);
                                }
                            });
                            s = getResources().getString(R.string.get_code_again);
                            timer.cancel();
                            countDown = 60;
                        } else {
                            countDown--;
                        }
                        final String finalS = s;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getCode.setText(finalS);
                            }
                        });
                    }
                };
                timer.schedule(timerTask, 0, 1000);
                break;
            case R.id.select_city_bar:
                String s = country.getText().toString();
                if (s.equals("中国")) {
                    if (provinceList != null) {
                        selectCity();
                    }
                    else {
                        showProgressDialog();
                        InternetUtils.allArea();
                    }
                } else {
                    if (countryList != null) {
                        int index = 0;
                        for (int i = 0; i < countryList.size(); i++) {
                            if (countryList.get(i).equals(country.getText().toString())){
                                index = i;
                                break;
                            }
                        }
                        CommentUtils.showPickView(this, foreignCityList.get(index), city, "请选择城市");
                    }else {
                        Toast.makeText(this, "请先选择国家", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void checkLegal() {
        legal = true;
        account = phone.getText().toString();
        if (account.equals("") || account.equals("null")) {
            legal = false;
            Toast.makeText(this, getResources().getString(R.string.reg_account_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        phoneCode = phone_code.getText().toString();
        if (phoneCode.equals("") || phoneCode.equals("null")) {
            legal = false;
            Toast.makeText(this, getResources().getString(R.string.phone_code_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        password = pwd.getText().toString();
        if (password.equals("") || password.equals("null")) {
            legal = false;
            Toast.makeText(this, getResources().getString(R.string.reg_pwd_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6 || password.length() > 12) {
            legal = false;
            Toast.makeText(this, getResources().getString(R.string.reg_pwd_length), Toast.LENGTH_SHORT).show();
            return;
        }

        String s = confirmPwd.getText().toString();
        if (s.equals("") || s.equals("null") || !s.equals(password)) {
            legal = false;
            Toast.makeText(this, getResources().getString(R.string.reg_pwd_diff), Toast.LENGTH_SHORT).show();
            return;
        }

        company = companyName.getText().toString();
        if (company.equals("") || company.equals("null")) {
            legal = false;
            Toast.makeText(this, getResources().getString(R.string.reg_company_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        addr = province.getText().toString();
        if (addr.equals("") || addr.equals("null")) {
            legal = false;
            Toast.makeText(this, getResources().getString(R.string.reg_add_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        userType = install_sn.getText().toString();
        if (userType.equals("") || userType.equals("null")) {
            legal = false;
            Toast.makeText(this, getResources().getString(R.string.reg_sn_empty), Toast.LENGTH_SHORT).show();
        }

        if (!agreement.isChecked()){
            legal = false;
            Toast.makeText(this, getResources().getString(R.string.reg_agreement), Toast.LENGTH_SHORT).show();
        }
    }

    private void pop() {
        View view = LayoutInflater.from(this).inflate(R.layout.check_sn,null);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void a1(UserNameExistMsg msg){
        if (msg.exist) {
            legal = false;
            Toast.makeText(this, getResources().getString(R.string.reg_account_exist), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void a1(RegistMsg msg){
        if (msg.code.equals("0")) {
            Toast.makeText(this, getResources().getString(R.string.reg_success), Toast.LENGTH_SHORT).show();
//            InternetUtils.login(account,password);
            finish();
        }else Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginInfo(LoginMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("isFirst",true);
            startActivity(intent);
            CommentUtils.save(this);
            //pop();
        }
    }

    /**
     * 显示国家列表
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void daws(CountryDataMsg msg) {
        disMissProgressDialog();
        if (msg.code.equals("1")) {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            countryList = msg.countryList;
            foreignCityList = msg.cityList;
            CommentUtils.showPickView(this, msg.countryList, country, "请选择国家");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAllCity(AllAreaMsg msg) {
        disMissProgressDialog();
        provinceList = msg.provinceList;
        cityList = msg.cityList;
        areaList = msg.areaList;
        selectCity();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void a2(GetCodeMsg msg) {
        if (msg.code.equals("0")) {
            Toast.makeText(this, getResources().getString(R.string.send_tips), Toast.LENGTH_SHORT).show();
            CODE = msg.data;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void b1(ThirdRegistMsg msg) {
        if (msg.code.equals("0")) {
            Toast.makeText(this, getResources().getString(R.string.reg_success), Toast.LENGTH_SHORT).show();
            finish();
        } else Toast.makeText(this, getResources().getString(R.string.reg_fail), Toast.LENGTH_SHORT).show();
    }

    private void selectCity() {
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(final int options1, final int options2, final int options3, View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String s = provinceList.get(options1);
                        if (s == null) s = "";
                        province.setText(s);
                        s = cityList.get(options1).get(options2);
                        if (s == null) s = "";
                        city.setText(s);
                        s = areaList.get(options1).get(options2).get(options3);
                        if (s == null) s = "";
                        area.setText(s);
                    }
                });
            }
        })
                .setTitleText("请选择城市")
                .setTitleBgColor(0xff032d3a)
                .setTitleColor(0xffffffff)
                .setSubmitColor(0xffffffff)
                .setCancelColor(0xff058ef0)
                .setBgColor(0xff032d3a)
                .setTitleSize(22)
                .setTextColorCenter(0xffffffff)
                .build();
        pvOptions.setPicker(provinceList, cityList, areaList);
        pvOptions.show();
    }
}
