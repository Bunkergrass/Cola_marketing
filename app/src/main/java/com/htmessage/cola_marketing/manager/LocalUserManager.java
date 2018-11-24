package com.htmessage.cola_marketing.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class LocalUserManager {
    /**
     * 保存Preference的name
     */
    public static final String PREFERENCE_NAME = "userInfo";
    private static SharedPreferences mSharedPreferences;
    private static LocalUserManager mPreferencemManager;
    private static SharedPreferences.Editor editor;
    private String SHARED_KEY_USER_INFO = "shared_key_user_info";
    private static Context context;
    private String SHARED_KEY_IS_VERSION_CHECKED = "shared_key_is_version_checked";

    private LocalUserManager(Context cxt) {
        this.context = cxt;
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static synchronized void init(Context cxt) {
        if (mPreferencemManager == null) {
            mPreferencemManager = new LocalUserManager(cxt);
        }
    }

    /**
     * 单例模式，获取instance实例
     *
     * @param
     * @return
     */
    public synchronized static LocalUserManager getInstance() {
        Log.d("CurrentUserManager---->", context.getPackageName());
        if (mPreferencemManager == null) {
            throw new RuntimeException("please init first!");
        }
        return mPreferencemManager;
    }


    public void setUserJson(JSONObject userJson) {
        String userInfo = "";
        if (userJson != null) {
            try {
                userInfo = userJson.toJSONString();
            } catch (JSONException e) {
            }
        }
        editor.putString(SHARED_KEY_USER_INFO, userInfo);
        editor.commit();
    }

    public JSONObject getUserJson() {
        JSONObject userJson = null;

        String userStr = mSharedPreferences.getString(SHARED_KEY_USER_INFO, null);
        if (userStr != null) {
            userJson = JSONObject.parseObject(userStr);

        }
        return userJson;
    }

    public void saveVersionDialog(boolean isShow) {
        editor.putBoolean(SHARED_KEY_IS_VERSION_CHECKED, isShow);
        editor.commit();
    }

    public boolean getVersionCheck() {
        return mSharedPreferences.getBoolean(SHARED_KEY_IS_VERSION_CHECKED, false);
    }
}
