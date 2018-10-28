package com.tuolve.powermanager.msgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/25.
 *
 */

public class CountryDataMsg {

    public List<String> countryList;
    public String errMsg;
    public String code;

    public CountryDataMsg(String s) {
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
            JSONArray array = jsonObject.optJSONArray("data");
            if (array == null) return;
            countryList = new ArrayList<>();
            JSONObject country;
            for (int i = 0; i < array.length(); i++){
                country = array.optJSONObject(i);
                countryList.add(country.optString("country"));
            }
        }
    }
}
