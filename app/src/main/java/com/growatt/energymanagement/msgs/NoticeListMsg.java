package com.growatt.energymanagement.msgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/2
 */

public class NoticeListMsg{

    public String code;
    public String errMsg;
    public List<NoticeListBean> list;

    public NoticeListMsg(String s) {
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
            JSONObject jsonBean;
            NoticeListBean dataBean;
            for (int i = 0; i <array.length(); i++) {
                jsonBean = array.optJSONObject(i);
                dataBean = new NoticeListBean();
                dataBean.id = jsonBean.optInt("id");
                dataBean.sn = jsonBean.optString("sn");
                dataBean.time = jsonBean.optLong("time");
                dataBean.event = jsonBean.optInt("event");
                dataBean.type = jsonBean.optString("type");
                list.add(dataBean);
            }
        }
    }

    public class NoticeListBean{
        public int id;
        public String sn;
        public long time;
        public int event;
        public String type;
    }
}
