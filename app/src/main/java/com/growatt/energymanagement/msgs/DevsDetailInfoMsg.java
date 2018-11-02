package com.growatt.energymanagement.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/10/24
 */

public class DevsDetailInfoMsg {

    public String code;
    public String errMsg;

    public DevsDetailInfoMsg(String s) {
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
        }
    }
}
