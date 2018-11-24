package com.htmessage.cola_marketing.activity.main.my.update;


public interface UpdateProfileBasePrester {
    void update();
    void updateInfo(String key, String value, String defaultStr);
    String getTitle(int type);
    String getKey(int type);
    void onDestory();
}
