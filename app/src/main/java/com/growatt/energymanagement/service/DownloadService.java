package com.growatt.energymanagement.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.growatt.energymanagement.msgs.UpdateProgressMsg;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadService extends IntentService {


    private File file;
    private long length = 0;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String url = intent.getAction();
        if (url == null) return;
        file = new File(Environment.getExternalStorageDirectory(), url.substring(url.lastIndexOf("/"),url.length()-1));
        try {
            if (!file.exists() && file.createNewFile()) return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody body = response.body();
                if (body == null) return;
                length = body.contentLength();
                sendProgress();
                byte[] buffer = new byte[1024*1024];
                InputStream inputStream = body.byteStream();
                FileOutputStream fos = new FileOutputStream(file);
                int l = inputStream.read(buffer);
                while (l > 0){
                    fos.write(buffer,0,l);
                    l = inputStream.read(buffer);
                }
                inputStream.close();
                fos.close();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivity(intent);
            }
        });
    }

    private void sendProgress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                while (true){
                    if (length != 0) {
                        int progress = (int) (file.length() * 100 / length);
                        EventBus.getDefault().post(new UpdateProgressMsg(progress));
                    }
                    if (file.length() >= length) break;
                    SystemClock.sleep(200);
                }
            }
        }).start();
    }

}
