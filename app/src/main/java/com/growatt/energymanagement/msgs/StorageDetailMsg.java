package com.growatt.energymanagement.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wecc on 2018/12/13
 */

public class StorageDetailMsg {

    public String code;
    public String errMsg;

    public StorageDetailMsg(String s) {
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

        }
    }
}
