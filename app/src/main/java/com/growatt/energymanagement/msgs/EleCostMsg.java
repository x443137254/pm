package com.growatt.energymanagement.msgs;

import com.github.mikephil.charting.data.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2018/10/24
 */

public class EleCostMsg {

    public String code;
    public String errMsg;
    public List<Entry> list;

    public EleCostMsg(String s) {
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
            list = new ArrayList<>();
            JSONObject data = jsonObject.optJSONObject("data");
            Iterator<String> keys = data.keys();
            Entry entry;
            while (keys.hasNext()){
                entry = new Entry();
                String key = keys.next();
                entry.setX(Integer.parseInt(key.substring(key.length() - 2)));
                entry.setY((float) data.optDouble(key));
                list.add(entry);
            }
            if (list.size() > 0){
                Collections.sort(list, new Comparator<Entry>() {
                    @Override
                    public int compare(Entry o1, Entry o2) {
                        if (o1.getX() > o2.getX()) return 1;
                        return -1;
                    }
                });
            }
        }
    }

}
