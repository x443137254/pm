package com.tuolve.powermanager.msgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/18.
 *
 */

public class AreaInfoMsg {

    public String code;
    public String errMsg;
    public List<AreaInfo> list;

    public AreaInfoMsg(String s) {
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
            list = new ArrayList<>();
            AreaInfo areaInfo;
            JSONObject json;
            for (int i = 0; i < array.length(); i++) {
                json = array.optJSONObject(i);
                areaInfo = new AreaInfo();
                areaInfo.path = json.optString("path");
                areaInfo.imgurl = json.optString("imgurl");
                areaInfo.level = json.optString("level");
                areaInfo.name = json.optString("name");
                areaInfo.parentId = json.optString("parentId");
                areaInfo.cid = json.optString("cid");
                areaInfo.info = json.optString("info");
                list.add(areaInfo);
            }
        }
    }

    public class AreaInfo{
        public String path;
        public String imgurl;
        public String level;
        public String name;
        public String parentId;
        public String cid;
        public String info;
    }
}
