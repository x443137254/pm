package com.tuolve.powermanager.msgs;

import com.github.mikephil.charting.data.Entry;
import com.tuolve.powermanager.utils.CommentUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/25.
 *
 */

public class QualityDataMsg {

    public String code;
    public String errMsg;
    public double power_warm;
    public double current_warm;
    public double voltage1_warm;
    public double voltage2_warm;
    public List<Entry> powerList;
    public List<Entry> currentList;
    public List<Entry> voltage1List;
    public List<Entry> voltage2List;

    public QualityDataMsg(String s) {
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
            JSONObject data = jsonObject.optJSONObject("data");
            if (data == null) return;
            power_warm = data.optDouble("power_warm");
            current_warm = data.optDouble("current_warm");
            voltage1_warm = data.optDouble("voltage1_warm");
            voltage2_warm = data.optDouble("voltage2_warm");
            JSONArray power = data.optJSONArray("power");
            JSONArray current = data.optJSONArray("current");
            JSONArray voltage1 = data.optJSONArray("voltage1");
            JSONArray voltage2 = data.optJSONArray("voltage2");
            Entry entry;
            JSONObject dataItem;
            if (power != null) {
                powerList = new ArrayList<>();
                for (int i = 0; i < power.length(); i++) {
                    dataItem = power.optJSONObject(i);
                    entry = new Entry();
                    entry.setX(parseString(dataItem.optString("time")));
                    entry.setY((float) dataItem.optDouble("coefficient"));
                    powerList.add(entry);
                }
            }
            if (current != null) {
                currentList = new ArrayList<>();
                for (int i = 0; i < current.length(); i++) {
                    dataItem = current.optJSONObject(i);
                    entry = new Entry();
                    entry.setX(parseString(dataItem.optString("time")));
                    entry.setY((float) dataItem.optDouble("coefficient"));
                    currentList.add(entry);
                }
            }
            if (voltage1 != null) {
                voltage1List = new ArrayList<>();
                for (int i = 0; i < voltage1.length(); i++) {
                    dataItem = voltage1.optJSONObject(i);
                    entry = new Entry();
                    entry.setX(parseString(dataItem.optString("time")));
                    entry.setY((float) dataItem.optDouble("coefficient"));
                    voltage1List.add(entry);
                }
            }
            if (voltage2 != null) {
                voltage2List = new ArrayList<>();
                for (int i = 0; i < voltage2.length(); i++) {
                    dataItem = voltage2.optJSONObject(i);
                    entry = new Entry();
                    entry.setX(parseString(dataItem.optString("time")));
                    entry.setY((float) dataItem.optDouble("coefficient"));
                    voltage2List.add(entry);
                }
            }
        }
    }

    private int parseString(String s){
        String numString = CommentUtils.getNumFromString(s);
        return Integer.parseInt(numString.substring(0,2)) * 3600 + Integer.parseInt(numString.substring(2,4)) * 60 + Integer.parseInt(numString.substring(4,6));
    }
}
