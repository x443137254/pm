package com.growatt.energymanagement.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/10/15.
 *
 */

public class AddCollectorMsg {

    public String data;
    public String code;

    public AddCollectorMsg(String s) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) return;
        code = jsonObject.optString("code");
        if (!code.equals("0")) {
            data = jsonObject.optString("data");
        }
    }
}
