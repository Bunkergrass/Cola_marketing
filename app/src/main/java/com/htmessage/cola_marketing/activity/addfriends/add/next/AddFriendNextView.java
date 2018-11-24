package com.htmessage.cola_marketing.activity.addfriends.add.next;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.activity.BaseView;

/**
 * 类描述：AddFriendNextView 描述:
 */
public interface AddFriendNextView extends BaseView<AddFriendNextPrestener> {
    String getInputString();
    void onSearchSuccess(JSONObject object);
    void onSearchFailed(String error);
}
