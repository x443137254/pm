package com.growatt.energymanagement.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.growatt.energymanagement.PMApplication;
import com.growatt.energymanagement.msgs.UpdateMsg;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CheckUpdateService extends IntentService {

    private UpdateInfo info;

    public CheckUpdateService() {
        super("CheckUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        PMApplication app = (PMApplication) getApplication();
        getUpdateInfo(action, app.BIG_VERSION, app.SMALL_VERSION);
    }

    private void getUpdateInfo(final String action, final int big, final int small){
        String url = "http://cdn.growatt.com/apk/EMSApp/EmsApp.xml";
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().get().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody body = response.body();
                if (body == null) return;
                InputStream inputStream = body.byteStream();
                info = new UpdateInfo();
                try {
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document document = builder.parse(inputStream);
                    Element element = document.getDocumentElement();
                    NodeList nodeList = element.getChildNodes();
                    for (int i = 0; i < nodeList.getLength(); i++){
                        Node item = nodeList.item(i);
                        Element node = null;
                        if (item instanceof Element) node = (Element) item;
                        if (node == null) continue;
                        String tagName = node.getTagName();
                        String textContent = node.getTextContent();
                        switch (tagName){
                            case "version-code-big":
                                info.version_code_big = textContent;
                                break;
                            case "version-name-big":
                                info.version_name_big = textContent;
                                break;
                            case "file-size-big":
                                info.file_size_big = textContent;
                                break;
                            case "download-url":
                                info.download_url = textContent;
                                break;
                            case "version-code-small":
                                info.version_code_small = textContent;
                                break;
                            case "version-name-small":
                                info.version_name_small = textContent;
                                break;
                            case "file-size-small":
                                info.file_size_small = textContent;
                                break;
                            case "priority":
                                info.priority = textContent;
                                break;
                            case "version-log":
                                NodeList list = node.getElementsByTagName("language");
                                for (int j = 0; j < list.getLength(); j++){
                                    Node log = list.item(j);
                                    String s = log.getAttributes().item(0).getTextContent();
                                    switch (s){
                                        case "en-US":
                                            info.enLog = log.getTextContent();
                                            break;
                                        case "zh-CN":
                                            info.cnLog = log.getTextContent();
                                            break;
                                        default:
                                            info.defaultLog = log.getTextContent();
                                            break;

                                    }
                                }
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                switch (action) {
                    case "check_big_version":
                        if (Integer.parseInt(info.version_code_big) > big){
                            EventBus.getDefault().post(new UpdateMsg(info, true));
                        }
                        break;
                    case "check_small_version":
                        if (Integer.parseInt(info.version_code_small) > small){
                            EventBus.getDefault().post(new UpdateMsg(info, false));
                        }
                        break;
                }
            }
        });
    }

    public class UpdateInfo{
        public String version_code_big;
        public String version_name_big;
        public String file_size_big;
        public String download_url;
        public String version_code_small;
        public String version_name_small;
        public String file_size_small;
        public String priority;
        public String cnLog;
        public String enLog;
        public String defaultLog;
    }
}
