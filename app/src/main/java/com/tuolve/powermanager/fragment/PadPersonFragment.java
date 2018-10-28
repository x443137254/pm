package com.tuolve.powermanager.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tuolve.powermanager.R;
import com.tuolve.powermanager.activity.LoginActivity;
import com.tuolve.powermanager.activity.SettingActivity;
import com.tuolve.powermanager.msgs.LoginMsg;

/**
 * A simple {@link Fragment} subclass.
 */
public class PadPersonFragment extends Fragment implements View.OnClickListener {


    private FragmentManager manager;
    private PersonalDataFragment personalDataFragment;
    private EleInfoMaintainFragment eleInfoMaintainFragment;
    private PriceConfFragment priceConfFragment;
    private LinearLayout item1;
    private LinearLayout item2;
    private LinearLayout item3;

    public PadPersonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_person_pad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        item1 = view.findViewById(R.id.person_item_01);
        item2 = view.findViewById(R.id.person_item_02);
        item3 = view.findViewById(R.id.person_item_03);
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);
        view.findViewById(R.id.logout).setOnClickListener(this);
        view.findViewById(R.id.setting).setOnClickListener(this);
        initFragments();
        manager = getChildFragmentManager();
        if (manager.getFragments().size() == 0) {
            manager.beginTransaction().add(R.id.personal_fragment, personalDataFragment).commit();
            item1.setBackground(getResources().getDrawable(R.drawable.person_bg_pad_left_top));
        }

        if (LoginMsg.cid != 0) {
            TextView account = view.findViewById(R.id.account);
            TextView company = view.findViewById(R.id.company);
            account.setText(LoginMsg.account);
            company.setText(LoginMsg.companyName);
        }
    }

    private void initFragments() {
        personalDataFragment = new PersonalDataFragment();
        eleInfoMaintainFragment = new EleInfoMaintainFragment();
        priceConfFragment = new PriceConfFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_item_01:
                if (LoginMsg.cid == 0) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    FragmentActivity activity = getActivity();
                    if (activity != null) activity.finish();
                }
                manager.beginTransaction().replace(R.id.personal_fragment, personalDataFragment).commit();
                item1.setBackground(getResources().getDrawable(R.drawable.person_bg_pad_left_top));
                item2.setBackgroundColor(0x0007455a);
                item3.setBackgroundColor(0x0007455a);
                break;
            case R.id.person_item_02:
                manager.beginTransaction().replace(R.id.personal_fragment, eleInfoMaintainFragment).commit();
                item1.setBackground(null);
                item2.setBackgroundColor(0xff07455a);
                item3.setBackgroundColor(0x0007455a);
                break;
            case R.id.person_item_03:
                manager.beginTransaction().replace(R.id.personal_fragment, priceConfFragment).commit();
                item1.setBackground(null);
                item2.setBackgroundColor(0x0007455a);
                item3.setBackgroundColor(0xff07455a);
                break;
            case R.id.logout:
                Context context = getContext();
                if (LoginMsg.cid == 0 && context != null) {
                    Resources resources = context.getResources();
                    Toast.makeText(context, resources.getString(R.string.logout_tip), Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    startActivity(new Intent(context, LoginActivity.class));
                    LoginMsg.cleanUserInfo();
                    FragmentActivity activity = getActivity();
                    if (activity != null) activity.finish();
                }
                break;
            case R.id.setting:
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
        }
    }
}
