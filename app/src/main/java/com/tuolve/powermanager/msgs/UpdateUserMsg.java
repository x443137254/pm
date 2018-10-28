package com.tuolve.powermanager.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/9/18.
 *
 */

public class UpdateUserMsg {

    public String errMsg;
    public String code;

    public UpdateUserMsg(String s) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) return;
        code = jsonObject.optString("code");
        errMsg = jsonObject.optString("data");
    }
}
