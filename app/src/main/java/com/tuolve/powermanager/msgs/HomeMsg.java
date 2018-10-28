package com.tuolve.powermanager.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/9/29.
 *
 */

public class HomeMsg {

    public String code;
    public String errMsg;
    public double ele_out; //当月发电
    public double ele_earnings; //当月收益
    public double gridPower; //电网功率
    public double ele_cost; //当月电费
    public double devicesPower; //总负载功率
    public double photovoltaicRuntimePower; //运行容量（光伏设备运行时功率）
    public double photovoltaicTheroyPower; //装机容量（光伏设备理论最大功率）
    public double ele_out_total; //累计发电
    public double ele_in; //当月用电
    //设备数量
    public int inverter;
    public int aircondition;
    public int socket;
    public int ameter;

    public static boolean storageSystem;

    public HomeMsg(String s) {
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
            ele_out = data.optDouble("ele_out");
            ele_earnings = data.optDouble("ele_earnings");
            gridPower = data.optDouble("gridPower");
            ele_cost = data.optDouble("ele_cost");
            devicesPower = data.optDouble("devicesPower");
            photovoltaicRuntimePower = data.optDouble("photovoltaicRuntimePower");
            photovoltaicTheroyPower = data.optDouble("photovoltaicTheroyPower");
            ele_out_total = data.optDouble("ele_out_total");
            ele_in = data.optDouble("ele_in");
            storageSystem = data.optBoolean("storageSystem");
            JSONObject devsData = data.optJSONObject("devsData");
            ameter = devsData.optInt("ameter");
            inverter = devsData.optInt("inverter");
            aircondition = devsData.optInt("aircondition");
            socket = devsData.optInt("socket");
        }
    }
}
