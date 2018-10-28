package com.tuolve.powermanager.msgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/24.
 *
 */

public class AreaDevsStateMsg {

    public String code;
    public String errMsg;
    public List<ConsumeItem> list;

    public AreaDevsStateMsg(String s) {
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
            ConsumeItem item;
            JSONObject jsonItem;
            for (int i = 0; i < array.length(); i++) {
                item = new ConsumeItem();
                jsonItem = array.optJSONObject(i);
                item.areaName = jsonItem.optString("areaName");
                item.path = jsonItem.optString("path");
                item.num_all = jsonItem.optInt("num_all");
                item.ele_cost = jsonItem.optInt("ele_cost");
                item.num_running = jsonItem.optInt("num_running");
                list.add(item);
            }
        }
    }

    public class ConsumeItem{
        public String areaName;
        public String path;
        public int num_all;
        public int num_running;
        public int ele_cost;
    }
}
