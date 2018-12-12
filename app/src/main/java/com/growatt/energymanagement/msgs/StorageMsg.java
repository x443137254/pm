package com.growatt.energymanagement.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wecc on 2018/12/13
 */

public class StorageMsg {

    public String code;
    public String errMsg;
    public double valtage_max;
    public double temp_min;
    public double current;
    public double voltage_all;
    public double soc;
    public double soh;
    public double valtage_min;
    public double temp_max;
    public String state;
    public String devId;

    public StorageMsg(String s) {
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
            if (data == null) return;
            valtage_max = data.optDouble("valtage_max");
            temp_min = data.optDouble("temp_min");
            current = data.optDouble("current");
            voltage_all = data.optDouble("voltage_all");
            soc = data.optDouble("soc");
            soh = data.optDouble("soh");
            valtage_min = data.optDouble("valtage_min");
            temp_max = data.optDouble("temp_max");
            state = data.optString("state");
            devId = data.optString("devId");
            }
        }
    }
