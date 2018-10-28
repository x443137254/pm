package com.tuolve.powermanager.activity;

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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tuolve.powermanager.R;
import com.tuolve.powermanager.msgs.GetCodeMsg;
import com.tuolve.powermanager.msgs.LoginMsg;
import com.tuolve.powermanager.msgs.ThirdRegistMsg;
import com.tuolve.powermanager.msgs.UserNameExistMsg;
import com.tuolve.powermanager.utils.CommentUtils;
import com.tuolve.powermanager.utils.InternetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

public class ThirdRegActivity extends BasicActivity implements View.OnClickListener {

    private EditText phone;
    private EditText phone_code;
    private EditText pwd;
    private EditText companyName;
    private EditText address;
    private EditText install_sn;
    private TextView getCode;
    private TextView country;
    private TextView province;
    private TextView city;
    private TextView area;
    private TextView language;
    private ImageView country_ic;
    private ImageView province_ic;
    private ImageView city_ic;
    private ImageView area_ic;
    private ImageView language_ic;
    private CheckBox agreement;
    private boolean legal = true;
    private String account;
    private String password;
    private String company;
    private String addr;
    private String userType;
    private String phoneCode;
    private Timer timer;
    private int countDown = 60;
    private String CODE = "";

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
        pwd = findViewById(R.id.pwd);
        companyName = findViewById(R.id.company);
        address = findViewById(R.id.address);
        install_sn = findViewById(R.id.sn);
        getCode = findViewById(R.id.get_code);
        country = findViewById(R.id.country);
        province = findViewById(R.id.province);
        city = findViewById(R.id.city);
        area = findViewById(R.id.area);
        language = findViewById(R.id.language);
        country_ic = findViewById(R.id.country_ic);
        province_ic = findViewById(R.id.province_ic);
        city_ic = findViewById(R.id.city_ic);
        area_ic = findViewById(R.id.area_ic);
        language_ic = findViewById(R.id.language_ic);
        agreement = findViewById(R.id.agreement);
        findViewById(R.id.reg_back).setOnClickListener(this);
        findViewById(R.id.next_step).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        country_ic.setOnClickListener(this);
        province_ic.setOnClickListener(this);
        city_ic.setOnClickListener(this);
        area_ic.setOnClickListener(this);
        language_ic.setOnClickListener(this);
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
    }

    private void checkLegal() {
        account = phone.getText().toString();
        if (account.equals("") || account.equals("null")) {
            legal = false;
            Toast.makeText(this, getResources().getString(R.string.reg_account_empty), Toast.LENGTH_SHORT).show();
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

        phoneCode = phone_code.getText().toString();
        if (phoneCode.equals("") || phoneCode.equals("null")) {
            legal = false;
            Toast.makeText(this, getResources().getString(R.string.phone_code_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        company = companyName.getText().toString();
        if (company.equals("") || company.equals("null")) {
            legal = false;
            Toast.makeText(this, getResources().getString(R.string.reg_company_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        addr = address.getText().toString();
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
    }

    private void pop() {
        View view = LayoutInflater.from(this).inflate(R.layout.check_sn, null);
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
        } else {
            new AlertDialog.Builder(this).setView(view).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void a1(UserNameExistMsg msg) {
        if (msg.exist) {
            legal = false;
            Toast.makeText(this, getResources().getString(R.string.reg_account_exist), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void b1(ThirdRegistMsg msg) {
        if (msg.code.equals("0")) {
            Toast.makeText(this, getResources().getString(R.string.reg_success), Toast.LENGTH_SHORT).show();
            InternetUtils.login(account,password);
        } else Toast.makeText(this, getResources().getString(R.string.reg_fail), Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginInfo(LoginMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            CommentUtils.save(this);
            pop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reg_back:
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
                InternetUtils.thirdRegist(appType,thirdUnique,account,account,company,country.getText().toString(),language.getText().toString(),addr,userType);
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
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void a2(GetCodeMsg msg) {
        if (msg.code.equals("0")) {
            Toast.makeText(this, getResources().getString(R.string.send_tips), Toast.LENGTH_SHORT).show();
            CODE = msg.data;
        }
    }

}
