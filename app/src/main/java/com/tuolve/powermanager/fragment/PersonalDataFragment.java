package com.tuolve.powermanager.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tuolve.powermanager.R;
import com.tuolve.powermanager.msgs.LoginMsg;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/9/10.
 *  个人中心
 */

public class PersonalDataFragment extends Fragment implements View.OnClickListener {

    private TextView account;
    private TextView nick;
    private TextView role;
    private TextView company;
    private TextView regDate;
    private TextView phone;
    private TextView email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person_pad,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.person_modify_name).setOnClickListener(this);
        view.findViewById(R.id.modify_company).setOnClickListener(this);
        view.findViewById(R.id.person_modify_pwd).setOnClickListener(this);
        view.findViewById(R.id.person_modify_phone).setOnClickListener(this);
        view.findViewById(R.id.person_modify_email).setOnClickListener(this);
        account = view.findViewById(R.id.user_name);
        nick = view.findViewById(R.id.nick);
        role = view.findViewById(R.id.role_classify);
        company = view.findViewById(R.id.company);
        regDate = view.findViewById(R.id.reg_date);
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.email);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (LoginMsg.cid != 0){
            account.setText(LoginMsg.account);
            company.setText(LoginMsg.companyName);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(LoginMsg.registTime);
            String s = format.format(date);
            regDate.setText(s);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.person_modify_name:
                break;
            case R.id.modify_company:
                break;
            case R.id.person_modify_pwd:
                break;
            case R.id.person_modify_phone:
                break;
            case R.id.person_modify_email:
                break;
        }
    }
}
