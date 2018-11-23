package com.growatt.energymanagement.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.NotificationMsg;
import com.growatt.energymanagement.utils.InternetUtils;
import com.growatt.energymanagement.view.CircleCake;
import com.growatt.energymanagement.view.GradientTextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 通知-报表详情
 */
public class ReportDetailActivity extends BasicActivity {

    private GradientTextView totalGenerate;
    private GradientTextView totalConsume;
    private LinearLayout detailList;
    private TextView time;
    private TextView pv_grid;
    private TextView pv_in;
    private TextView benefit;
    private CircleCake percent_cake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.report_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        time = findViewById(R.id.time);
        totalGenerate = findViewById(R.id.gen_ele_total);
        totalConsume = findViewById(R.id.con_ele_total);
        totalGenerate.setShapeColors(0xff08ffce,0xff00c0ff);
        totalConsume.setShapeColors(0xffffe21d,0xfff5b622);
        pv_grid = findViewById(R.id.pv_grid);
        pv_in = findViewById(R.id.pv_in);
        benefit = findViewById(R.id.benefit);
        percent_cake = findViewById(R.id.percent_cake);
        detailList = findViewById(R.id.detail_list);

        InternetUtils.notice(LoginMsg.uniqueId, 1, "report", "");
    }

    private void addDetailItem(String title,String fromNet,String fee,String offer,int net,int fe,int of){
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_ele_consume_detail,detailList,false);
        TextView classify = view.findViewById(R.id.consume_ele_classify);
        TextView eleFromNet = view.findViewById(R.id.from_ele_net_text);
        TextView cost = view.findViewById(R.id.fee_text);
        TextView lightOffer = view.findViewById(R.id.light_offer_text);
        ProgressBar eleFrom = view.findViewById(R.id.from_ele_net_bar);
        ProgressBar feeBar = view.findViewById(R.id.fee_bar);
        ProgressBar offerBar = view.findViewById(R.id.light_offer_bar);
        classify.setText(title);
        eleFromNet.setText(fromNet);
        cost.setText(fee);
        lightOffer.setText(offer);
        eleFrom.setProgress(net);
        feeBar.setProgress(fe);
        offerBar.setProgress(of);
        detailList.addView(view);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ooo(NotificationMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        }else {
            NotificationMsg.ReportEntry reportEntry = msg.reportList.get(0);
            if (reportEntry == null) return;
            String temp = reportEntry.startDay + "~" + reportEntry.endDay;
            time.setText(temp);
            totalGenerate.setText(String.valueOf((int)reportEntry.pv_out_total));
            totalConsume.setText(String.valueOf((int)reportEntry.ele_total));
            pv_grid.setText(String.valueOf((int)reportEntry.pv_grid));
            pv_in.setText(String.valueOf((int)reportEntry.pv_in));
            percent_cake.setPercent((int) (reportEntry.pv_grid/(reportEntry.pv_grid + reportEntry.pv_in)));
            benefit.setText(String.format(getResources().getConfiguration().locale,"%.2f",reportEntry.benifit));

            addDetailItem("谷",
                    (int)reportEntry.valley.ele + "kWh",
                    "￥" + (int)reportEntry.valley.cost,
                    (int)reportEntry.valley.photovoltaic + "kWh",
                    80,20,50);
            addDetailItem("平",
                    (int)reportEntry.flat.ele + "kWh",
                    "￥" + (int)reportEntry.flat.cost,
                    (int)reportEntry.flat.photovoltaic + "kWh",
                    90,20,50);
            addDetailItem("峰",
                    (int)reportEntry.peak.ele + "kWh",
                    "￥" + (int)reportEntry.peak.cost,
                    (int)reportEntry.peak.photovoltaic + "kWh",
                    70,20,50);
            addDetailItem("尖",
                    (int)reportEntry.tip.ele + "kWh",
                    "￥" + (int)reportEntry.tip.cost,
                    (int)reportEntry.tip.photovoltaic + "kWh",
                    30,20,50);
        }
    }
}
