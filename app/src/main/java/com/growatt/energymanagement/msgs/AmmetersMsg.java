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

public class AmmetersMsg {

    public String code;
    public String errMsg;
    public List<AmmeterDev> list;

    public AmmetersMsg(String s) {
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
        }else {
            JSONArray array = jsonObject.optJSONArray("data");
            if (array == null) return;
            list = new ArrayList<>();
            AmmeterDev item;
            JSONObject jsonItem;
            for (int i = 0; i < array.length(); i++) {
                jsonItem = array.optJSONObject(i);
                item = new AmmeterDev();
                item.apath = jsonItem.optString("apath");
                item.devType = jsonItem.optString("devType");
                item.areaName = jsonItem.optString("areaName");
                item.name = jsonItem.optString("name");
                item.sn = jsonItem.optString("sn");
                item.online = jsonItem.optInt("online");
                list.add(item);
            }
        }
    }

    public class AmmeterDev{
        public String devType;
        public String areaName;
        public String name;
        public int online;
        public String sn;
        public String apath;
    }
}
