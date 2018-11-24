package com.htmessage.cola_marketing.activity.main.my.region;

import com.htmessage.cola_marketing.activity.BaseView;

import java.util.List;


public interface RegionView extends BaseView<RegionPresenter> {
    void showCityList(List<String> cityList);
    void showProvince(String province);
    void showCity(String city);
}
