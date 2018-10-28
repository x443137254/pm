package com.growatt.energymanagement.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/9/25.
 *
 */

public class BindingMsg {

    public String code;
    public String errMsg;
    public boolean data;
    public  String username;
    public  String pwd;

    public BindingMsg(String s,String username,String pwd) {
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
            data = jsonObject.optBoolean("data");
            this.username = username;
            this.pwd = pwd;
        }
    }
}
