package com.growatt.energymanagement.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/11/2
 */

public class NoticeInfoMsg {

    public String code;
    public String errMsg;
    public String devType;
    public String solution;
    public String name;
    public String sn;
    public long time;
    public int event;
    public int cid;

    public NoticeInfoMsg(String s) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) return;
        code = jsonObject.optString("code");
        if (code.equals("1")){
            errMsg = jsonObject.optString("code");
        }else {
            JSONObject data = jsonObject.optJSONObject("data");
            if (data == null) return;
            JSONObject warning = data.optJSONObject("warning");
            if (warning == null) return;
            devType = warning.optString("devType");
            solution = warning.optString("solution");
            name = warning.optString("name");
            sn = warning.optString("sn");
            time = warning.optLong("time");
            event = warning.optInt("event");
            cid = warning.optInt("cid");
        }
    }
}
