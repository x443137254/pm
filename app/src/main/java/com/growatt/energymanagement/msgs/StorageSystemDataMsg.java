package com.growatt.energymanagement.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/10/22.
 *
 */

public class StorageSystemDataMsg {

    public String code;
    public String errMsg;
    public double power_grid = 0;
    public double power_cost = 0;
    public double power_pv = 0;
    public double power_discharger = 0;
    public double pv_in = 0;
    public double pv_out = 0;
    public boolean status;

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
            if (data == null) return;
            power_grid = data.optDouble("power_grid");
            power_cost = data.optDouble("power_cost");
            power_pv = data.optDouble("power_pv");
            power_discharger = data.optDouble("power_discharger");
            pv_in = data.optDouble("pv_in");
            pv_out = data.optDouble("pv_out");
            status = data.optBoolean("status");
        }
    }
}
