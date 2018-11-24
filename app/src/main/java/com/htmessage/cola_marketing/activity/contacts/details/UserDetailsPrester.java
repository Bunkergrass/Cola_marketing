package com.htmessage.cola_marketing.activity.contacts.details;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.domain.User;
import com.htmessage.cola_marketing.manager.ContactsManager;
import com.htmessage.cola_marketing.utils.CommonUtils;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class UserDetailsPrester implements UserDetailsBasePrester {

    private UserDetailsView detailsView;

    public UserDetailsPrester(UserDetailsView detailsView) {
        this.detailsView = detailsView;
        this.detailsView.setPresenter(this);
    }

    @Override
    public void onDestory() {
        detailsView = null;
    }

    @Override
    public void refreshInfo(final String userId, final boolean backTask) {
        if (!backTask){
            detailsView.showDialog();
        }
        List<Param> parms = new ArrayList<>();
        parms.add(new Param("userId", userId));
        new OkHttpUtils(detailsView.getBaseContext()).post(parms, HTConstant.URL_Get_UserInfo, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getInteger("code");
                if (!backTask){
                    detailsView.hintDialog();
                }
                switch (code){
                    case 1:
                        JSONObject json = jsonObject.getJSONObject("user");
                        //刷新ui
                        if (isFriend(userId)) {
                            User user = CommonUtils.Json2User(json);
                            Map<String, User> contactList = ContactsManager.getInstance().getContactList();
                            User user1 = contactList.get(userId);
                            user1.setAvatar(user.getAvatar());
                            user1.setNick(user.getNick());
                            contactList.put(userId, user1);
                            ContactsManager.getInstance().saveContact(user1);
                            ArrayList<User> users1 = new ArrayList<>(contactList.values());
                            ContactsManager.getInstance().saveContactList(users1);
                        }
                        detailsView.showUi(json);
                        break;
                    default:
                        detailsView.hintDialog();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                detailsView.onRefreshFailed(errorMsg);
                if (!backTask){
                    detailsView.hintDialog();
                }
            }
        });
    }

    @Override
    public boolean isMe(String userId) {
        return HTApp.getInstance().getUsername().equals(userId);
    }

    @Override
    public boolean isFriend(String userId) {
        return ContactsManager.getInstance().getContactList().containsKey(userId);
    }

    @Override
    public void start() {}

}
