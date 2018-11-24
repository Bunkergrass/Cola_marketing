package com.htmessage.cola_marketing.activity.main.my.profile;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.activity.BaseView;


public interface ProfileView extends BaseView<ProfilePrester> {
    void onNickUpdate(String nick, boolean isHang);
    void onSexUpdate(int sex, boolean isHang);
    void onSignUpdate(String sign, boolean isHang);
    void onAvatarUpdate(String avatar, boolean isHang);
    void onRegionUpdate(String region, boolean isHang);
    void onFxidUpdate(String fxid, boolean isHang);
    void onUpdateSuccess(String msg);
    void onUpdateFailed(String error);
    JSONObject getUserJson();
}
