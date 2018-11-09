package com.growatt.energymanagement.msgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/24
 */

public class DevsDetailInfoMsg {

    public String code;
    public String errMsg;
    public DevsDetailInfo all;
    public DevsDetailInfo running;
    public DevsDetailInfo error;
    public DevsDetailInfo sleeping;

    public DevsDetailInfoMsg(String s) {
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
        } else {
            JSONObject data = jsonObject.optJSONObject("data");
            if (data == null) return;
            JSONObject allJson = data.optJSONObject("all");
            JSONObject runningJson = data.optJSONObject("running");
            JSONObject errorJson = data.optJSONObject("error");
            JSONObject sleepingJson = data.optJSONObject("sleeping");

            if (allJson != null){
                all = new DevsDetailInfo();
                all.devsNum = allJson.optInt("devsNum");
                all.totalEle = allJson.optInt("totalEle");
                JSONArray array = allJson.optJSONArray("devs");
                if (array != null) {
                    all.devs = new ArrayList<>();
                    JSONObject json;
                    Dev dev;
                    for (int i = 0; i < array.length(); i++){
                        json = array.optJSONObject(i);
                        dev = new Dev();
                        dev.devId = json.optString("devId");
                        dev.name = json.optString("name");
                        dev.roomTemp = json.optInt("roomTemp");
                        dev.online = json.optInt("online");
                        dev.onoff = json.optInt("onoff");
                        all.devs.add(dev);
                    }
                }
            }

            if (runningJson != null){
                running = new DevsDetailInfo();
                running.devsNum = runningJson.optInt("devsNum");
                running.totalEle = runningJson.optInt("totalEle");
                JSONArray array = runningJson.optJSONArray("devs");
                if (array != null) {
                    running.devs = new ArrayList<>();
                    JSONObject json;
                    Dev dev;
                    for (int i = 0; i < array.length(); i++){
                        json = array.optJSONObject(i);
                        dev = new Dev();
                        dev.devId = json.optString("devId");
                        dev.name = json.optString("name");
                        dev.roomTemp = json.optInt("roomTemp");
                        dev.online = json.optInt("online");
                        dev.onoff = json.optInt("onoff");
                        running.devs.add(dev);
                    }
                }
            }

            if (errorJson != null){
                error = new DevsDetailInfo();
                error.devsNum = errorJson.optInt("devsNum");
                error.totalEle = errorJson.optInt("totalEle");
                JSONArray array = errorJson.optJSONArray("devs");
                if (array != null) {
                    error.devs = new ArrayList<>();
                    JSONObject json;
                    Dev dev;
                    for (int i = 0; i < array.length(); i++){
                        json = array.optJSONObject(i);
                        dev = new Dev();
                        dev.devId = json.optString("devId");
                        dev.name = json.optString("name");
                        dev.roomTemp = json.optInt("roomTemp");
                        dev.online = json.optInt("online");
                        dev.onoff = json.optInt("onoff");
                        error.devs.add(dev);
                    }
                }
            }

            if (sleepingJson != null){
                sleeping = new DevsDetailInfo();
                sleeping.devsNum = sleepingJson.optInt("devsNum");
                sleeping.totalEle = sleepingJson.optInt("totalEle");
                JSONArray array = sleepingJson.optJSONArray("devs");
                if (array != null) {
                    sleeping.devs = new ArrayList<>();
                    JSONObject json;
                    Dev dev;
                    for (int i = 0; i < array.length(); i++){
                        json = array.optJSONObject(i);
                        dev = new Dev();
                        dev.devId = json.optString("devId");
                        dev.name = json.optString("name");
                        dev.roomTemp = json.optInt("roomTemp");
                        dev.online = json.optInt("online");
                        dev.onoff = json.optInt("onoff");
                        sleeping.devs.add(dev);
                    }
                }
            }
        }
    }

    public class DevsDetailInfo {
        public int devsNum;
        public int totalEle;
        public List<Dev> devs;
    }

    public class Dev {
        public String devId;
        public String name;
        public int roomTemp;
        public int online;
        public int onoff;
    }
}
