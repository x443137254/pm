package com.tuolve.powermanager.msgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/18.
 *
 */

public class GenerateElectricitysMsg {

    public String code;
    public String errMsg;
    public List<Device> list;

    public GenerateElectricitysMsg(String s) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) return;
        code = jsonObject.optString("code");
        if (code.equals("1")){
            errMsg = jsonObject.optString("data");
        }else {
            list = new ArrayList<>();
            Device device;
            JSONArray array = jsonObject.optJSONArray("data");
            JSONObject json;
            for (int i = 0; i < array.length(); i++) {
                json = array.optJSONObject(i);
                device = new Device();
                device.devId = json.optString("devId");
                device.areaId = json.optString("devId");
                device.ele_total = json.optString("ele_total");
                device.areaName = json.optString("areaName");
                device.ele_day = json.optString("ele_day");
                device.datalog_sn = json.optString("datalog_sn");
                device.power = json.optString("power");
                device.installTime = json.optString("installTime");
                device.areaPath = json.optString("areaPath");
                device.devName = json.optString("devName");
                device.status = json.optString("status");
                list.add(device);
            }
        }
    }

    public class Device{
        public String devId;
        public String areaId;
        public String ele_total;
        public String areaName;
        public String ele_day;
        public String datalog_sn;
        public String power;
        public String installTime;
        public String areaPath;
        public String devName;
        public String status;
    }
}
