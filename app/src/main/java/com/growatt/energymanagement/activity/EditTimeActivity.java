package com.growatt.energymanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.growatt.energymanagement.R;


public class EditTimeActivity extends BasicActivity implements View.OnClickListener {

    private EditText startH;
    private EditText startM;
    private EditText endH;
    private EditText endM;
    private int resultCode = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_time);
        getSupportActionBar().hide();
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);
        startH = findViewById(R.id.start_h);
        startM = findViewById(R.id.start_m);
        endH = findViewById(R.id.end_h);
        endM = findViewById(R.id.end_m);
        Intent intent = getIntent();
        String time = intent.getStringExtra("time");
        if (time == null || time.equals("") || time.equals("null")) return;
        String[] s = time.split("[\\D]");
        startH.setText(s[0]);
        startM.setText(s[1]);
        endH.setText(s[2]);
        endM.setText(s[3]);
        resultCode = 702;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                String sh = startH.getText().toString();
                String sm = startM.getText().toString();
                String eh = endH.getText().toString();
                String em = endM.getText().toString();
                if (checkHourLegal(sh) && checkMiniLegal(sm) && checkHourLegal(eh) && checkMiniLegal(em)){
                    String time = add0(sh) + ":" + add0(sm) + "-" + add0(eh) + ":" + add0(em);
                    Intent data = new Intent();
                    data.putExtra("time", time);
                    setResult(resultCode,data);
                    finish();
                }else {
                    Toast.makeText(this, getResources().getString(R.string.input_not_legal), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.back:
                finish();
                break;
            case R.id.delete:
                resultCode = 701;
                setResult(resultCode);
                finish();
                break;
        }
    }

    private boolean checkMiniLegal(String s) {
        return !s.equals("") && !s.equals("null") && Integer.parseInt(s) < 60;
    }

    private boolean checkHourLegal(String s) {
        return !s.equals("") && !s.equals("null") && Integer.parseInt(s) < 24;
    }

    private String add0(String s){
        return Integer.parseInt(s) < 10 && s.length() == 1 ? "0" + s : s;
    }
}
