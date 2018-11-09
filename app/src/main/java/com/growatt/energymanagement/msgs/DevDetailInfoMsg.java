package com.growatt.energymanagement.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/11/6
 */

public class DevDetailInfoMsg {

    public String code;
    public String errMsg;
    public String areaName;
    public int power;
    public int current;
    public int voltage;
    public int online;
    public int onoff;
    public int temp_room;
    public int temp_set;
    public String sn;
    public String classify;

    public DevDetailInfoMsg(String s) {
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
            areaName = data.optString("areaName");
            power = data.optInt("power");
            current = data.optInt("current");
            voltage = data.optInt("voltage");
            online = data.optInt("online");
            onoff = data.optInt("onoff");
            temp_room = data.optInt("temp_room");
            temp_set = data.optInt("temp_set");
            sn = data.optString("sn");
            classify = data.optString("classify");
        }
    }
}
