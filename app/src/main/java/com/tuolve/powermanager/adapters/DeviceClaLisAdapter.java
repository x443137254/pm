package com.tuolve.powermanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuolve.powermanager.R;
import com.tuolve.powermanager.activity.EnergyDetailActivity;

/**
 *能耗-设备管理页面列表数据适配器
 */
public class DeviceClaLisAdapter extends BaseAdapter {
    private Context mContex;

    public DeviceClaLisAdapter(Context mContex) {
        this.mContex = mContex;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContex).inflate(R.layout.list_item_device_classify,parent,false);
            holder = new Holder();
            holder.linearLayout = convertView.findViewById(R.id.status_ic_container);
            holder.status1 = convertView.findViewById(R.id.status_01);
            holder.status2 = convertView.findViewById(R.id.status_02);
            holder.title = convertView.findViewById(R.id.title);
            holder.tem = convertView.findViewById(R.id.temperature);
            holder.onOff = convertView.findViewById(R.id.switch_ic);
            convertView.setTag(holder);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContex.startActivity(new Intent(mContex, EnergyDetailActivity.class));
                }
            });
        }else holder = (Holder) convertView.getTag();


        return convertView;
    }

    private class Holder{
        private LinearLayout linearLayout;
        private TextView status1;
        private TextView status2;
        private TextView title;
        private TextView tem;
        private ImageView onOff;
    }
}
