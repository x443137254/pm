package com.growatt.energymanagement.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.adapters.NoticeListAdapter;
import com.growatt.energymanagement.adapters.WarmListAdapter;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.NoticeInfoMsg;
import com.growatt.energymanagement.msgs.NoticeListMsg;
import com.growatt.energymanagement.msgs.NoticeMsg;
import com.growatt.energymanagement.utils.InternetUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class MsgFragment extends Fragment implements View.OnClickListener {

    private HomeFragment homeFragment;
    private LinearLayout msgDetail;
    private ConstraintLayout reportBar;
    private ConstraintLayout warnBar;
    private WarmListAdapter warmListAdapter;
    private ListView warnList;
    private View reportView;
    private View warnDetailView;

    private TextView sn;
    private TextView name;
    private TextView fault_num;
    private TextView fault_value;
    private TextView time;
    private TextView detail;
    private TextView suggest;

    public MsgFragment() {
    }

    public void setBackData(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_msg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        msgDetail = view.findViewById(R.id.msg_detail);
        view.findViewById(R.id.msg_back).setOnClickListener(this);
        reportBar = view.findViewById(R.id.report_item);
        warnBar = view.findViewById(R.id.warn_item);
        reportBar.setOnClickListener(this);
        warnBar.setOnClickListener(this);

        InternetUtils.noticeList(LoginMsg.uniqueId, 0);
        warnList = new ListView(getContext());
        warnList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        warmListAdapter = new WarmListAdapter(getContext(), null);
        warnList.setAdapter(warmListAdapter);
        msgDetail.removeAllViews();
        msgDetail.addView(warnList);

        reportView = LayoutInflater.from(getContext()).inflate(R.layout.layout_report_detail, msgDetail, false);
        warnDetailView = LayoutInflater.from(getContext()).inflate(R.layout.layout_warm_detail, msgDetail, false);
        sn = warnDetailView.findViewById(R.id.sn);
        name = warnDetailView.findViewById(R.id.name);
        fault_num = warnDetailView.findViewById(R.id.fault_num);
        fault_value = warnDetailView.findViewById(R.id.fault_value);
        time = warnDetailView.findViewById(R.id.time);
        detail = warnDetailView.findViewById(R.id.detail);
        suggest = warnDetailView.findViewById(R.id.suggest);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.msg_back:
                FragmentManager manager = getFragmentManager();
                if (manager != null) {
                    manager.beginTransaction().replace(R.id.fl, homeFragment).commitAllowingStateLoss();
                }
                break;
            case R.id.report_item:
                reportBar.setBackgroundColor(0xff07455a);
                warnBar.setBackgroundColor(0x0007455a);
                msgDetail.removeAllViews();
                msgDetail.addView(reportView);
                break;
            case R.id.warn_item:
                warnBar.setBackgroundColor(0xff07455a);
                reportBar.setBackgroundColor(0x0007455a);
                msgDetail.removeAllViews();
                msgDetail.addView(warnList);
                InternetUtils.noticeList(LoginMsg.uniqueId, 0);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showWarnNum(NoticeListMsg msg) {
        if (msg.code.equals("0") && msg.list != null) {
            if (warmListAdapter != null) warmListAdapter.setList(msg.list);
        } else Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getData(NoticeInfoMsg msg){
        if (msg.code.equals("1")){
            Toast.makeText(getContext(), msg.errMsg, Toast.LENGTH_SHORT).show();
        }else {
            msgDetail.removeAllViews();
            msgDetail.addView(warnDetailView);
            sn.setText(msg.sn);
            name.setText(changeName(msg.devType));
            fault_value.setText(String.valueOf(msg.event));
            time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"
                    ,getResources().getConfiguration().locale).format(new Date(msg.time)));
            suggest.setText(msg.solution);
            detail.setText(msg.name);
        }
    }

    private String changeName(String s){
        if (s.equals("ammeter")) return "电表";
        else return "逆变器";
    }
}
