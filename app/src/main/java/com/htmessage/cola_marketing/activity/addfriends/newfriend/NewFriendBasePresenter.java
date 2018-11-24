package com.htmessage.cola_marketing.activity.addfriends.newfriend;

import android.content.Context;

import com.htmessage.cola_marketing.activity.BasePresenter;
import com.htmessage.cola_marketing.domain.InviteMessage;

import java.util.List;

/**
 * 类描述：NewFriendBasePresenter 描述:
 */
public interface NewFriendBasePresenter extends BasePresenter {
    List<InviteMessage> getAllInviteMessage();
    void registerRecivier();
    void startActivity(Context context, Class clazz);
    void saveUnreadMessageCount(int count);
    void refresh();
    void onDestory();
}
