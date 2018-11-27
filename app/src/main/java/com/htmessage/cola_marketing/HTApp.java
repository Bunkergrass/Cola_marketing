package com.htmessage.cola_marketing;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.manager.ContactsManager;
import com.htmessage.cola_marketing.manager.LocalUserManager;
import com.htmessage.cola_marketing.manager.NotifierManager;
import com.htmessage.cola_marketing.manager.PreferenceManager;
import com.htmessage.cola_marketing.manager.SettingsManager;
import com.htmessage.cola_marketing.utils.CommonUtils;
import com.jrmf360.tools.JrmfClient;
import com.tencent.bugly.crashreport.CrashReport;

import org.anyrtc.rtmpc_hybrid.RTMPCHybird;
import org.jivesoftware.smack.util.MD5;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class HTApp extends Application {
    private static Context applicationContext;
    private static Handler handler;
    private static int mainThreadId;
    private static HTApp instance;

    private JSONObject userJson = null;
    private List<Activity> activities = new ArrayList<>();
    public static boolean isCalling = false;
    private String thirdToken = "";

    @Override
    public void onCreate() {
        //sdk采用双进程守护,因此不要在守护进程中初始Application
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid, this);
        if (processAppName == null || !processAppName.equalsIgnoreCase(this.getPackageName())) {
            return;
        }
        super.onCreate();

        applicationContext = this;
        instance = this;
        LocalUserManager.init(applicationContext);
        PreferenceManager.init(applicationContext);
        ContactsManager.init(applicationContext);
        SettingsManager.init(applicationContext);
        NotifierManager.init(applicationContext);
        CrashReport.initCrashReport(applicationContext, HTConstant.BUGLY_KEY, false);  //腾讯bugly
        HTClientHelper.init(applicationContext);
        //2. 创建主handler
        handler = new Handler(Looper.getMainLooper());
        //3. 获取主线程id
        mainThreadId = android.os.Process.myTid();

       /**
         * 初始化RTMPC引擎
        */
        RTMPCHybird.Inst().Init(applicationContext);
        RTMPCHybird.Inst().InitEngineWithAnyrtcInfo(HTConstant.DEVELOPERID, HTConstant.APPID, HTConstant.APPKEY, HTConstant.APPTOKEN);

//        //红包
        JrmfClient.isDebug(true);
//        JrmfClient.setWxAppid(HTConstant.WX_APP_ID);
//        //初始化项目
        JrmfClient.init(this);
//        //设置微信的appid，如果不使用微信支付可以不调用此方法
    }

    /**
     * 金融魔方配置
     */
    public String getThirdToken() {
        if (TextUtils.isEmpty(thirdToken)) {
            thirdToken = MD5.hex(getUsername() + HTConstant.MOFANG_SECRET);
        }
        return thirdToken;
    }

    public String getMD5MFSign(String timeStamp) {
        String content = "cust_id:" + HTApp.getInstance().getUsername() + "|customkey:" + HTConstant.MOFANG_KEY + "|mobiletelno:" + HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_TEL) + "|seckey:" + HTConstant.MOFANG_SECRET1 + "|timeStamp:" + timeStamp;
        return CommonUtils.getMD5String(content);
    }

    public String getMD5SaiKunSign(String timeStamp) {
        String content = "cust_id:" + HTApp.getInstance().getUsername() + "|customkey:" + HTConstant.SAIKUN_BAOXIANCHAOSHI_KEY_ONLINE + "|mobiletelno:" + HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_TEL) + "|seckey:" + HTConstant.MOFANG_SECRET_BAOXIANCHAOSHI_ONLINE + "|timeStamp:" + timeStamp;
        return CommonUtils.getMD5String(content);
    }

    public String getSignYinHangJiJinUrl(String timeStamp) {
        return HTConstant.BASE_YINHANGJIJIN_URL + "?mobiletelno=" + HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_TEL)
                + "&cust_id=" + HTApp.getInstance().getUsername() + "&customkey=" + HTConstant.MOFANG_KEY + "&timeStamp=" + timeStamp + "&sign=" + getMD5MFSign(timeStamp);
    }

    public String getSignBaoxianchaoshiUrl(String timeStamp) {
        return HTConstant.BASE_BAOXIANCHAOSHI_URL + "?mobiletelno=" + HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_TEL)
                + "&cust_id=" + HTApp.getInstance().getUsername() + "&customkey=" + HTConstant.SAIKUN_BAOXIANCHAOSHI_KEY_ONLINE + "&timeStamp=" + timeStamp + "&sign=" + getMD5SaiKunSign(timeStamp);
    }

    public void clearThirdToken() {
        thirdToken = null;
    }


    //获取需要提醒的活动数量
    public int getActivityCount(String groupId) {
        return PreferenceManager.getInstance().getActivityCount(groupId);
    }

    public void clearActivityCount(String groupId) {
        PreferenceManager.getInstance().setActivityCount(groupId, 0);
    }


    public static Context getContext() {
        return applicationContext;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }

    public static HTApp getInstance() {
        return instance;
    }

    public String getUsername() {
        String username = null;
        if (getUserJson() != null) {
            username = getUserJson().getString(HTConstant.JSON_KEY_HXID);
        }
        return username;
    }

    public String getUserNick() {
        String username = null;
        if (getUserJson() != null) {
            username = getUserJson().getString(HTConstant.JSON_KEY_NICK);
            if (TextUtils.isEmpty(username)) {
                username = getUsername();
            }
        }
        return username;
    }

    public String getUserAvatar() {
        String username = null;
        if (getUserJson() != null) {
            username = getUserJson().getString(HTConstant.JSON_KEY_AVATAR);
        }
        return username;
    }

    public String getUserTel() {
        String username = null;
        if (getUserJson() != null) {
            username = getUserJson().getString(HTConstant.JSON_KEY_TEL);
        }
        return username;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this); not needed after sdk 21
    }

    public void setUserJson(JSONObject userJson) {
        this.userJson = userJson;
        LocalUserManager.getInstance().setUserJson(userJson);
    }

    public JSONObject getUserJson() {
        if (userJson == null) {
            userJson = LocalUserManager.getInstance().getUserJson();
        }
        return userJson;
    }


    public static String getNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String time = format.format(date);
        return time;
    }


    public void saveActivity(Activity activity) {
        if (activity != null) {
            activities.add(activity);
        }
    }

    public void finishActivities() {
        for (Activity activity : activities) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }


    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     * @param pID
     * @return
     */
    private static String getAppName(int pID, Context appContext) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
    }

    /**
     * 得到自定义的progressDialog
     * @param context
     * @param msg
     * @return
     */
    public Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
        LinearLayout layout = v.findViewById(R.id.dialog_loading);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = v.findViewById(R.id.iv_dialog_loading);
        TextView tipTextView = v.findViewById(R.id.tv_dialog_loading);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息
        Dialog loadingDialog = new Dialog(context, R.style.dialog_loading);// 创建自定义样式dialog
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局

        return loadingDialog;
    }

    public String getDirFilePath() {
        File file = new File(HTConstant.DIR_AVATAR);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.mkdir();
        }
        return file.getAbsolutePath() + File.separator;
    }
}
