package com.tuolve.powermanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.tuolve.powermanager.R;

import java.util.List;

/**
 * 能耗-电量详情 列表数据适配器
 */
public class EnergyDetailListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Entry> list;
    private int type;
    private float max;

    public EnergyDetailListAdapter(Context mContext, int type) {
        this.mContext = mContext;
        this.type = type;


    }

    public void setList(List<Entry> list) {
        this.list = list;
        if (list != null && list.size() > 1) {
            max = list.get(0).getY();
            for (int i = 0; i < list.size(); i++) {
                float y = list.get(i).getY();
                if (y > max) max = y;
            }
        }
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_consume_detail, parent, false);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.zone_consume_list_title);
            holder.bar = convertView.findViewById(R.id.zone_consume_list_item);
            holder.data = convertView.findViewById(R.id.zone_consume_list_data);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        String s = (int) list.get(position).getX() + "";
        if (type == 3) {
            s = s + "日";
        } else {
            s = s + "月";
        }
        holder.title.setText(s);
        float y = list.get(position).getY();
        s = (int) y + "kWh";
        holder.data.setText(s);
        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.5 * y / max);
        holder.bar.setWidth(width);
        return convertView;
    }

    private class ViewHolder {
        private TextView title;
        private TextView bar;
        private TextView data;
    }
}
