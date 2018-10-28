package com.tuolve.powermanager.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tuolve.powermanager.R;
import com.tuolve.powermanager.msgs.ElePriceInfoMsg;
import com.tuolve.powermanager.msgs.ElePriceMsg;
import com.tuolve.powermanager.msgs.LoginMsg;
import com.tuolve.powermanager.utils.InternetUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 个人-电价配置
 */
public class PriceConfActivity extends BasicActivity {

    private LinearLayout confList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_conf);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        findViewById(R.id.price_conf_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.price_conf_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(PriceConfActivity.this,AddPriceActivity.class),104);
            }
        });
        confList = findViewById(R.id.price_conf_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LoginMsg.cid != 0) InternetUtils.elePriceInfo(LoginMsg.uniqueId);
    }

    /**
     * 添加电价配置条目
     * @param title 电价名称
     * @param price 单价
     * @param time 起始时间
     * @param validTime 有效时间
     * @param isValid 是否有效
     */
    private void addConfItem(final int cid,final String title, final String price, final String time, final String  validTime, final String isValid) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_price_conf,confList,false);
        TextView titleView = view.findViewById(R.id.title);
        TextView priceView = view.findViewById(R.id.price);
        TextView startTimeView = view.findViewById(R.id.start_time);
        TextView validTimeView = view.findViewById(R.id.valid_time);
        TextView isValidView = view.findViewById(R.id.is_valid);
        titleView.setText(title);
        priceView.setText(price);
        startTimeView.setText(time);
        validTimeView.setText(validTime);
        isValidView.setText(isValid);
        confList.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PriceConfActivity.this, PriceConfDetailActivity.class);
                intent.putExtra("priceName",title);
                intent.putExtra("price",price);
                intent.putExtra("time",time);
                intent.putExtra("effTime",validTime);
                intent.putExtra("isEff",isValid);
                intent.putExtra("cid",cid);
                startActivity(intent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showList(ElePriceInfoMsg msg){
        if (msg.code.equals("1")){
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        }else {
            confList.removeAllViews();
            for (int i = 0; i < msg.elePriceList.size(); i++){
                ElePriceInfoMsg.ElePrice elePrice = msg.elePriceList.get(i);
                String validTime = "永久有效";
                if (elePrice.effectiveTime != null && !elePrice.effectiveTime.equals("")) validTime = elePrice.effectiveTime;
                String isValid = "是";
                if (elePrice.status == 1) isValid = "否";
                addConfItem(elePrice.cid,elePrice.name,String.valueOf(elePrice.price),elePrice.timeValue,validTime,isValid);
            }
        }
    }
}
