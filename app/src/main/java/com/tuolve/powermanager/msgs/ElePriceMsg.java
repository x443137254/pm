package com.tuolve.powermanager.msgs;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/18..
 *
 */

public class ElePriceMsg {

    public String errMsg;
    public String code;
    public List<ElePrice> elePriceList;

    public ElePriceMsg(String s) {
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
            ElePrice elePrice;
            JSONObject elePriceJson;
            for (int i = 0; i < array.length(); i++){
                elePrice = new ElePrice();
                elePriceJson = array.optJSONObject(i);
                elePrice.id = elePriceJson.optInt("id");
                elePrice.name = elePriceJson.optString("name");
                elePrice.price = elePriceJson.optDouble("price");
                elePriceList.add(elePrice);
            }
        }

    }

    public class ElePrice{
        public int id;
        public String name;
        public double price;
    }
}
