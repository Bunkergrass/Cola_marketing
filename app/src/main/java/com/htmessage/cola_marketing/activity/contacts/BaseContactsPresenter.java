package com.htmessage.cola_marketing.activity.contacts;

import com.htmessage.cola_marketing.activity.BasePresenter;
import com.htmessage.cola_marketing.domain.User;

import java.util.List;


public interface BaseContactsPresenter extends BasePresenter {

    List<User> getContactsListInDb();

    void deleteContacts(String userId);

    void moveUserToBlack(String userId);

    List<User> sortList(List<User> users);

    void refreshContactsInServer();

    int getInvitionCount();

    int getContactsCount();

    void clearInvitionCount();

    void refreshContactsInLocal();
}
