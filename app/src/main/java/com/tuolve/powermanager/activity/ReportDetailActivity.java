package com.tuolve.powermanager.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tuolve.powermanager.R;
import com.tuolve.powermanager.view.GradientTextView;

/**
 * 通知-报表详情
 */
public class ReportDetailActivity extends BasicActivity {

    private GradientTextView totalGenerate;
    private GradientTextView totalConsume;
    private LinearLayout detailList;

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
        totalGenerate = findViewById(R.id.gen_ele_total);
        totalConsume = findViewById(R.id.con_ele_total);
        totalGenerate.setShapeColors(0xff08ffce,0xff00c0ff);
        totalConsume.setShapeColors(0xffffe21d,0xfff5b622);
        detailList = findViewById(R.id.detail_list);

        addDetailItem("谷","4307kWh","￥200","302kWh",80,10,45);
        addDetailItem("平","4306kWh","￥201","305kWh",90,18,55);
        addDetailItem("峰","4305kWh","￥202","304kWh",70,15,75);
        addDetailItem("尖","4304kWh","￥203","307kWh",30,12,35);
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
}
