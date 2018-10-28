package com.growatt.energymanagement.msgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/19.
 *
 */

public class NotificationMsg {
    public List<WarnEntry> warnList;
    public List<ReportEntry> reportList;
    public String errMsg;
    public String code;

    public NotificationMsg(String s) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) return;
        code = jsonObject.optString("code");
        if (code.equals("0")){
            JSONArray warning = jsonObject.optJSONArray("warning");
            JSONArray report = jsonObject.optJSONArray("report");
            JSONObject json;
            if (warning != null){
                warnList = new ArrayList<>();
                WarnEntry entry;
                for (int i = 0; i < warning.length(); i++) {
                    json = warning.optJSONObject(i);
                    entry = new WarnEntry();
                    entry.cid = json.optInt("cid");
                    entry.devType = json.optInt("devType");
                    entry.sn = json.optString("sn");
                    entry.event = json.optInt("event");
                    warnList.add(entry);
                }
            }
            if (report != null){
                reportList = new ArrayList<>();
                ReportEntry entry;
                for (int i = 0; i < report.length(); i++) {
                    json = report.optJSONObject(i);
                    entry = new ReportEntry();
                    entry.reportName = json.optString("reportName");
                    entry.ele_total = json.optDouble("ele_total");
                    entry.pv_out_total = json.optDouble("pv_out_total");
                    entry.startDay = json.optString("startDay");
                    entry.endDay = json.optString("endDay");
                    entry.pv_grid = json.optDouble("pv_grid");
                    entry.pv_in = json.optDouble("pv_in");
                    entry.benifit = json.optDouble("benifit");
                    entry.occurrenceTime = json.optString("occurrenceTime");
                    entry.peak = entry.new Peak();
                    entry.flat = entry.new Flat();
                    entry.valley = entry.new Valley();
                    entry.tip = entry.new Tip();

                    JSONObject json1;
                    json1 = json.optJSONObject("peak");
                    entry.peak.cost = json1.optDouble("cost");
                    entry.peak.photovoltaic = json1.optDouble("photovoltaic");
                    entry.peak.ele = json1.optDouble("ele");

                    json1 = json.optJSONObject("flat");
                    entry.flat.cost = json1.optDouble("cost");
                    entry.flat.photovoltaic = json1.optDouble("photovoltaic");
                    entry.flat.ele = json1.optDouble("ele");

                    json1 = json.optJSONObject("valley");
                    entry.valley.cost = json1.optDouble("cost");
                    entry.valley.photovoltaic = json1.optDouble("photovoltaic");
                    entry.valley.ele = json1.optDouble("ele");

                    json1 = json.optJSONObject("tip");
                    entry.tip.cost = json1.optDouble("cost");
                    entry.tip.photovoltaic = json1.optDouble("photovoltaic");
                    entry.tip.ele = json1.optDouble("ele");

                    reportList.add(entry);
                }
            }
        }else {
            errMsg = jsonObject.optString("data");
        }
    }
    public class WarnEntry{
        public int cid;
        public String sn;
        public int devType;
        public int event;
    }

    public class ReportEntry{
        public String reportName;
        public double ele_total;
        public double pv_out_total;
        public String startDay;
        public String endDay;
        public double pv_grid;
        public double pv_in;
        public double benifit;
        public String occurrenceTime;
        public Peak peak;
        public Flat flat;
        public Valley valley;
        public Tip tip;

        public class Peak{
            public double cost;
            public double photovoltaic;
            public double ele;
        }
        public class Flat{
            public double cost;
            public double photovoltaic;
            public double ele;
        }
        public class Valley{
            public double cost;
            public double photovoltaic;
            public double ele;
        }
        public class Tip{
            public double cost;
            public double photovoltaic;
            public double ele;
        }
    }
}
