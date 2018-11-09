package com.growatt.energymanagement.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.adapters.DeviceClaLisAdapter;

/**
 * Created by Administrator on 2018/9/11
 */

public class DeviceManageFragment extends Fragment {
    private EnergyFragment energyFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_manage,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fl, energyFragment).commit();
            }
        });
        ((ListView)view.findViewById(R.id.device_classify_list)).setAdapter(new DeviceClaLisAdapter(getContext(),null,""));
    }

    public void setBack(EnergyFragment energyFragment){
        this.energyFragment = energyFragment;
    }
}
