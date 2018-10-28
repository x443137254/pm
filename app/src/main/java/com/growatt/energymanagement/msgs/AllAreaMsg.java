package com.growatt.energymanagement.msgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/27.
 *
 */

public class AllAreaMsg {

    public List<CityInfo> list;

    public AllAreaMsg(String s) {
        JSONArray array = null;
        try {
            array = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (array == null) return;
        list = new ArrayList<>();
        CityInfo info;
        JSONObject jsonObject;
        for (int i = 0; i < array.length(); i++) {
            info = new CityInfo();
            jsonObject = array.optJSONObject(i);
            info.name = jsonObject.optString("name");
            info.adCode = jsonObject.optString("adCode");
            list.add(info);
        }
    }

    public class CityInfo {
        public String name;
        public String adCode;
    }
}
