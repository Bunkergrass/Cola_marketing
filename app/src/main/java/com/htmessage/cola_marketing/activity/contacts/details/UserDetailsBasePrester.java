package com.htmessage.cola_marketing.activity.contacts.details;

import com.htmessage.cola_marketing.activity.BasePresenter;

/**
 * 类描述：UserDetailsBasePrester 描述:
 */

public interface UserDetailsBasePrester extends BasePresenter {
        void onDestory();
        void refreshInfo(String userId, boolean backTask);
        boolean isMe(String userId);
        boolean isFriend(String userId);
}
