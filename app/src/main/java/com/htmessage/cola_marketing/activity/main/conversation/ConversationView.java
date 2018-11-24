package com.htmessage.cola_marketing.activity.main.conversation;

import com.htmessage.cola_marketing.activity.BaseView;
import com.htmessage.sdk.model.HTConversation;


public interface ConversationView extends BaseView<ConversationPresenter> {

    void showItemDialog(HTConversation htConversation);
    void refresh();
}
