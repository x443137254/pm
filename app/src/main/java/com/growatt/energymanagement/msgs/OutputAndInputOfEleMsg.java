package com.growatt.energymanagement.msgs;

import com.github.mikephil.charting.data.Entry;
import com.growatt.energymanagement.utils.CommentUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/20.
 *
 */

public class OutputAndInputOfEleMsg {

    public String code;
    public String errMsg;
    public List<Entry> list;
    private int timeType;
    private String time;

    public OutputAndInputOfEleMsg(String s, int timeType, String time) {
        this.time = time;
        this.timeType = timeType;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) return;
        code = jsonObject.optString("code" );
        if (code.equals("1")){
            errMsg = jsonObject.optString("data");
        }else {
            JSONArray array = jsonObject.optJSONArray("data");
            if (array == null) return;
            list = new ArrayList<>();
            getData(array);
        }
    }

    private void getData(JSONArray array) {
        if (array == null) return;
        Entry entry;
        JSONObject jsonObject;
        if (timeType == 4) {
            int a = Integer.parseInt(time)-5;
            for (int i = a; i < a + 6; i++) {
                entry = new Entry();
                entry.setX(i);
                for (int j = 0; j < array.length(); j++) {
                    jsonObject = array.optJSONObject(j);
                    if (jsonObject.optInt("Year")==i) {
                        entry.setY((float) jsonObject.optDouble("energy"));
                        break;
                    } else entry.setY(0);
                }
                list.add(entry);
            }
        } else if (timeType == 3){
            for (int i = 1; i <= 12; i++) {
                entry = new Entry();
                entry.setX(i);
                String s = i >= 10 ? time.substring(0,4) + "-" + i : time.substring(0,4) + "-0" + i;
                for (int j = 0; j < array.length(); j++) {
                    jsonObject = array.optJSONObject(j);
                    if (jsonObject.optString("time").equals(s)) {
                        entry.setY(jsonObject.optInt("Energy"));
                        break;
                    } else entry.setY(0);
                }
                list.add(entry);
            }
        }else if (timeType == 2){
            int a = CommentUtils.maxDays(Integer.parseInt(time)/100, Integer.parseInt(time)%100);
            for (int i = 0; i <= a; i++) {
                entry = new Entry();
                entry.setX(i);
                String s = i >= 10 ? time.substring(0,4) + "-" + time.substring(4,6) + "-" + i : time.substring(0,4) + "-" + time.substring(4,6) + "-0" + i;
                for (int j = 0; j < array.length(); j++) {
                    jsonObject = array.optJSONObject(j);
                    if (jsonObject.optString("time").equals(s)) {
                        entry.setY(jsonObject.optInt("energy"));
                        break;
                    } else entry.setY(0);
                }
                list.add(entry);
            }
        }else {
            for (int i = 0; i < 24; i++) {
                entry = new Entry();
                entry.setX(i);
                String s = i >= 10 ? "" + i : "0" + i;
                for (int j = 0; j < array.length(); j++) {
                    jsonObject = array.optJSONObject(j);
                    String time = jsonObject.optString("time");
                    int len = time.length();
                    String temp = time.substring(len-2, len);
                    if (temp.equals(s)) {
                        entry.setY(jsonObject.optInt("power"));
                        break;
                    } else entry.setY(0);
                }
                list.add(entry);
            }
        }
    }
}
