package com.tuolve.powermanager.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/10/15.
 *
 */

public class AnalysisDataMsg {

    public String code;
    public String errMsg;
    public double benifit_total;
    public double ele_total;
    public double cost_curr;
    public double ele_last;
    public double ele_curr;
    public double cost_last;

    public AnalysisDataMsg(String s) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) return;
        code = jsonObject.optString("code");
        if (code.equals("0")) {
            JSONObject data = jsonObject.optJSONObject("data");
            benifit_total = data.optDouble("benifit_total");
            ele_total = data.optDouble("ele_total");
            cost_curr = data.optDouble("cost_curr");
            ele_last = data.optDouble("ele_last");
            ele_curr = data.optDouble("ele_curr");
            cost_last = data.optDouble("cost_last");
        } else {
            errMsg = jsonObject.optString("data");
        }
    }
}
