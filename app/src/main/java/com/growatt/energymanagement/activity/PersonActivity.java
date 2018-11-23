package com.growatt.energymanagement.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.growatt.energymanagement.R;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.utils.CommentUtils;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 个人-个人资料
 */
public class PersonActivity extends BasicActivity implements View.OnClickListener {

    private TextView account;
    private TextView nick;
    private TextView company;
    private TextView regTime;
    private TextView phone;
    private TextView email;
    private final int MODIFY_NICK = 100;
    private final int MODIFY_COMPANY = 101;
    private final int MODIFY_PHONE = 102;
    private final int MODIFY_EMAIL = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.person_back).setOnClickListener(this);
        findViewById(R.id.person_modify_name).setOnClickListener(this);
        findViewById(R.id.person_modify_pwd).setOnClickListener(this);
        findViewById(R.id.person_modify_phone).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);
//        findViewById(R.id.company_edit).setOnClickListener(this);
        findViewById(R.id.modify_email).setOnClickListener(this);
        account = findViewById(R.id.account);
        nick = findViewById(R.id.nick);
        company = findViewById(R.id.company);
        regTime = findViewById(R.id.reg_time);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);

        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        String uniqueId = sp.getString("uniqueId", "");
        if (uniqueId.equals("")) return;
        InternetUtils.login(sp.getString("account",""),sp.getString("password",""));
    }


    private String getDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", getResources().getConfiguration().locale);
        Date date = new Date(time);
        return format.format(date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_back:
                finish();
                break;
            case R.id.person_modify_name:
                Intent intent = new Intent(this, ModifyActivity.class);
                intent.putExtra("key", "nick");
                startActivityForResult(intent, MODIFY_NICK);
                break;
            case R.id.person_modify_pwd:
                startActivity(new Intent(this, ModifyPwdActivity.class));
                break;
            case R.id.person_modify_phone:
                startActivity(new Intent(this, ModifyPhoneActivity.class));
                break;
            case R.id.logout:
                LoginMsg.cleanUserInfo();
                CommentUtils.save(this);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.company_edit:
                Intent intent1 = new Intent(this, ModifyActivity.class);
                intent1.putExtra("key", "company");
                startActivityForResult(intent1, MODIFY_COMPANY);
                break;
            case R.id.modify_email:
                startActivityForResult(new Intent(this,ModifyActivity.class),MODIFY_EMAIL);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 1000) return;
        String s = data.getStringExtra("data");
        switch (requestCode) {
            case MODIFY_NICK:
                nick.setText(s);
                InternetUtils.updateUser("nick",s);
                break;
            case MODIFY_COMPANY:
                company.setText(s);
                InternetUtils.updateUser("companyName",s);
                break;
            case MODIFY_EMAIL:
                email.setText(s);
                InternetUtils.updateUser("email",s);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginInfo(LoginMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            account.setText(LoginMsg.account);
            company.setText(LoginMsg.companyName);
            regTime.setText(getDate(LoginMsg.registTime));
            nick.setText(LoginMsg.nick);
            phone.setText(LoginMsg.phone);
            email.setText(LoginMsg.email);
        }
    }
}
