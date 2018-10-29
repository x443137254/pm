package com.growatt.energymanagement.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/9/17.
 *
 */

public class LoginMsg {
    public String code;
    public String errMsg;
    public static String nick;
    public static long registTime;
    public static String password;
    public static String companyId;
    public static boolean hasMsg;
    public static String phone;
    public static String companyName;
    public static String account;
    public static String email;
    public static String uniqueId;
    public static int cid = 0;

    public LoginMsg(String s) {
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

    public static void cleanUserInfo(){
        account = "";
        nick = "";
        companyId = "";
        phone = "";
        email = "";
        uniqueId = "";
        companyName = "";
        password = "";
        hasMsg = false;
        cid = 0;
        registTime = 0;
    }
}
