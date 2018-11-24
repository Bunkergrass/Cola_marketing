package com.htmessage.cola_marketing.activity.main.my.profile;

import android.content.Intent;

import com.htmessage.cola_marketing.activity.BasePresenter;


public interface ProfileBasePrester  extends BasePresenter {
    void resgisteRecivier();
    void showPhotoDialog();
    void showSexDialog();
    void result(int requestCode, int resultCode, Intent intent);
    void onDestory();
}
