package com.htmessage.cola_marketing.activity.main.conversation;

import com.htmessage.cola_marketing.activity.BasePresenter;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTMessage;

import java.util.List;


public interface BaseConversationPresenter extends BasePresenter {
    List<HTConversation> getAllConversations();
    void deleteConversation(HTConversation conversation);
    void setTopConversation(HTConversation conversation);
    void cancelTopConversation(HTConversation conversation);
    void refreshConversations();
    void onNewMsgReceived(HTMessage htmessage);
    int getUnreadMsgCount();
    void markAllMessageRead(HTConversation conversation);
}
