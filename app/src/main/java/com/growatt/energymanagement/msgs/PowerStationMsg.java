package com.growatt.energymanagement.msgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/10/25.
 *
 */

public class PowerStationMsg {

    public String code;
    public String errMsg;
    public String country;
    public String city;
    public String latitude;
    public String designCompany;
    public String name;
    public String setDate;
    public String timeArea;
    public String longitude;
    public double subsidy;
    public double benifit;
    public int id;
    public int power;

    public PowerStationMsg(String s) {
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
            JSONArray array = jsonObject.optJSONArray("data");
            if (array == null) return;
            JSONObject data = array.optJSONObject(0);
            if (data == null) return;
            country = data.optString("country");
            city = data.optString("city");
            latitude = data.optString("Latitude");
            designCompany = data.optString("designCompany");
            name = data.optString("name");
            setDate = data.optString("setDate");
            timeArea = data.optString("timeArea");
            longitude = data.optString("longitude");
            subsidy = data.optDouble("subsidy");
            benifit = data.optDouble("benifit");
            id = data.optInt("id");
            power = data.optInt("power");
        }
    }
}
