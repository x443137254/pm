package com.growatt.energymanagement.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/9/17.
 *
 */

public class ThirdLoginMsg {

    public String code;
    public String errMsg;
    public String nick;
    public long registTime;
    public String password;
    public String companyId;
    public boolean hasMsg;
    public String phone;
    public String companyName;
    public String account;
    public String email;
    public String uniqueId;
    public int cid = 0;

    public ThirdLoginMsg(String s) {
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
        }else {
            JSONObject data = jsonObject.optJSONObject("data");
            account = data.optString("account");
            nick = data.optString("nick");
//            password = data.optString("password");
            companyId = data.optString("companyId");
            phone = data.optString("phone");
            email = data.optString("email");
            uniqueId = data.optString("uniqueId");
            companyName = data.optString("companyName");
            hasMsg = data.optBoolean("hasMsg");
            cid = data.optInt("cid");
            registTime = data.optLong("registTime");
        }
    }
}
