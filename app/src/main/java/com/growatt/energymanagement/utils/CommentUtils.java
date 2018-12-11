package com.growatt.energymanagement.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.growatt.energymanagement.msgs.LoginMsg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/8/31.
 * 公共的工具类
 */

public class CommentUtils {

    /**
     * 6.0动态权限检查
     * @param activity
     * @param perMissions
     * @param requestCode
     * @return
     */
    public static boolean checkPermission(Activity activity, String[] perMissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String p : perMissions) {
                if ((ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(activity, perMissions, requestCode);
                    return false;
                }
            }
            return true;
        } else return true;
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 读取jason文件
     * @param fileName asset文件夹内的数据文件名
     * @param context 上下文
     * @return 以字符串形式返回
     */
    public static String getJson(String fileName,Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static String MD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return toHex(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (byte aByte : bytes) {
            ret.append(HEX_DIGITS[(aByte >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[aByte & 0x0f]);
        }
        return ret.toString();
    }

    /**
     * 弹出滚动选择器
     * @param data     数据源
     * @param textView 显示的位置
     * @param title    选择器标题
     */
    public static void showPickView(final Activity context, final List<String> data, final TextView textView, String title) {
         OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = data.get(options1);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(tx);
                    }
                });
            }
        })
                .setTitleText(title)
                .setTitleBgColor(0xff032d3a)
                .setTitleColor(0xffffffff)
                .setSubmitColor(0xffffffff)
                .setCancelColor(0xff058ef0)
                .setBgColor(0xff032d3a)
                .setTitleSize(22)
                .setTextColorCenter(0xffffffff)
                .build();
        pvOptions.setPicker(data);
        pvOptions.show();
    }

    /**
     * 缓存用户登录信息到本地
     */
    public static void save(Context context) {
        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("account", LoginMsg.account);
        edit.putString("password", LoginMsg.password);
        edit.putInt("cid", LoginMsg.cid);
        edit.putString("uniqueId", LoginMsg.uniqueId);
        edit.putBoolean("hasMsg", LoginMsg.hasMsg);
        edit.putString("nick", LoginMsg.nick);
        edit.putString("companyName", LoginMsg.companyName);
        edit.apply();
    }

    /**
     * 返回指定月份天数
     * @param year 年
     * @param month 月
     * @return 天数
     */
    public static int maxDays(int year, int month) {
        if (year <= 0 || month <= 0) return 0;
        if (month == 2) {
            if (year % 100 == 0 && year % 400 == 0) return 29;
            else if (year % 100 != 0 && year % 4 == 0) return 29;
            else return 28;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) return 30;
        else return 31;
    }

    /**
     * 提取字符串中的数字
     * @param s .
     * @return 返回数字
     */
    public static String getNumFromString(String s) {
        if (s == null) return "";
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        return m.replaceAll("").trim();
    }
}
