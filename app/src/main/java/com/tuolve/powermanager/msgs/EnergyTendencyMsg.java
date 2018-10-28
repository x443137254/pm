package com.tuolve.powermanager.msgs;

import com.github.mikephil.charting.data.Entry;
import com.tuolve.powermanager.utils.CommentUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/19.
 *
 */

public class EnergyTendencyMsg {

    public String code;
    public String errMsg;
    public List<Entry> photovoltaicList;
    public List<Entry> chargerList;
    public List<Entry> gridList;
    public List<Entry> costList;

    private int timeType;
    private String time;

    public EnergyTendencyMsg(String s, int timeType, String time) {
        this.timeType = timeType;
        this.time = time;
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
            JSONArray photovoltaic = data.optJSONArray("photovoltaic");
            JSONArray charger = data.optJSONArray("charger");
            JSONArray grid = data.optJSONArray("grid");
            JSONArray cost = data.optJSONArray("photovoltaic");
            photovoltaicList = new ArrayList<>();
            chargerList = new ArrayList<>();
            gridList = new ArrayList<>();
            costList = new ArrayList<>();
            getData(photovoltaic, 1);
            getData(charger, 2);
            getData(grid, 3);
            getData(cost, 4);
        }
    }

    private void getData(JSONArray array, int n) {
        if (array == null) return;
        List<Entry> list = new ArrayList<>();
        Entry entry;
        JSONObject jsonObject;
        if (timeType == 4) {
            int a = Integer.parseInt(time)-5;
            for (int i = a; i < a + 6; i++) {
                entry = new Entry();
                entry.setX(i);
                for (int j = 0; j < array.length(); j++) {
                    jsonObject = array.optJSONObject(j);
                    if (jsonObject.optInt("ctime") == i) {
                        entry.setY(jsonObject.optInt("ele"));
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
                    if (jsonObject.optString("ctime").equals(s)) {
                        entry.setY(jsonObject.optInt("ele"));
                        break;
                    } else entry.setY(0);
                }
                list.add(entry);
            }
        }else {
            int a = CommentUtils.maxDays(Integer.parseInt(time)/100, Integer.parseInt(time)%100);
            for (int i = 0; i <= a; i++) {
                entry = new Entry();
                entry.setX(i);
                String s = i >= 10 ? time.substring(0,4) + "-" + time.substring(4,6) + "-" + i : time.substring(0,4) + "-" + time.substring(4,6) + "-0" + i;
                for (int j = 0; j < array.length(); j++) {
                    jsonObject = array.optJSONObject(j);
                    if (jsonObject.optString("ctime").equals(s)) {
                        entry.setY(jsonObject.optInt("ele"));
                        break;
                    } else entry.setY(0);
                }
                list.add(entry);
            }
        }

        if (n == 1) {
            photovoltaicList = list;
        } else if (n == 2) {
            chargerList = list;
        } else if (n == 3) {
            gridList = list;
        } else if (n == 4) {
            costList = list;
        }
    }

}
