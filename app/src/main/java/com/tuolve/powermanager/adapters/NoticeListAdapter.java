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
import com.tuolve.powermanager.activity.ReportDetailActivity;
import com.tuolve.powermanager.activity.WarnListActivity;
import com.tuolve.powermanager.msgs.NoticeMsg;

import org.greenrobot.eventbus.EventBus;

/**
 * 通知列表数据适配器
 */
public class NoticeListAdapter extends BaseAdapter {
    private Context mContext;

    public NoticeListAdapter(Context mContext) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_notice,parent,false);
        ImageView ic = view.findViewById(R.id.notice_item_ic);
        TextView title = view.findViewById(R.id.notice_item_title);
        TextView msg = view.findViewById(R.id.notice_item_msg);
        TextView time = view.findViewById(R.id.notice_item_time);
        if (position == 0) {
            ic.setImageResource(R.mipmap.notice_01);
            title.setText("7月耗电报表");
            msg.setText("上月累计用电：876543kWh");
            time.setText("09:30");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MainActivity.isPad){
                        NoticeMsg msg = new NoticeMsg(0);
                        EventBus.getDefault().post(msg);
                    }else mContext.startActivity(new Intent(mContext, ReportDetailActivity.class));
                }
            });
        }else {
            ic.setImageResource(R.mipmap.notice_02);
            title.setText("告警消息");
            msg.setText("逆变器告警");
            time.setText("7月28日");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MainActivity.isPad){
                        NoticeMsg msg = new NoticeMsg(1);
                        EventBus.getDefault().post(msg);
                    }else mContext.startActivity(new Intent(mContext, WarnListActivity.class));
                }
            });
        }
        return view;
    }
}
