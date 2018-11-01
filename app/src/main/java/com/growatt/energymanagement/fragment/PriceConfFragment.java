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
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.activity.PriceConfDetailActivity;
import com.growatt.energymanagement.msgs.ElePriceInfoMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2018/9/10.
 *
 */

public class PriceConfFragment extends Fragment {

    private LinearLayout confList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (LoginMsg.cid != 0) InternetUtils.elePriceInfo(LoginMsg.uniqueId);
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

    private void addItem() {
        Context context = getContext();
        if (context == null) return;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_price_pad,null);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        dialog.show();
    }

    /**
     * 添加电价配置条目
     * @param title 电价名称
     * @param price 单价
     * @param time 起始时间
     * @param validTime 有效时间
     * @param isValid 是否有效
     */
    private void addConfItem(final int cid,final String title, final String price, final String time, final String  validTime, final String isValid) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_price_conf_pad,confList,false);
        TextView titleView = view.findViewById(R.id.title);
        TextView priceView = view.findViewById(R.id.price);
        TextView startTimeView = view.findViewById(R.id.start_time);
        TextView validTimeView = view.findViewById(R.id.valid_time);
        TextView isValidView = view.findViewById(R.id.is_valid);
        titleView.setText(title);
        priceView.setText(price);
        startTimeView.setText(time);
        validTimeView.setText(validTime);
        isValidView.setText(isValid);
        confList.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PriceConfDetailActivity.class);
                intent.putExtra("priceName",title);
                intent.putExtra("price",price);
                intent.putExtra("time",time);
                intent.putExtra("effTime",validTime);
                intent.putExtra("isEff",isValid);
                intent.putExtra("cid",cid);
                startActivity(intent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showList(ElePriceInfoMsg msg){
        if (msg.code.equals("1")){
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        }else {
            confList.removeAllViews();
            for (int i = 0; i < msg.elePriceList.size(); i++){
                ElePriceInfoMsg.ElePrice elePrice = msg.elePriceList.get(i);
                String validTime = "永久有效";
                if (elePrice.effectiveTime != null && !elePrice.effectiveTime.equals("")) validTime = elePrice.effectiveTime;
                String isValid = "是";
                if (elePrice.status == 1) isValid = "否";
                addConfItem(elePrice.cid,elePrice.name,String.valueOf(elePrice.price),elePrice.timeValue,validTime,isValid);
            }
        }
    }
}
