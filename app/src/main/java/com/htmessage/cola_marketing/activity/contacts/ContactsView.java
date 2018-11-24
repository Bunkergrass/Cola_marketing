package com.htmessage.cola_marketing.activity.contacts;

import com.htmessage.cola_marketing.activity.BaseView;
import com.htmessage.cola_marketing.domain.User;


public interface ContactsView extends BaseView<ContactsPresenter> {

    void showItemDialog(User user);

    void showSiderBar();

    void showInvitionCount(int count);

    void showContactsCount(int count);

    void refresh();

}
