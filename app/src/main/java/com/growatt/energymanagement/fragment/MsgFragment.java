package com.growatt.energymanagement.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.adapters.NoticeListAdapter;
import com.growatt.energymanagement.adapters.WarmListAdapter;
import com.growatt.energymanagement.msgs.NoticeMsg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 */
public class MsgFragment extends Fragment {

    private HomeFragment homeFragment;
    private LinearLayout msgDetail;

    public MsgFragment() {
    }

    public void setBackData(HomeFragment homeFragment){
        this.homeFragment = homeFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_msg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView noticeList = view.findViewById(R.id.notice_list);
        msgDetail = view.findViewById(R.id.msg_detail);
        view.findViewById(R.id.msg_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fl, homeFragment).commit();
            }
        });
        noticeList.setAdapter(new NoticeListAdapter(getContext()));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showMsg(NoticeMsg msg){
        View view;
        switch (msg.item){
            case 0:
                view = LayoutInflater.from(getContext()).inflate(R.layout.layout_report_detail,msgDetail,false);
                msgDetail.removeAllViews();
                msgDetail.addView(view);
                break;
            case 1:
                ListView listView = new ListView(getContext());
                listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                listView.setAdapter(new WarmListAdapter(getContext()));
                msgDetail.removeAllViews();
                msgDetail.addView(listView);
                break;
            case -1:
                view = LayoutInflater.from(getContext()).inflate(R.layout.layout_warm_detail,msgDetail,false);
                msgDetail.removeAllViews();
                msgDetail.addView(view);
                break;
        }
    }
}
