package com.tuolve.powermanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuolve.powermanager.activity.MainActivity;
import com.tuolve.powermanager.R;
import com.tuolve.powermanager.activity.WarmDetailActivity;
import com.tuolve.powermanager.msgs.NoticeMsg;

import org.greenrobot.eventbus.EventBus;

/**
 * 告警列表数据适配器
 */
public class WarmListAdapter extends BaseAdapter {
    private Context mContext;

    public WarmListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return 2;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_warm,parent,false);
        ImageView ic = view.findViewById(R.id.warm_item_ic);
        TextView time = view.findViewById(R.id.warm_item_time);
        TextView title = view.findViewById(R.id.warm_item_title);
        TextView sn = view.findViewById(R.id.warm_item_sn);
        TextView num = view.findViewById(R.id.warm_item_num);
        if (position == 0) {
            ic.setImageResource(R.mipmap.notice_02);
            time.setText("02:31");
            title.setText("逆变器");
            sn.setText("机器编号：AD5697");
            num.setText("故障码：123");
        }else {
            ic.setImageResource(R.mipmap.notice_02);
            time.setText("2018年7月28日02:31");
            title.setText("逆变器");
            sn.setText("机器编号：AD5697");
            num.setText("故障码：123");
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isPad){
                    EventBus.getDefault().post(new NoticeMsg(-1));
                }else mContext.startActivity(new Intent(mContext, WarmDetailActivity.class));
            }
        });
        return view;
    }
}
