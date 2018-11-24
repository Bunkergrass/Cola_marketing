package com.htmessage.cola_marketing.activity.login.password;

import com.htmessage.cola_marketing.activity.BaseView;

public interface PasswordView extends BaseView<PasswordPrester> {
    String getCountryName();
    String getCountryCode();
    String getCacheCode();
    String getSMSCode();
    boolean getIsReset();
    String getMobile();
    String getPwd();
    String getConfimPwd();
    void clearCacheCode();
    void onSendSMSCodeSuccess(String msg);
    void onSendSMSCodeFailed(String error);
    void onResetSuccess(String msg);
    void onResetFailed(String error);
    void startTimeDown();
    void finishTimeDown();
    void onLogOutFailed(String msg);
}
