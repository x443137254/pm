package com.tuolve.powermanager.msgs;

import android.view.LayoutInflater;

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
                bean.name = jsonItem.optString("name");
                bean.datalog_sn = jsonItem.optString("datalog_sn");
                bean.type = jsonItem.optString("type");
                bean.inverterId = jsonItem.optString("inverterId");
                devList.add(bean);
            }
        }
    }

    public class DevBean{
        public String name;
        public String datalog_sn;
        public String type;
        public String inverterId;
    }
}
