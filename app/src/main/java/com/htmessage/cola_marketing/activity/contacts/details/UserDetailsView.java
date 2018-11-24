package com.htmessage.cola_marketing.activity.contacts.details;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.activity.BaseView;

/**
 * 类描述：UserDetailsView
 */
public interface UserDetailsView extends BaseView<UserDetailsPrester> {
    void onRefreshSuccess(String msg);
    void onRefreshFailed(String error);
    String getFxid();
    JSONObject getUserJson();
    void showDialog();
    void hintDialog();
    void showUi(JSONObject object);
}
