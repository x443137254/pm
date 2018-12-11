package com.growatt.energymanagement.msgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/27.
 *
 */

public class AllAreaMsg {

    public List<String> provinceList;
    public List<List<String>> cityList;
    public List<List<List<String>>> areaList;
    public String originalString;

    public AllAreaMsg(String s) {
        originalString = s;
        JSONArray array1 = null;
        try {
            array1 = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (array1 == null) return;
        provinceList = new ArrayList<>();
        cityList = new ArrayList<>();
        areaList = new ArrayList<>();
        for (int i = 0; i < array1.length(); i++) {
            JSONObject jsonObject1 = array1.optJSONObject(i);
            String province = jsonObject1.optString("name");
            provinceList.add(province);
            JSONArray array2 = jsonObject1.optJSONArray("citys");
            List<String> list = new ArrayList<>();
            List<List<String>> list2 = new ArrayList<>();

            if (province.equals("北京市") || province.equals("重庆市") || province.equals("天津市") || province.equals("上海市")){
                list.add(province);
                List<String> list1 = new ArrayList<>();
                for (int j = 0; j < array2.length(); j++) {
                    JSONObject jsonObject2 = array2.optJSONObject(j);
                    list1.add(jsonObject2.optString("name"));
                }
                list2.add(list1);
                cityList.add(list);
                areaList.add(list2);
            }else {
                for (int j = 0; j < array2.length(); j++) {
                    JSONObject jsonObject2 = array2.optJSONObject(j);
                    list.add(jsonObject2.optString("name"));
                    JSONArray array3 = jsonObject2.optJSONArray("areas");
                    List<String> list1 = new ArrayList<>();
                    for (int k = 0; k < array3.length(); k++) {
                        JSONObject jsonObject3 = array3.optJSONObject(k);
                        if (jsonObject3 != null){
                            String name = jsonObject3.optString("name");
                            if (name == null) name = "";
                            list1.add(name);
                        }
                    }
                    list2.add(list1);
                }
                cityList.add(list);
                areaList.add(list2);
            }
        }
    }
}
