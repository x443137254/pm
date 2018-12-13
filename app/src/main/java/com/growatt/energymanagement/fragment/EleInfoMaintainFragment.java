package com.growatt.energymanagement.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.growatt.energymanagement.R;
import com.growatt.energymanagement.msgs.CountryDataMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.PowerStationMsg;
import com.growatt.energymanagement.utils.CommentUtils;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/9/10.
 *
 */

public class EleInfoMaintainFragment extends Fragment implements View.OnClickListener {

    private TextView country;
    private TextView subsidy;
    private TextView latitude;
    private TextView designCompany;
    private TextView name;
    private TextView setDate;
    private TextView power;
    private TextView timeArea;
    private TextView city;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        InternetUtils.powerStation(LoginMsg.uniqueId);
        return inflater.inflate(R.layout.fragment_info_maintain_pad,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        country = view.findViewById(R.id.station);
        subsidy = view.findViewById(R.id.subsidy);
        latitude = view.findViewById(R.id.Latitude);
        designCompany = view.findViewById(R.id.designCompany);
        name = view.findViewById(R.id.name);
        setDate = view.findViewById(R.id.setDate);
        power = view.findViewById(R.id.power);
        timeArea = view.findViewById(R.id.timeArea);
        city = view.findViewById(R.id.city);
        view.findViewById(R.id.install_date).setOnClickListener(this);
        view.findViewById(R.id.country).setOnClickListener(this);
        view.findViewById(R.id.city_item).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

//    private void selectCity() {
//        RegionUtil regionUtil = new RegionUtil(getContext());
//        final List<String> provinceList = regionUtil.getProvinceList();
//        final List<List<String>> citesList = new ArrayList<>();
//        for (int i = 0; i < provinceList.size(); i++) {
//            citesList.add(regionUtil.getCityList(i));
//        }
//        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
//            @Override
//            public void onOptionsSelect(int options1, int options2, int options3, View v) {
//                final String tx = citesList.get(options1).get(options2);
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        city.setText(tx);
//                    }
//                });
//            }
//        })
//                .setTitleText("请选择城市")
//                .setTitleBgColor(0xff032d3a)
//                .setTitleColor(0xffffffff)
//                .setSubmitColor(0xffffffff)
//                .setCancelColor(0xff058ef0)
//                .setBgColor(0xff032d3a)
//                .setTitleSize(22)
//                .setTextColorCenter(0xffffffff)
//                .build();
//        pvOptions.setPicker(provinceList,citesList);
//        pvOptions.show();
//    }

    private void selectTime() {
        TimePickerView pvTime = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
                setDate.setText(format.format(date));
            }
        })
                .setTitleText("请选择")
                .setSubmitColor(0xffffffff)
                .setBackgroundId(0x00000000)
                .setBgColor(0xff032D3B)
                .setTitleBgColor(0xff032D3B)
                .setTitleColor(0xffffffff)
                .setTextColorCenter(0xffffffff)
                .setTextColorOut(0x66ffffff)
                .build();

        pvTime.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void daws(CountryDataMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            CommentUtils.showPickView(getActivity(), msg.countryList, country, "请选择国家");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void das(PowerStationMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            String temp;
            name.setText(msg.name);
            setDate.setText(msg.setDate);
            designCompany.setText(msg.designCompany);
            temp = msg.power + "W";
            power.setText(temp);
            country.setText(msg.country);
            city.setText(msg.city);
            temp = "GMT" + msg.timeArea;
            timeArea.setText(temp);
            temp = msg.latitude + "\n" + msg.longitude;
            latitude.setText(temp);
            temp = "￥" + msg.subsidy;
            subsidy.setText(temp);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.install_date:
                selectTime();
                break;
            case R.id.country:
                InternetUtils.countryData();
                break;
            case R.id.city_item:
                //selectCity();
                break;
        }
    }
}
