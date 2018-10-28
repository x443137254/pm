package com.tuolve.powermanager.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tuolve.powermanager.R;
import com.tuolve.powermanager.msgs.UpdateUserMsg;
import com.tuolve.powermanager.utils.InternetUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

/**
 * 内容修改编辑
 */
public class ModifyActivity extends BasicActivity {

    private EditText editText;
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        findViewById(R.id.modify_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.modify_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = editText.getText().toString();
                if (data.equals("")) {
                    Toast.makeText(ModifyActivity.this, "请输入修改内容", Toast.LENGTH_SHORT).show();
                }else {
//                    String s = getIntent().getStringExtra("key");
//                    InternetUtils.updateUser(s,data);
                    Intent intent = new Intent();
                    intent.putExtra("data",data);
                    setResult(1000,intent);
                    finish();
                }
            }
        });
        editText = findViewById(R.id.modify_data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void aa(UpdateUserMsg msg){
        if (msg.code.equals("0")){
            Intent intent = new Intent();
            intent.putExtra("data",data);
            setResult(RESULT_OK, intent);
            finish();
        }
        Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
    }
}
