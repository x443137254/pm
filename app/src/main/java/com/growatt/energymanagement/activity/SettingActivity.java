package com.growatt.energymanagement.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.msgs.UpdateMsg;
import com.growatt.energymanagement.msgs.UpdateProgressMsg;
import com.growatt.energymanagement.service.CheckUpdateService;
import com.growatt.energymanagement.service.DownloadService;
import com.growatt.energymanagement.utils.CommentUtils;
import com.growatt.energymanagement.utils.DataCleanManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

/**
 * 个人-设置
 */
public class SettingActivity extends BasicActivity implements View.OnClickListener {

    private AlertDialog dialog;
    private ProgressBar updateProgress;
    private TextView clareCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        findViewById(R.id.setting_back).setOnClickListener(this);
        findViewById(R.id.check_update).setOnClickListener(this);
        findViewById(R.id.clare_cache_item).setOnClickListener(this);
        TextView version = findViewById(R.id.version);
        TextView companyPhone = findViewById(R.id.company_phone);
        clareCache = findViewById(R.id.clare_cache);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            version.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        try {
            String size = DataCleanManager.getTotalCacheSize(this);
            clareCache.setText(size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_back:
                finish();
                break;
            case R.id.check_update:
                Intent intent = new Intent(this, CheckUpdateService.class);
                intent.setAction("check_small_version");
                startService(intent);
                break;
            case R.id.clare_cache_item:
                try {
                    DataCleanManager.getTotalCacheSize(this);
                    String size = DataCleanManager.getTotalCacheSize(this);
                    clareCache.setText(size);
                    Toast.makeText(this, getResources().getString(R.string.clare_cache_success), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, getResources().getString(R.string.clare_cache_fail), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void alerUpdateDialog(final UpdateMsg msg) {
        if (msg.isBig) return;
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update, null);
        view.findViewById(R.id.update_dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        TextView version = view.findViewById(R.id.update_dialog_version_text);
        final TextView updateButton = view.findViewById(R.id.update_dialog_bt);
        TextView updateDesc = view.findViewById(R.id.update_dialog_desc);
        final TextView updateNextTime = view.findViewById(R.id.update_dialog_next_time);
        updateProgress = view.findViewById(R.id.update_dialog_progress);

        String s;
        if (msg.isBig) {
            s = "新版本：" + msg.info.version_name_big + "， 大小：" + msg.info.file_size_big;
        } else {
            s = "新版本：" + msg.info.version_name_small + "， 大小：" + msg.info.file_size_small;
        }
        version.setText(s);

        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String des;
        if (language.contains("zh")) {
            des = msg.info.cnLog;
        } else if (language.contains("en")) {
            des = msg.info.enLog;
        } else {
            des = msg.info.defaultLog;
        }
        updateDesc.setText(des);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNextTime.setText(getResources().getString(R.string.update_background));
                updateButton.setText(getResources().getString(R.string.updating));
                updateButton.setOnClickListener(null);
                if (CommentUtils.checkPermission(SettingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 9999)) {
                    startDownload(msg.info.download_url);
                }
            }
        });

        updateNextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog = new AlertDialog.Builder(this).setView(view).create();
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void startDownload(String url) {
        Intent intent = new Intent(SettingActivity.this, DownloadService.class);
//        intent.setAction("http://shouji.www.yxdown.com/down?id=79147");
        intent.setAction(url);
        startService(intent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setProgress(UpdateProgressMsg msg) {
        if (updateProgress != null) updateProgress.setProgress(msg.progress);
        if (msg.progress >= 100) dialog.cancel();
    }
}
