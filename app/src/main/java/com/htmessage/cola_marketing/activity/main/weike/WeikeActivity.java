package com.htmessage.cola_marketing.activity.main.weike;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.widget.swipyrefresh.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.List;


public class WeikeActivity extends BaseActivity implements WeikeContract.View, SwipyRefreshLayout.OnRefreshListener {
    RecyclerView rv_weike;
    SwipyRefreshLayout srl_weike;

    WeikeAdapter weikeAdapter;
    WeikeContract.Presenter presenter;
    static int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weike);
        setTitle("资源整合");

        srl_weike = findViewById(R.id.srl_weike);
        rv_weike = findViewById(R.id.rv_weike);

        setView();
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View root = inflater.inflate(R.layout.activity_weike, container, false);
//        srl_weike = root.findViewById(R.id.srl_weike);
//        rv_weike = root.findViewById(R.id.rv_weike);
//        setView();
//        return root;
//    }

    @Override
    public void onResume() {
        super.onResume();
        presenter = new WeikePresenter(this);
        getData();
    }

    public void getData() {
        presenter.getWeikeList(page, true);
    }

    private void setView() {
        srl_weike.setOnRefreshListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        weikeAdapter = new WeikeAdapter(this,new ArrayList<JSONObject>());
        rv_weike.setLayoutManager(manager);
        rv_weike.setAdapter(weikeAdapter);
    }

    @Override
    public void onRefresh(int index) {
        page = 1;
        presenter.getWeikeList(page, true);
    }

    @Override
    public void onLoad(int index) {
        page ++;
        presenter.getWeikeList(page, false);
    }

    @Override
    public void setPresenter(WeikeContract.Presenter presenter) {}

    @Override
    public Activity getBaseActivity() {
        return this;
    }

    @Override
    public void stopRefresh() {
        srl_weike.setRefreshing(false);
    }

    @Override
    public void loadMore(List<JSONObject> list) {
        weikeAdapter.addItems(list);
    }

    @Override
    public void refreshList(List<JSONObject> list) {
        weikeAdapter.refreshList(list);
    }
}