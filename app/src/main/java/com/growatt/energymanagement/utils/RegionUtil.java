package com.growatt.energymanagement.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/15.
 *
 */

public class RegionUtil {

    private JSONArray provinceArray;

    public RegionUtil(Context context) {
        try {
            provinceArray = new JSONArray(CommentUtils.getJson("province.json",context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<String> getProvinceList(){
        List<String> provinceList = new ArrayList<>();
        for (int i = 0; i < provinceArray.length(); i ++){
            provinceList.add(provinceArray.optJSONObject(i).optString("name"));
        }
        return provinceList;
    }

    public List<String> getCityList(int position){
        List<String> provinceList = new ArrayList<>();
        JSONArray cityArray = provinceArray.optJSONObject(position).optJSONArray("city");
        for (int i = 0; i < cityArray.length(); i ++){
            provinceList.add(cityArray.optJSONObject(i).optString("name"));
        }
        return provinceList;
    }

    public List<String> getAreaList(int provincePosition, int cityPosition){
        List<String> provinceList = new ArrayList<>();
        JSONArray areaArray = provinceArray.optJSONObject(provincePosition).optJSONArray("city")
                .optJSONObject(cityPosition).optJSONArray("area");
        for (int i = 0; i < areaArray.length(); i ++){
            provinceList.add(areaArray.optString(i));
        }
        return provinceList;
    }
}
