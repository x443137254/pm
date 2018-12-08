package com.growatt.energymanagement.activity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.ProgressBar;

import com.growatt.energymanagement.msgs.EmptyMsg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BasicActivity extends AppCompatActivity {

    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void aa(EmptyMsg msg){

    }

    void showProgressDialog(){
        if (progressDialog != null) {
            progressDialog.show();
            return;
        }
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressDialog = new AlertDialog.Builder(this)
                .setView(progressBar)
                .setCancelable(false)
                .create();
        Window window = progressDialog.getWindow();
        if (window != null){
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        progressDialog.show();
    }

    void disMissProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
