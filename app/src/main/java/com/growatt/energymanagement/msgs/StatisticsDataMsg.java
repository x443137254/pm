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
    public String power_theory;
    public String ele_total;
    public String benifit_total;
    public String ele_cost;
    public String cost_pv;
    public String cost_grid;
    public String cost_battery;

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
            power_theory = data.optString("power_theory");
            ele_total = data.optString("ele_total");
            benifit_total = data.optString("benifit_total");
            ele_cost = data.optString("ele_cost");
            cost_pv = data.optString("cost_pv");
            cost_grid = data.optString("cost_grid");
            cost_battery = data.optString("cost_battery");
        }
    }
}
