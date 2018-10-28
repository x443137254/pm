package com.growatt.energymanagement.msgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/18.
 *
 */

public class ElePriceInfoMsg {

    public String code;
    public String errMsg;
    public List<ElePriceInfoMsg.ElePrice> elePriceList;

    public ElePriceInfoMsg(String s) {
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
            elePriceList = new ArrayList<>();
            JSONArray array = jsonObject.optJSONArray("data");
            ElePriceInfoMsg.ElePrice elePrice;
            JSONObject elePriceJson;
            for (int i = 0; i < array.length(); i++){
                elePrice = new ElePriceInfoMsg.ElePrice();
                elePriceJson = array.optJSONObject(i);
                elePrice.cid = elePriceJson.optInt("id");
                elePrice.status = elePriceJson.optInt("status");
                elePrice.name = elePriceJson.optString("name");
                elePrice.timeValue = elePriceJson.optString("timeValue");
                elePrice.effectiveTime = elePriceJson.optString("effectiveTime");
                elePrice.price = elePriceJson.optDouble("price");
                elePriceList.add(elePrice);
            }
        }
    }

    public class ElePrice{
        public int cid;
        public String name;
        public String timeValue;
        public String effectiveTime;
        public double price;
        public int status;
    }
}
