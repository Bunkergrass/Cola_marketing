package com.htmessage.cola_marketing.activity.main.my.update;

import com.htmessage.cola_marketing.activity.BaseView;

public interface UpdateProfileView extends BaseView<UpdateProfilePrestener> {
    String getDefultString();
    int getType();
    String getInputString();
    void onUpdateSuccess(String msg);
    void onUpdateFailed(String msg);
}
