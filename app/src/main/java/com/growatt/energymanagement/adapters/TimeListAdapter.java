package com.growatt.energymanagement.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/9/18.
 *
 */

public class TimeListAdapter extends BaseAdapter {
    private Context mContext;

    public TimeListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 144;
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
        TextView textView;
        if (convertView == null) {
            textView = new TextView(mContext);
            ViewGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(0xffffffff);
            textView.setTextSize(18);
        }else {
            textView = (TextView) convertView;
        }
        int h = position / 6;
        int m = position % 6;
        String s;
        if (h < 10) s = "0" +  String.valueOf(h);
        else s = String.valueOf(h);
        String s1 = s + ":" + String.valueOf(m) + "0";
        textView.setText(s1);
        return textView;
    }
}
