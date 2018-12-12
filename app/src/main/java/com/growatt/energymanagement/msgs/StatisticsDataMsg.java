package com.growatt.energymanagement.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/10/22.
 *
 */

public class StatisticsDataMsg {

    public String code;
    public String errMsg;
    public double power_theory = 0;
    public double ele_total = 0;
    public double benifit_total = 0;
    public double ele_cost = 0;
    public double cost_pv = 0;
    public double cost_grid = 0;
    public double cost_battery = 0;

    public StatisticsDataMsg(String s) {
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
            power_theory = data.optDouble("power_theory");
            ele_total = data.optDouble("ele_total");
            benifit_total = data.optDouble("benifit_total");
            ele_cost = data.optDouble("ele_cost");
            cost_pv = data.optDouble("cost_pv");
            cost_grid = data.optDouble("cost_grid");
            cost_battery = data.optDouble("cost_battery");
        }
    }
}
