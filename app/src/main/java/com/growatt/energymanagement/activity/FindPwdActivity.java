package com.growatt.energymanagement.activity;

import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.growatt.energymanagement.R;


/**
 * 找回密码
 */
public class FindPwdActivity extends BasicActivity implements View.OnClickListener {

    private RelativeLayout relativeLayout;
    private Animation leftIn;
    private Animation leftOut;
    private Animation rightIn;
    private Animation rightOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.find_pwd_back).setOnClickListener(this);
        TabLayout tablayout = findViewById(R.id.find_pwd_tab);
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.find_pwd_back:
                finish();
                break;
        }
    }

    public void switchWay(String s){
        if (s == null || s.equals("") || s.equals("null")) return;
        View newView;
        View oldView = relativeLayout.getChildAt(0);
        if (s.equals("通过手机号找回")){
            newView = LayoutInflater.from(this).inflate(R.layout.find_pwd_by_phone,relativeLayout,false);
            oldView.setAnimation(rightOut);
            newView.setAnimation(leftIn);
            rightOut.start();
            leftIn.start();
        }else {
            newView = LayoutInflater.from(this).inflate(R.layout.find_pwd_by_mailbox,relativeLayout,false);
            oldView.setAnimation(leftOut);
            newView.setAnimation(rightIn);
            leftOut.start();
            rightIn.start();
        }
        relativeLayout.removeAllViews();
        relativeLayout.addView(newView);
    }
}
