package com.growatt.energymanagement.msgs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/9/15.
 *
 */

public class UserNameExistMsg {
    public boolean exist;
    public UserNameExistMsg(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            exist = jsonObject.optBoolean("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
