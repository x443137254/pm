package com.growatt.energymanagement.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.growatt.energymanagement.activity.MainActivity;
import com.growatt.energymanagement.R;
import com.growatt.energymanagement.activity.WarmDetailActivity;
import com.growatt.energymanagement.msgs.NoticeListMsg;
import com.growatt.energymanagement.msgs.NoticeMsg;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 告警列表数据适配器
 */
public class WarmListAdapter extends BaseAdapter {

    private Context mContext;
    private List<NoticeListMsg.NoticeListBean> list;

    public WarmListAdapter(Context context, List<NoticeListMsg.NoticeListBean> list) {
        this.mContext = context;
        this.list = list;
    }

    public void setList(List<NoticeListMsg.NoticeListBean> list) {
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
        NoticeListHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_warm, parent, false);
            holder = new NoticeListHolder();
            holder.ic = convertView.findViewById(R.id.warm_item_ic);
            holder.time = convertView.findViewById(R.id.warm_item_time);
            holder.title = convertView.findViewById(R.id.warm_item_title);
            holder.sn = convertView.findViewById(R.id.warm_item_sn);
            holder.num = convertView.findViewById(R.id.warm_item_num);
            convertView.setTag(holder);
        }else holder = (NoticeListHolder) convertView.getTag();

        final NoticeListMsg.NoticeListBean bean = list.get(position);
        holder.ic.setImageResource(R.mipmap.notice_02);
        holder.time.setText(new SimpleDateFormat("yyyy年MM月dd日 HH:mm"
                ,mContext.getResources().getConfiguration().locale)
                .format(new Date(bean.time)));
        holder.title.setText(changeName(bean.type));
        holder.sn.setText(bean.sn);
        holder.num.setText(String.valueOf(bean.event));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isPad) {
                    InternetUtils.noticeInfo("warning",bean.id,bean.type);
                } else {
                    Intent intent = new Intent(mContext, WarmDetailActivity.class);
                    intent.putExtra("cid",bean.id);
                    intent.putExtra("devType",bean.type);
                    mContext.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    public class NoticeListHolder {
        public ImageView ic;
        public TextView time;
        public TextView title;
        public TextView sn;
        public TextView num;
    }
    private String changeName(String s){
        if (s.equals("ammeter")) return "电表";
        else return "逆变器";
    }
}
