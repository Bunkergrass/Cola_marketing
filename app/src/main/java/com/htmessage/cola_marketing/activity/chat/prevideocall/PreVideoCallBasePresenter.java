package com.htmessage.cola_marketing.activity.chat.prevideocall;


import com.htmessage.cola_marketing.activity.BasePresenter;
import com.htmessage.cola_marketing.domain.User;

import java.util.List;

/**
 * 项目名称：yichat0504
 * 类描述：PreVideoCallBasePresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/11 10:33
 * 邮箱:814326663@qq.com
 */
public interface PreVideoCallBasePresenter extends BasePresenter {
    void getGroupMembers(String groupId);
    List<String> getUserIds();
    List<User> getUsers();
    List<User> getExitUsers();
    void showCheckedDialog();
    String getCallId();
    void onDestory();

}
