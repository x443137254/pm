package com.growatt.energymanagement.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.activity.EnergyDetailActivity;
import com.growatt.energymanagement.msgs.DevsDetailInfoMsg;

import java.util.List;

/**
 * 能耗-设备管理页面列表数据适配器
 */
public class DeviceClaLisAdapter extends BaseAdapter {

    private Context mContext;
    private List<DevsDetailInfoMsg.Dev> list;
    private String devtype;

    public DeviceClaLisAdapter(Context context, List<DevsDetailInfoMsg.Dev> list, String devtype) {
        this.mContext = context;
        this.list = list;
        this.devtype = devtype;
    }

    public void setList(List<DevsDetailInfoMsg.Dev> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list == null) return 0;
        return list.size();
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_device_classify, parent, false);
            holder = new Holder();
            holder.linearLayout = convertView.findViewById(R.id.status_ic_container);
            holder.status1 = convertView.findViewById(R.id.status_01);
            holder.status2 = convertView.findViewById(R.id.status_02);
            holder.title = convertView.findViewById(R.id.title);
            holder.tem = convertView.findViewById(R.id.temperature);
            holder.onOff = convertView.findViewById(R.id.switch_ic);
            convertView.setTag(holder);
        } else holder = (Holder) convertView.getTag();

        final DevsDetailInfoMsg.Dev dev = list.get(position);
        holder.title.setText(dev.name);
        String s = String.valueOf(dev.roomTemp) + "°";
        holder.tem.setText(s);
        if (dev.online == 1) {
            holder.status2.setText("离线");
            holder.status1.setText("故障");
            holder.status1.setTextColor(0xfff9374e);
            holder.linearLayout.setBackgroundResource(R.drawable.circle_red_bg);
        } else {
            holder.status2.setText("在线");
            if (dev.onoff == 1) {
                holder.status1.setText("待机");
                holder.status1.setTextColor(0xff00b0ea);
                holder.linearLayout.setBackgroundResource(R.drawable.circle_blue_bg);
            }else {
                holder.status1.setText("运行中");
                holder.status1.setTextColor(0xff0fed97);
                holder.linearLayout.setBackgroundResource(R.drawable.circle_green_bg);
            }
        }

        if (dev.onoff == 1) holder.onOff.setImageResource(R.mipmap.list_off);
        else holder.onOff.setImageResource(R.mipmap.list_on);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EnergyDetailActivity.class);
                intent.putExtra("devType",devtype);
                intent.putExtra("devId",dev.devId);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    private class Holder {
        private LinearLayout linearLayout;
        private TextView status1;
        private TextView status2;
        private TextView title;
        private TextView tem;
        private ImageView onOff;
    }
}
