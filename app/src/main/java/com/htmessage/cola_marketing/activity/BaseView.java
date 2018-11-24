package com.htmessage.cola_marketing.activity;

import android.app.Activity;
import android.content.Context;


public interface BaseView<T> {
    void setPresenter(T presenter);
    Context getBaseContext();
    Activity getBaseActivity();
}
