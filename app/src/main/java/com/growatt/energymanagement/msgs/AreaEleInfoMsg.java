package com.growatt.energymanagement.msgs;

import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/26.
 *
 */

public class AreaEleInfoMsg {

    public String code;
    public String errMsg;
    public int timeType;
    public List<Entry> list;

    public AreaEleInfoMsg(String s, int timeType) {
        this.timeType = timeType;
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
            Entry entry;
            JSONObject data;
            for (int i = 0; i < array.length(); i++) {
                data = array.optJSONObject(i);
                entry = new Entry();
                entry.setY((float) data.optDouble("ele"));
                String time ;
                if (timeType == 4){
                    time = data.optString("ctime");
                }else{
                    time = data.optString("time");
                }
                entry.setX(Integer.parseInt(time.substring(time.length() - 2)));
                list.add(entry);
            }
        }
    }
}
