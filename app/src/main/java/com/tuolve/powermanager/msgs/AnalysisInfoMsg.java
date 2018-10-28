package com.tuolve.powermanager.msgs;

import com.github.mikephil.charting.data.Entry;
import com.tuolve.powermanager.utils.CommentUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/25.
 *
 */

public class AnalysisInfoMsg {

    public String code;
    public String errMsg;
    public int value_all;
    public int value_grid;
    public int value_bms;
    public List<Entry> gridList;
    public List<Entry> bmsList;
    public List<Entry> allList;
    private int timeType;

    public AnalysisInfoMsg(String s, int timeType) {
        this.timeType = timeType;
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
            if (data == null) return;
            value_all = data.optInt("value_all");
            value_grid = data.optInt("value_grid");
            value_bms = data.optInt("value_bms");
            JSONArray ele_grid = data.optJSONArray("ele_grid");
            JSONArray ele_bms = data.optJSONArray("ele_bms");
            JSONArray ele_all = data.optJSONArray("ele_all");
            if (ele_grid != null){
                gridList = parseArray(ele_grid);
            }
            if (ele_bms != null){
                bmsList = parseArray(ele_bms);
            }
            if (ele_all != null){
                allList = parseArray(ele_all);
            }
        }
    }

    private List<Entry> parseArray(JSONArray array){
        List<Entry> list = new ArrayList<>();
        JSONObject jsonObject;
        Entry entry;
        for (int i = 0; i < array.length(); i++) {
            jsonObject = array.optJSONObject(i);
            entry = new Entry();
            entry.setY((float) jsonObject.optDouble("ele"));
            switch (timeType){
                case 1:
                    entry.setX(Integer.parseInt(CommentUtils.getNumFromString(jsonObject.optString("ctime"))));
                    break;
                case 2:
                    entry.setX(Integer.parseInt(jsonObject.optString("ctime").substring(8)));
                    break;
                case 3:
                    entry.setX(Integer.parseInt(jsonObject.optString("ctime").substring(5)));
                    break;
                case 4:
                    entry.setX(jsonObject.optInt("ctime"));
                    break;
            }
            list.add(entry);
        }
        return list;
    }

}
