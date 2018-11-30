package com.htmessage.cola_marketing.activity.main;

import com.htmessage.cola_marketing.activity.BaseView;


public interface MainView extends BaseView<MainPrestener> {

    void showConflicDialog();

    void showUpdateDialog(String message, String url, boolean isForce);
}
