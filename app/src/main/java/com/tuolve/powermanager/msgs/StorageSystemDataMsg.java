package com.tuolve.powermanager.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/10/22.
 *
 */

public class StorageSystemDataMsg {

    public String code;
    public String errMsg;
    public String power_grid;
    public String power_cost;
    public String power_pv;
    public String power_discharger;
    public String pv_in;
    public String pv_out;
    public String status;

    public StorageSystemDataMsg(String s) {
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
            power_grid = data.optString("power_grid");
            power_cost = data.optString("power_cost");
            power_pv = data.optString("power_pv");
            power_discharger = data.optString("power_discharger");
            pv_in = data.optString("pv_in");
            pv_out = data.optString("pv_out");
            status = data.optString("status");
        }
    }
}
