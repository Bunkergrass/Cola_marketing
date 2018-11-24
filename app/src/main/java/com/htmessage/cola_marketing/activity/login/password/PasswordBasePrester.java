package com.htmessage.cola_marketing.activity.login.password;

import com.htmessage.cola_marketing.activity.BasePresenter;

public interface PasswordBasePrester  extends BasePresenter {
    void sendSMSCode(String mobile, String countryName, String countryCode);
    void resetPassword(String cacheCode, String smsCode, String password, String confimPwd, String mobile);
}
