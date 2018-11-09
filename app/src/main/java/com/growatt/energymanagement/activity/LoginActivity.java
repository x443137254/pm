package com.growatt.energymanagement.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.growatt.energymanagement.R;
import com.growatt.energymanagement.msgs.BindingMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.ThirdLoginMsg;
import com.growatt.energymanagement.utils.CommentUtils;
import com.growatt.energymanagement.utils.InternetUtils;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

/**
 * 登录页面
 */
public class LoginActivity extends BasicActivity implements View.OnClickListener, UMAuthListener {

    private PopupWindow pop;
    private String appType;
    private String thirdUnique;
    private EditText popAccount;
    private EditText popPwd;
    private String pwd;
    private EditText accountEditText;
    private EditText pwdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MainActivity.isPad) {
            setContentView(R.layout.activity_login_pad);
            setBackground();
        } else setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_LOGS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.SET_DEBUG_APP,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.WRITE_APN_SETTINGS
            };
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }

        findViewById(R.id.login_back).setOnClickListener(this);
        findViewById(R.id.forget_pwd).setOnClickListener(this);
        findViewById(R.id.login_reg_bt).setOnClickListener(this);
        findViewById(R.id.login_qq).setOnClickListener(this);
        findViewById(R.id.login_wechat).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.forget_pwd).setOnClickListener(this);
        accountEditText = findViewById(R.id.account);
        pwdEditText = findViewById(R.id.pwd);
    }

    private void readCache() {
        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        String account = sp.getString("account", "");
        if (account.equals("")) return;
        String password = sp.getString("password", "");
        accountEditText.setText(account);
        pwdEditText.setText(password);
    }

    /**
     * 设置pad横竖版背景图片
     */
    private void setBackground() {
        View view = findViewById(R.id.login_pad_activity);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (metrics.widthPixels > metrics.heightPixels) {
            view.setBackgroundResource(R.mipmap.login_bg_pad_v);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_back:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.forget_pwd:
                startActivity(new Intent(this, FindPwdActivity.class));
                break;
            case R.id.login_reg_bt:
                startActivity(new Intent(this, RegActivity.class));
                break;
            case R.id.login_qq:
                UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.QQ, this);
                break;
            case R.id.login_wechat:
                UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, this);
                break;
            case R.id.pop_cancel:
                pop.dismiss();
                break;
            case R.id.third_reg:
                Intent intent = new Intent(this, RegActivity.class);
                intent.putExtra("appType", appType);
                intent.putExtra("thirdUnique", thirdUnique);
                startActivity(intent);
                break;
            case R.id.login:
                String account = accountEditText.getText().toString();
                if (account.equals("") || account.equals("null")) {
                    Toast.makeText(this, getResources().getString(R.string.account_empty), Toast.LENGTH_SHORT).show();
                    return;
                }
                pwd = pwdEditText.getText().toString();
                if (pwd.equals("") || pwd.equals("null")) {
                    Toast.makeText(this, getResources().getString(R.string.reg_pwd_empty), Toast.LENGTH_SHORT).show();
                    return;
                }
                InternetUtils.login(account, pwd);
                break;
            case R.id.bind:
                String bindUserName = popAccount.getText().toString();
                String bingPwd = popPwd.getText().toString();
                if (TextUtils.isEmpty(bindUserName) || TextUtils.isEmpty(bingPwd)) {
                    Toast.makeText(this, getResources().getString(R.string.input_empty), Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    InternetUtils.binding(bindUserName,bingPwd,appType,thirdUnique);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 第三方登录回调
     *
     * @param share_media 登录平台类型
     */
    @Override
    public void onStart(SHARE_MEDIA share_media) {
    }

    /**
     * 第三方登录回调
     *
     * @param share_media 登录平台类型
     * @param i
     * @param map         获取的用户信息
     */
    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
        Toast.makeText(this, getResources().getString(R.string.accredit_success), Toast.LENGTH_SHORT).show();
        appType = share_media.toString();
        thirdUnique = map.get("uid");
        InternetUtils.thirdLogin(appType, thirdUnique);
    }

    /**
     * 第三方登录回调
     *
     * @param share_media 登录平台类型
     * @param i
     * @param throwable   错误信息
     */
    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        Toast.makeText(this, getResources().getString(R.string.accredit_fail), Toast.LENGTH_SHORT).show();
    }

    /**
     * 第三方登录回调
     *
     * @param share_media 登录平台类型
     * @param i
     */
    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void tl(ThirdLoginMsg msg) {
        if (msg.code.equals("1")) {
            pop();
        }
    }

    /**
     * 弹出绑定账号菜单
     */
    private void pop() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_bind_account, null);
        if (!MainActivity.isPad) {
            pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pop.setOutsideTouchable(true);
            pop.setTouchable(true);
            pop.setFocusable(true);
            pop.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
            final WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.5f;
            getWindow().setAttributes(lp);
            pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    lp.alpha = 1;
                    getWindow().setAttributes(lp);
                }
            });
        } else {
            new AlertDialog.Builder(this).setView(view).show();
        }
        view.findViewById(R.id.pop_cancel).setOnClickListener(this);
        view.findViewById(R.id.third_reg).setOnClickListener(this);
        view.findViewById(R.id.bind).setOnClickListener(this);
        popAccount = view.findViewById(R.id.bind_account);
        popPwd = view.findViewById(R.id.bind_pwd);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginInfo(LoginMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            LoginMsg.password = pwd;
            CommentUtils.save(this);
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bindResult(BindingMsg msg) {
        if (msg.code.equals("1")) {
            Toast.makeText(this, msg.errMsg, Toast.LENGTH_SHORT).show();
        } else {
            InternetUtils.login(msg.username,msg.pwd);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        readCache();
    }
}
