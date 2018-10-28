package com.growatt.energymanagement.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.activity.PriceConfDetailActivity;

/**
 * Created by Administrator on 2018/9/10.
 */

public class PriceConfFragment extends Fragment {

    private LinearLayout confList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_price_conf_pad,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.price_conf_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        confList = view.findViewById(R.id.price_conf_list);
        addConfItem("平时电价","¥0.91/kWh","18:00-23:59","永久有效","是");
        addConfItem("谷时电价","¥0.91/kWh","18:00-23:59","永久有效","是");
        addConfItem("尖时电价","¥0.91/kWh","18:00-23:59","永久有效","是");
        addConfItem("峰时电价","¥0.91/kWh","18:00-23:59","永久有效","是");
    }

    private void addItem() {
        Context context = getContext();
        if (context == null) return;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_price_pad,null);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        dialog.show();
    }

    private void addConfItem(String title, String price, String startTime, String  validTime, String isValid) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_price_conf_pad,confList,false);
        TextView titleView = view.findViewById(R.id.title);
        TextView priceView = view.findViewById(R.id.price);
        TextView startTimeView = view.findViewById(R.id.start_time);
        TextView validTimeView = view.findViewById(R.id.valid_time);
        TextView isValidView = view.findViewById(R.id.is_valid);
        titleView.setText(title);
        priceView.setText(price);
        startTimeView.setText(startTime);
        validTimeView.setText(validTime);
        isValidView.setText(isValid);
        confList.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PriceConfDetailActivity.class));
            }
        });
    }
}
