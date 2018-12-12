package com.growatt.energymanagement.msgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/10.
 *
 */

public class InvertersMsg {

    public String code;
    public String errMsg;
    public List<DevBean> devList;

    public InvertersMsg(String s) {
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
            devList = new ArrayList<>();
            JSONObject jsonItem;
            DevBean bean;
            for (int i = 0; i < array.length(); i++) {
                jsonItem = array.optJSONObject(i);
                bean = new DevBean();
                bean.devId = jsonItem.optString("devId");
                bean.devType = jsonItem.optString("devType");
                bean.systemType = jsonItem.optInt("systemType");
                devList.add(bean);
            }
        }
    }

    public class DevBean{
        public String devId;
        public String devType;
        public int systemType;
    }
}
