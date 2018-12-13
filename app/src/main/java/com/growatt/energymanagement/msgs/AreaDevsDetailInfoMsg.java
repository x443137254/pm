package com.growatt.energymanagement.msgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wecc on 2018/12/12
 */

public class AreaDevsDetailInfoMsg {

    public String code;
    public String errMsg;
    public List<DevsDetailInfo> allDev;
    public List<DevsDetailInfo> runningDev;
    public List<DevsDetailInfo> errorDev;
    public List<DevsDetailInfo> sleepingDev;
    public int totalEle;

    public AreaDevsDetailInfoMsg(String s) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) return;
        code = jsonObject.optString("code");
        if (code.equals("1")) {
            errMsg = jsonObject.optString("data");
        } else {
            JSONObject data = jsonObject.optJSONObject("data");
            if (data == null) return;
            totalEle = data.optInt("totalEle");
            JSONArray all =  data.optJSONArray("all");
            JSONArray running =  data.optJSONArray("running");
            JSONArray error =  data.optJSONArray("error");
            JSONArray sleeping =  data.optJSONArray("sleeping");
            allDev = parsData(all);
            runningDev = parsData(running);
            errorDev = parsData(error);
            sleepingDev = parsData(sleeping);
        }
    }

    private List<DevsDetailInfo> parsData(JSONArray array){
        List<DevsDetailInfo> list = new ArrayList<>();
        if (array == null) return list;
        DevsDetailInfo info;
        JSONObject jsonObject;
        for (int i = 0; i < array.length(); i++) {
            jsonObject = array.optJSONObject(i);
            info = new DevsDetailInfo();
            info.name = jsonObject.optString("name");
            info.path = jsonObject.optString("path");
            info.devId = jsonObject.optString("devId");
            info.mode = jsonObject.optInt("mode");
            info.temp = jsonObject.optInt("temp");
            info.online = jsonObject.optInt("online");
            info.totalEle = jsonObject.optInt("totalEle");
            info.wind = jsonObject.optInt("wind");
            info.onoff = jsonObject.optInt("onoff");
            list.add(info);
        }
        return list;
    }

    public class DevsDetailInfo {
        public int mode;
        public int temp;
        public int online;
        public int totalEle;
        public int wind;
        public int onoff;
        public String name;
        public String path;
        public String devId;
    }
}
