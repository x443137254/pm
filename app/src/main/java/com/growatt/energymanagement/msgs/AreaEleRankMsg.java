package com.growatt.energymanagement.msgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/23.
 *
 */

public class AreaEleRankMsg {

    public String code;
    public String errMsg;
    public List<AreaEleRankBean> list;

    public AreaEleRankMsg(String s) {
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
            JSONArray array = jsonObject.optJSONArray("data");
            if (array == null) return;
            list = new ArrayList<>();
            AreaEleRankBean bean;
            JSONObject json;
            for (int i = 0; i < array.length(); i++) {
                bean = new AreaEleRankBean();
                json = array.optJSONObject(i);
                bean.devType = json.optString("devType");
                bean.areaName = json.optString("areaName");
                bean.sn = json.optString("sn");
                bean.time = json.optString("time");
                bean.devName = json.optString("devName");
                bean.areaPath = json.optString("areaPath");
                bean.ele = json.optDouble("ele");
                list.add(bean);
            }
        }
    }

    public class AreaEleRankBean{
        public String devType;
        public String areaName;
        public String sn;
        public String time;
        public String devName;
        public String areaPath;
        public double ele;
    }
}
