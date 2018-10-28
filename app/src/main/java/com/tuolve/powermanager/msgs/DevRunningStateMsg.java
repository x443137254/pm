package com.tuolve.powermanager.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/10/24.
 *
 */

public class DevRunningStateMsg {

    public String code;
    public String errMsg;
    public DevRunState airCondition;
    public DevRunState socket;
    public DevRunState chargePile;
    public DevRunState thermostat;
    public DevRunState shineBoost;

    public DevRunningStateMsg(String s) {
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
            JSONObject json;
            if (data.has("airConditon")){
                airCondition = new DevRunState();
                json = data.optJSONObject("airConditon");
                airCondition.num_all = json.optInt("num_all");
                airCondition.num_runing = json.optInt("num_runing");
                airCondition.ele_cost = json.optInt("ele_cost");
            }
            if (data.has("socket")){
                socket = new DevRunState();
                json = data.optJSONObject("airCondition");
                socket.num_all = json.optInt("num_all");
                socket.num_runing = json.optInt("num_runing");
                socket.ele_cost = json.optInt("ele_cost");
            }
            if (data.has("chargePile")){
                chargePile = new DevRunState();
                json = data.optJSONObject("airCondition");
                chargePile.num_all = json.optInt("num_all");
                chargePile.num_runing = json.optInt("num_runing");
                chargePile.ele_cost = json.optInt("ele_cost");
            }
            if (data.has("thermostat")){
                thermostat = new DevRunState();
                json = data.optJSONObject("airCondition");
                thermostat.num_all = json.optInt("num_all");
                thermostat.num_runing = json.optInt("num_runing");
                thermostat.ele_cost = json.optInt("ele_cost");
            }
            if (data.has("shineBoost")){
                shineBoost = new DevRunState();
                json = data.optJSONObject("airCondition");
                shineBoost.num_all = json.optInt("num_all");
                shineBoost.num_runing = json.optInt("num_runing");
                shineBoost.ele_cost = json.optInt("ele_cost");
            }
        }
    }

    public class DevRunState{
        public int num_all;
        public int num_runing;
        public int ele_cost;
    }
}
