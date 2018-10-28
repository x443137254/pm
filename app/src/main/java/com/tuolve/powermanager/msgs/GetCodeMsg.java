package com.tuolve.powermanager.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/9/17.
 *
 */

public class GetCodeMsg {

    public String code;
    public String data;

    public GetCodeMsg(String s) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) return;
        code = jsonObject.optString("code");
        data = jsonObject.optString("data");
    }
}
