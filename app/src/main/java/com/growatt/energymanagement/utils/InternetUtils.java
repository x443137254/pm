package com.growatt.energymanagement.utils;

import com.growatt.energymanagement.msgs.AddCollectorMsg;
import com.growatt.energymanagement.msgs.AddElePriceMsg;
import com.growatt.energymanagement.msgs.AllAreaMsg;
import com.growatt.energymanagement.msgs.AmmetersMsg;
import com.growatt.energymanagement.msgs.AnalysisDataMsg;
import com.growatt.energymanagement.msgs.AnalysisInfoMsg;
import com.growatt.energymanagement.msgs.AreaDevsStateMsg;
import com.growatt.energymanagement.msgs.AreaEleInfoMsg;
import com.growatt.energymanagement.msgs.AreaEleRankMsg;
import com.growatt.energymanagement.msgs.AreaInfoMsg;
import com.growatt.energymanagement.msgs.BindingMsg;
import com.growatt.energymanagement.msgs.CountryDataMsg;
import com.growatt.energymanagement.msgs.DevDetailInfoMsg;
import com.growatt.energymanagement.msgs.DevRunningStateMsg;
import com.growatt.energymanagement.msgs.DevTypeEleCostMsg;
import com.growatt.energymanagement.msgs.DevsDetailInfoMsg;
import com.growatt.energymanagement.msgs.EditPowerStationMsg;
import com.growatt.energymanagement.msgs.EleCostMsg;
import com.growatt.energymanagement.msgs.ElePriceInfoMsg;
import com.growatt.energymanagement.msgs.ElePriceMsg;
import com.growatt.energymanagement.msgs.EnergyTendencyMsg;
import com.growatt.energymanagement.msgs.ForgetPwdMsg;
import com.growatt.energymanagement.msgs.GenerateEleOverviewMsg;
import com.growatt.energymanagement.msgs.GenerateElectricityMsg;
import com.growatt.energymanagement.msgs.GenerateElectricitysMsg;
import com.growatt.energymanagement.msgs.GetCodeMsg;
import com.growatt.energymanagement.msgs.GetDevWarningNumMsg;
import com.growatt.energymanagement.msgs.GreenBenifitMsg;
import com.growatt.energymanagement.msgs.HomeMsg;
import com.growatt.energymanagement.msgs.LoginMsg;
import com.growatt.energymanagement.msgs.NoticeInfoMsg;
import com.growatt.energymanagement.msgs.NoticeListMsg;
import com.growatt.energymanagement.msgs.NotificationMsg;
import com.growatt.energymanagement.msgs.OutputAndInputOfEleMsg;
import com.growatt.energymanagement.msgs.PowerStationMsg;
import com.growatt.energymanagement.msgs.QualityDataMsg;
import com.growatt.energymanagement.msgs.RegistMsg;
import com.growatt.energymanagement.msgs.InvertersMsg;
import com.growatt.energymanagement.msgs.SettingMsg;
import com.growatt.energymanagement.msgs.StatisticsDataMsg;
import com.growatt.energymanagement.msgs.StorageSystemDataMsg;
import com.growatt.energymanagement.msgs.ThirdLoginMsg;
import com.growatt.energymanagement.msgs.ThirdRegistMsg;
import com.growatt.energymanagement.msgs.UpdateElePriceMsg;
import com.growatt.energymanagement.msgs.UpdateUserMsg;
import com.growatt.energymanagement.msgs.UserAllDevsMsg;
import com.growatt.energymanagement.msgs.UserNameExistMsg;
import com.growatt.energymanagement.msgs.WeatherMsg;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/9/11.
 *
 */

public class InternetUtils {
    private static final String host = "http://chat.growatt.com/eic_web/energy/";
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30,TimeUnit.SECONDS)
            .writeTimeout(30,TimeUnit.SECONDS)
            .build();
    private static MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    private static String access(String url, String params) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, params))
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        if (response == null) return "";
        int code = response.code();
        if (code != 200) return code + "";
        ResponseBody body = response.body();
        if (body == null) return "";
        try {
            return body.string();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 账号是否存在
     *
     * @param userName 账号名
     */
    public static void accountExist(final String userName) {
        final String url = host + "accountExist";
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("account", userName);
                    String s = InternetUtils.access(url, jsonObject.toString());
                    EventBus.getDefault().post(new UserNameExistMsg(s));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 登录
     *
     * @param userName 用户名
     * @param pwd      密码
     */
    public static void login(String userName, String pwd) {
        final String url = host + "login";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", userName);
            jsonObject.put("password", pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new LoginMsg(s));
            }
        }).start();
    }

    /**
     * 注册
     * @param password 密码
     * @param phone 手机号
     * @param country 国家
     * @param language 语言
     * @param company 公司名称
     * @param addr 地址
     * @param installerCode 安装商编码
     */
    public static void regist(String password, String phone, String country, String language
            , String company, String addr, String installerCode) {
        final String url = host + "regist";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("password", password);
            jsonObject.put("phone", phone);
            jsonObject.put("country", country);
            jsonObject.put("language", language);
            jsonObject.put("company", company);
            jsonObject.put("addr", addr);
            jsonObject.put("installerCode", installerCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new RegistMsg(s));
            }
        }).start();
    }

    /**
     * 第三方登录
     *
     * @param appType     平台类型
     * @param thirdUnique uid
     */
    public static void thirdLogin(String appType, String thirdUnique) {
        final String url = host + "thirdLogin";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("appType", appType);
            jsonObject.put("thirdUnique", thirdUnique);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new ThirdLoginMsg(s));
            }
        }).start();
    }

    /**
     * 发送验证码
     *
     * @param phone 手机号
     */
    public static void sendCode(String phone) {
        final String url = host + "sendCode";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new GetCodeMsg(s));
            }
        }).start();
    }

    /**
     * 第三方注册
     *
     * @param appType     平台类型
     * @param thirdUnique 平台返回的uid
     * @param phone       手机号
     * @param email       邮箱
     * @param company     公司名称
     * @param country     国家
     * @param language    语言
     * @param addr        详细地址
     * @param userType    用户类型
     */
    public static void thirdRegist(String appType, String thirdUnique, String phone, String email, String company, String country, String language, String addr, String userType) {
        final String url = host + "thirdRegist";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("appType", appType);
            jsonObject.put("thirdUnique", thirdUnique);
            jsonObject.put("phone", phone);
            jsonObject.put("email", email);
            jsonObject.put("company", company);
            jsonObject.put("country", country);
            jsonObject.put("language", language);
            jsonObject.put("addr", addr);
            jsonObject.put("userType", userType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new ThirdRegistMsg(s));
            }
        }).start();
    }

    /**
     * 修改用户信息
     *
     * @param key   修改的字段
     * @param value 新的值
     */
    public static void updateUser(String key, String value) {
        final String url = host + "updateUser";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(key, value);
            jsonObject.put("cid", LoginMsg.cid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new UpdateUserMsg(s));
            }
        }).start();
    }

    /**
     * 获取电价配置列表
     *
     * @param uniqueId 用户ID
     */
    public static void elePrice(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "elePrice";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new ElePriceMsg(s));
            }
        }).start();
    }

    public static void addElePrice(String uniqueId, String name, String time, double price, String effectiveTime, int status) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "addElePrice";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("name", name);
            jsonObject.put("time", time);
            jsonObject.put("price", price);
            jsonObject.put("effectiveTime", effectiveTime);
            jsonObject.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new AddElePriceMsg(s));
            }
        }).start();
    }

    /**
     * 获取电价配置信息
     *
     * @param uniqueId 用户唯一识别号
     */
    public static void elePriceInfo(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "elePriceInfo";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new ElePriceInfoMsg(s));
            }
        }).start();
    }

    /**
     * 分页获取通知信息
     *
     * @param uniqueId   用户id
     * @param page       页码
     * @param noticeType 通知类型
     * @param devType    设备类型
     */
    public static void notice(String uniqueId, int page, String noticeType, String devType) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "notice";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("page", page);
            jsonObject.put("noticeType", noticeType);
            jsonObject.put("devType", devType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new NotificationMsg(s));
            }
        }).start();
    }

    /**
     * 根据时间获取能源趋势数据（多种类型个时间节点图表）
     *
     * @param uniqueId 用户唯一识别码
     * @param time     时间选择器的时间
     * @param timeType 天2、月3、年4
     */
    public static void energyTendency(String uniqueId, final int timeType, final String time) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "energyTendency";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("timeType", timeType);
            jsonObject.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new EnergyTendencyMsg(s, timeType, time));
            }
        }).start();
    }

    /**
     * 获取各种设备类型对应的告警数量
     *
     * @param uniqueId 用户ID
     */
    public static void getDevWarningNum(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "getDevWarningNum";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new GetDevWarningNumMsg(s));
            }
        }).start();
    }

    /**
     * 获取当月环保效益
     *
     * @param uniqueId 用户ID
     */
    public static void greenBenifit(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "greenBenifit";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new GreenBenifitMsg(s));
            }
        }).start();
    }

    /**
     * 获取天气信息（可以返回多日或近期一周）
     *
     * @param adCode 城市
     */
    public static void weather(String adCode) {
        final String url = host + "weather";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("adCode", adCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().postSticky(new WeatherMsg(s));
            }
        }).start();
    }

    /**
     * 编辑电价配置信息
     *
     * @param cid           用户id
     * @param name          电价类型名称
     * @param price         单价
     * @param effectiveTime 有效期
     * @param t             时间段（开始和结束时间）
     */
    public static void updateElePrice(int cid, String name, String effectiveTime, String t, double price) {
        final String url = host + "updateElePrice";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cid", cid);
            jsonObject.put("name", name);
            jsonObject.put("price", price);
            jsonObject.put("time", t);
            jsonObject.put("effectiveTime", effectiveTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new UpdateElePriceMsg(s));
            }
        }).start();
    }

    /**
     * 获取单个设备日月年发电量信息
     *
     * @param devId 设备id
     */
    public static void generateElectricity(String devId, final int timeType, String info, final String time) {
        final String url = host + "generateElectricity";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("devId", devId);
            jsonObject.put("timeType", timeType);
            jsonObject.put("info", info);
            jsonObject.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new GenerateElectricityMsg(s, timeType, time));
            }
        }).start();
    }

    /**
     * 获取发电模块概览数据
     *
     * @param uniqueId 账户
     */
    public static void generateEleOverview(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "generateEleOverview";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new GenerateEleOverviewMsg(s));
            }
        }).start();
    }

    /**
     * 根据时间获取光伏系统发电信息和能源产耗
     *
     * @param uniqueId 账户
     * @param timeType 时间类型
     * @param time     时间
     */
    public static void outputAndInputOfEle(String uniqueId, final int timeType, final String time) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "outputAndInputOfEle";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("timeType", timeType);
            jsonObject.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new OutputAndInputOfEleMsg(s, timeType, time));
            }
        }).start();
    }

    /**
     * 获取国家列表接口
     */
    public static void countryData() {
        final String url = host + "countryData";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                Response response;
                try {
                    response = client.newCall(request).execute();
                    int code = response.code();
                    if (code != 200) return;
                    ResponseBody body = response.body();
                    if (body == null) return;
                    String s = body.string();
                    EventBus.getDefault().post(new CountryDataMsg(s));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 第三方绑定
     *
     * @param account     账号
     * @param password    密码
     * @param appType     平台类型
     * @param thirdUnique uid
     */
    public static void binding(final String account, final String password, String appType, String thirdUnique) {
        final String url = host + "binding";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", account);
            jsonObject.put("password", password);
            jsonObject.put("appType", appType);
            jsonObject.put("thirdUnique", thirdUnique);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new BindingMsg(s, account, password));
            }
        }).start();
    }

    /**
     * 首页
     *
     * @param uniqueId 用户ID
     */
    public static void home(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "home";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new HomeMsg(s));
            }
        }).start();
    }

    /**
     * 获取用户发电设备列表
     *
     * @param uniqueId 用户ID
     */
    public static void inverters(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "inverters";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new InvertersMsg(s));
            }
        }).start();
    }

    /**
     * 获取当前用户下电表设备列表信息
     *
     * @param uniqueId 用户ID
     */
    public static void ammeters(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "ammeters";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new AmmetersMsg(s));
            }
        }).start();
    }

    /**
     * 获取能源分析概览数据
     *
     * @param uniqueId 用户ID
     */
    public static void analysisData(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "analysisData";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new AnalysisDataMsg(s));
            }
        }).start();
    }

    /**
     * 添加采集器
     *
     * @param uniqueId 用户ID
     * @param devId    设备id
     * @param authCode 采集器校检码
     */
    public static void addCollector(String uniqueId, String devId, String authCode) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "addCollector";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("devId", devId);
            jsonObject.put("authCode", authCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new AddCollectorMsg(s));
            }
        }).start();
    }

    /**
     * 获取指定区域的所有发电设备
     *
     * @param uniqueId 用户ID
     * @param path     区域路径
     */
    public static void generateElectricitys(String uniqueId, String path) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "generateElectricitys";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            if (path != null && !path.equals("")) {
                jsonObject.put("path", path);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new GenerateElectricitysMsg(s));
            }
        }).start();
    }

    /**
     * 获取用户所有区域信息
     *
     * @param uniqueId 用户ID
     */
    public static void areaInfo(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "areaInfo";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new AreaInfoMsg(s));
            }
        }).start();
    }

    /**
     * 获取用户所有设备信息接口
     *
     * @param uniqueId 用户ID
     */
    public static void userAllDevs(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "userAllDevs";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new UserAllDevsMsg(s));
            }
        }).start();
    }

    /**
     * 获取发电模块统计数据
     *
     * @param uniqueId 用户ID
     */
    public static void statisticsData(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "statisticsData";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new StatisticsDataMsg(s));
            }
        }).start();
    }

    /**
     * 获取发电模块统计数据
     *
     * @param uniqueId 用户ID
     * @param devId    设备ID
     */
    public static void statisticsData(String uniqueId, String devId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "statisticsData";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("devId", devId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new StatisticsDataMsg(s));
            }
        }).start();
    }

    /**
     * 根据获取储能系统能源信息、能源产耗、充放电信息
     *
     * @param uniqueId 用户ID
     * @param devId    设备ID
     */
    public static void storageSystemData(String uniqueId, String devId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "storageSystemData";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("devId", devId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new StorageSystemDataMsg(s));
            }
        }).start();
    }

    /**
     * 根据时间（日月年）获取区域耗电量信息
     *
     * @param uniqueId 用户ID
     * @param timeType 日/月/年
     * @param time     时间
     */
    public static void areaEleRank(String uniqueId, int timeType, String time) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "areaEleRank";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("timeType", timeType);
            jsonObject.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new AreaEleRankMsg(s));
            }
        }).start();
    }

    /**
     * 根据设备类型和时间（日月年）获取单种设备类型日月年能耗数据
     *
     * @param uniqueId 用户ID
     * @param timeType 日/月/年
     * @param time     时间
     */
    public static void devTypeEleCost(String uniqueId, int timeType, String time) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "devTypeEleCost";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("timeType", timeType);
            jsonObject.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new DevTypeEleCostMsg(s));
            }
        }).start();
    }

    /**
     * 根据设备类型获取用户所有能耗设备概览数据
     *
     * @param uniqueId 用户ID
     */
    public static void devRunningState(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "devRunningState";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new DevRunningStateMsg(s));
            }
        }).start();
    }

    /**
     * 根据设备类型获取能耗列表详细数据
     *
     * @param uniqueId 用户ID
     * @param devType  设备类型
     */
    public static void devsDetailInfo(String uniqueId, String devType) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "devsDetailInfo";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("devType", devType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new DevsDetailInfoMsg(s));
            }
        }).start();
    }

    /**
     * 根据区域分类获取用户能耗概览数据
     *
     * @param uniqueId 用户ID
     */
    public static void areaDevsState(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "areaDevsState";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new AreaDevsStateMsg(s));
            }
        }).start();
    }

    /**
     * 根据时间（日月年）获取能耗电量信息
     *
     * @param uniqueId 用户ID
     * @param timeType 日/月/年
     * @param time     时间
     */
    public static void eleCost(String uniqueId, final int timeType, final String time, String path) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "eleCost";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("timeType", timeType);
            jsonObject.put("time", time);
            jsonObject.put("path", path);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new EleCostMsg(s));
            }
        }).start();
    }

    /**
     * 根据电表id和时间获取电表（三相电流/电压不平衡度）各时间节点数据
     *
     * @param ammeterId 电表ID
     * @param time      时间
     */
    public static void qualityData(String ammeterId, String time) {
        final String url = host + "qualityData";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ammeterId", ammeterId);
            jsonObject.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new QualityDataMsg(s));
            }
        }).start();
    }

    /**
     * 根据时间获取能源分析详情数据：（BMS电池、总数据、电网）功率和电量
     *
     * @param uniqueId 用户ID
     * @param timeType 日/月/年
     * @param time     时间
     */
    public static void analysisInfo(String uniqueId, final int timeType, final String time) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "analysisInfo";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("timeType", timeType);
            jsonObject.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new AnalysisInfoMsg(s, timeType));
            }
        }).start();
    }

    /**
     * 获取电站信息维护
     *
     * @param uniqueId 用户ID
     */
    public static void powerStation(String uniqueId) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "powerStation";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new PowerStationMsg(s));
            }
        }).start();
    }

    /**
     * 编辑电站信息
     *
     * @param map 参数列表
     */
    public static void editPowerStation(Map<String, Object> map) {
        final String url = host + "editPowerStation";
        final JSONObject jsonObject = new JSONObject();
        try {
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                jsonObject.put(key, map.get(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new EditPowerStationMsg(s));
            }
        }).start();
    }

    /**
     * 根据区域id和时间（日月年）获取区域详细耗电量信息
     * @param uniqueId 用户ID
     * @param timeType 时间类型(2\3\4)
     * @param time 时间值格式
    timeType:2/3/4
    time:日/月/年
    time:20181010/201810/2018
     * @param path 区域路径
     */
    public static void areaEleInfo(String uniqueId, final int timeType, final String time,String path) {
        if (uniqueId == null || uniqueId.length() <= 0) return;
        final String url = host + "areaEleInfo";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("timeType", timeType);
            jsonObject.put("time", time);
            jsonObject.put("path", path);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new AreaEleInfoMsg(s, timeType));
            }
        }).start();
    }

    public static void allArea() {
        final String url = host + "allArea";
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, "");
                EventBus.getDefault().post(new AllAreaMsg(s));
            }
        }).start();
    }

    /**
     * 找回密码
     * @param phone 手机号
     * @param password  密码
     */
    public static void forgetPwd(String phone,String password) {
        final String url = host + "forgetPwd";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", phone);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new ForgetPwdMsg(s));
            }
        }).start();
    }

    /**
     * 根据告警类型获取对应告警列表
     * @param uniqueId 用户ID
     * @param type 0、1、2、3 对应 全部、发电设备告警、掉线告警、用电告警
     */
    public static void noticeList(String uniqueId,int type) {
        final String url = host + "noticeList";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueId", uniqueId);
            jsonObject.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new NoticeListMsg(s));
            }
        }).start();
    }

    /**
     * 根据通知id获取详情数据
     * @param noticeType 消息类型(report、warning)
     * @param cid 消息id
     * @param devType 设备类型(ameter、inverter)
     */
    public static void noticeInfo(String noticeType,int cid,String devType) {
        final String url = host + "noticeInfo";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("noticeType", noticeType);
            jsonObject.put("cid", cid);
            jsonObject.put("devType", devType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new NoticeInfoMsg(s));
            }
        }).start();
    }

    /**
     * 根据设备id和时间获取能耗详细数据
     * @param devId 设备id(序列号)
     * @param devType 设备类型
     */
    public static void devDetailInfo(String devId,String devType) {
        final String url = host + "devDetailInfo";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("devId", devId);
            jsonObject.put("devType", devType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new DevDetailInfoMsg(s));
            }
        }).start();
    }

    /**
     * 修改单个设备各参数信息
     * @param devId 设备编号
     * @param devType kt(空调)、wkq(温控器)、cz(插座)
     * @param onoff 开1 关0
     */
    public static void setting(String devId,String devType,int onoff) {
        final String url = host + "setting";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("devType", devType);
            JSONObject settings = new JSONObject();
            settings.put("devId", devId);
            settings.put("onoff", onoff);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = InternetUtils.access(url, jsonObject.toString());
                EventBus.getDefault().post(new SettingMsg(s));
            }
        }).start();
    }
}
