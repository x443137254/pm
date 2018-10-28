package com.tuolve.powermanager.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/9/20.
 *
 */

public class GreenBenifitMsg {
    public String errMsg;
    public String code;
    public String tce;
    public String plants;
    public String co2;

    public GreenBenifitMsg(String s) {
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
            tce = data.optString("TCE");
            plants = data.optString("TCE");
            co2 = data.optString("TCE");
        }
    }
}
