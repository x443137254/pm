package com.growatt.energymanagement.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.msgs.GetCodeMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.UpdateUserMsg;
import com.growatt.energymanagement.utils.InternetUtils;
import com.umeng.socialize.utils.UmengText;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;


/**
 *
 * 个人-个人资料-修改手机
 */
public class ModifyPhoneActivity extends BasicActivity {

    private String CODE = "";
    private int countDown = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_phone);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.modify_phone_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final EditText originalPhone = findViewById(R.id.original_phone);
        final EditText newPhone = findViewById(R.id.new_phone);
        final EditText code = findViewById(R.id.code);
        final TextView getCode = findViewById(R.id.get_code);
        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CODE.equals(code.getText().toString()) && LoginMsg.phone.equals(originalPhone.getText().toString())){
                    InternetUtils.updateUser("phone",newPhone.getText().toString());
                    finish();
                }
            }
        });
        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = originalPhone.getText().toString();
                if (!s1.equals(LoginMsg.phone)) {
                    Toast.makeText(ModifyPhoneActivity.this, "原手机号输入错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (s1.equals("") || s1.equals("null")) return;
                InternetUtils.sendCode(s1);
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
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sda(UpdateUserMsg msg){
        if (msg.code.equals("0")){
            Toast.makeText(this, "手机修改成功", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
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
