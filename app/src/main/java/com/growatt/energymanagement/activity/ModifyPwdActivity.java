package com.growatt.energymanagement.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.UpdateUserMsg;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 个人-个人资料-修改密码
 */
public class ModifyPwdActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.modify_pwd_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final EditText originalPwd = findViewById(R.id.original_pwd);
        final EditText newPwd = findViewById(R.id.new_pwd);
        final EditText confirmPwd = findViewById(R.id.confirm_pwd);
        findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = originalPwd.getText().toString();
                if (!s.equals(LoginMsg.password)){
                    Toast.makeText(ModifyPwdActivity.this, "原密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                String s1 = newPwd.getText().toString();
                String s2 = confirmPwd.getText().toString();
                if (s1.equals(s2)){
                    InternetUtils.updateUser("password",s1);
                    finish();
                }else {
                    Toast.makeText(ModifyPwdActivity.this, "2次密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sda(UpdateUserMsg msg){
        if (msg.code.equals("0")){
            Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        }
    }
}
