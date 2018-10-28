package com.tuolve.powermanager;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.tuolve.powermanager.service.CheckUpdateService;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by Administrator on 2018/9/17.
 *
 */

public class PMApplication extends Application {
    public final int BIG_VERSION = 1;
    public int SMALL_VERSION  = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            SMALL_VERSION  = getPackageManager().getPackageInfo(getPackageName(),0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        UMConfigure.init(this,"5b9f2a15f29d982006000190","umeng", UMConfigure.DEVICE_TYPE_PHONE,"");
        PlatformConfig.setWeixin("wxd35b09ff1d12f3ce", "0e360ecc00be1ca75f85da2ab029e743");
        PlatformConfig.setQQZone("101500767", "a8251b3caaa19725b20ac6bb29797ac7");
        UMShareAPI.get(this);
        Intent intent = new Intent(this, CheckUpdateService.class);
        intent.setAction("check_big_version");
        startService(intent);
    }
}
