package com.htmessage.cola_marketing.activity.main.my.region;

import com.htmessage.cola_marketing.activity.BasePresenter;

import java.util.List;


public interface RegionBasePrestener extends BasePresenter {
    List<String> getProvinceList();
    List<String> getCityList(int position);
    void onDestory();
}
