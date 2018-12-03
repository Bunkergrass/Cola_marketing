package com.htmessage.cola_marketing.activity.homepageFunc.baokuan;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.swipyrefresh.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.List;


public class BaokuanActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private RecyclerView rv_baokuan;
    private SwipyRefreshLayout srl_baokuan;
    SwipeRefreshLayout s;

    private BaokuanAdapter adapter;
    private List<JSONObject> list = new ArrayList<>();

    private int page = 1;
    private static final String REQUEST_TYPE = "1";//1：爆款打造

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baokuan);
        setTitle("爆款打造");

        srl_baokuan = findViewById(R.id.srl_baokuan);
        rv_baokuan = findViewById(R.id.rv_baokuan);
        setView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        requestBaokuanList(page);
    }

    private void setView() {
        srl_baokuan.setOnRefreshListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new BaokuanAdapter(this,list);
        rv_baokuan.setLayoutManager(manager);
        rv_baokuan.setAdapter(adapter);
    }

    private void requestBaokuanList(final int page) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("page",page+""));
        params.add(new Param("type",REQUEST_TYPE));
        new OkHttpUtils(this).post(params, HTConstant.URL_TRADE_ONLY_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                srl_baokuan.setRefreshing(false);
                Log.d("requestBaokuanList",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        JSONArray data = jsonObject.getJSONArray("data");
                        if (page == 1) {
                            list.clear();
                            list.addAll(JSONArray.parseArray(data.toJSONString(),JSONObject.class));
                        } else {
                            list.addAll(JSONArray.parseArray(data.toJSONString(),JSONObject.class));
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        Toast.makeText(BaokuanActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                srl_baokuan.setRefreshing(false);
                Toast.makeText(BaokuanActivity.this,R.string.request_failed_msg + errorMsg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh(int index) {
        page = 1;
        requestBaokuanList(page);
    }

    @Override
    public void onLoad(int index) {
        page ++;
        requestBaokuanList(page);
    }
}
