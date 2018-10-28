package com.growatt.energymanagement.msgs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/24.
 *
 */

public class DevTypeEleCostMsg {

    public String code;
    public String errMsg;
    public List<Map<String, Integer>> list;

    public DevTypeEleCostMsg(String s) {
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
            Iterator<String> keys = data.keys();
            if (keys == null) return;
            list = new ArrayList<>();
            Map<String, Integer> map;
            while (keys.hasNext()) {
                map = new HashMap<>();
                String key = keys.next();
                map.put(key,data.optJSONObject(key).optInt("ele"));
                list.add(map);
            }
        }
    }
}
