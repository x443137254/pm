package com.growatt.energymanagement.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.msgs.ForgetPwdMsg;
import com.growatt.energymanagement.msgs.GetCodeMsg;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 找回密码
 */
public class FindPwdActivity extends BasicActivity implements View.OnClickListener {

    private RelativeLayout relativeLayout;
    private Animation leftIn;
    private Animation leftOut;
    private Animation rightIn;
    private Animation rightOut;
    private EditText phone;
    private EditText code;
    private EditText pwd;
    private EditText confirmPwd;
    private Button getCode;
    private int countDown = 60;
    private String CODE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.find_pwd_back).setOnClickListener(this);
        TabLayout tablayout = findViewById(R.id.find_pwd_tab);
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchWay((String) tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        relativeLayout = findViewById(R.id.find_pwd_contain);
        leftIn = AnimationUtils.loadAnimation(this, R.anim.move_in_left);
        leftOut = AnimationUtils.loadAnimation(this, R.anim.move_out_left);
        rightIn = AnimationUtils.loadAnimation(this, R.anim.move_in_right);
        rightOut = AnimationUtils.loadAnimation(this, R.anim.move_out_right);
        findViewById(R.id.confirm).setOnClickListener(this);

        switchWay("通过手机号找回");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find_pwd_back:
                finish();
                break;
            case R.id.confirm:
                String s = pwd.getText().toString();
                if (!s.equals(confirmPwd.getText().toString())) {
                    Toast.makeText(this, "2次密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (CODE.equals(code.getText().toString())) {
                    Toast.makeText(this, "手机验证码不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                InternetUtils.forgetPwd(phone.getText().toString(), s);
                break;
            case R.id.get_code:
                String account = phone.getText().toString();
                if (account.equals("") || account.equals("null")) return;
                InternetUtils.sendCode(account);
                getCode.setEnabled(false);
                final Timer timer = new Timer();
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

    public void switchWay(String s) {
        if (s == null || s.equals("") || s.equals("null")) return;
        View newView;
        View oldView = relativeLayout.getChildAt(0);
        if (s.equals("通过手机号找回")) {
            newView = LayoutInflater.from(this).inflate(R.layout.find_pwd_by_phone, relativeLayout, false);
            oldView.setAnimation(rightOut);
            newView.setAnimation(leftIn);
            rightOut.start();
            leftIn.start();
            phone = newView.findViewById(R.id.phone);
            code = newView.findViewById(R.id.code);
            pwd = newView.findViewById(R.id.pwd);
            confirmPwd = newView.findViewById(R.id.confirm_pwd);
            getCode = newView.findViewById(R.id.get_code);
            getCode.setOnClickListener(this);
        } else {
            newView = LayoutInflater.from(this).inflate(R.layout.find_pwd_by_mailbox, relativeLayout, false);
            oldView.setAnimation(leftOut);
            newView.setAnimation(rightIn);
            leftOut.start();
            rightIn.start();
        }
        relativeLayout.removeAllViews();
        relativeLayout.addView(newView);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void a2(GetCodeMsg msg) {
        if (msg.code.equals("0")) {
            Toast.makeText(this, getResources().getString(R.string.send_tips), Toast.LENGTH_SHORT).show();
            CODE = msg.data;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sdf5(ForgetPwdMsg msg) {
        if (msg.code.equals("0")) {
            Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "密码修改失败", Toast.LENGTH_SHORT).show();
        }
    }
}
