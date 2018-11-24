package com.htmessage.cola_marketing.activity.myLists.myMarkets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.htmessage.cola_marketing.activity.myLists.myApplies.MyAppliesActivity;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.swipyrefresh.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MyMarketsActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private RecyclerView rv_market;
    private SwipyRefreshLayout srl_market;

    private MarketsAdapter adapter;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_markets);
        setTitle("我的销售");

        rv_market = findViewById(R.id.rv_markets);
        srl_market = findViewById(R.id.srl_markets);
        setView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        requestMartketList(page);
    }

    private void setView() {
        srl_market.setOnRefreshListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new MarketsAdapter(MyMarketsActivity.this,new ArrayList<JSONObject>());
        rv_market.setLayoutManager(manager);
        rv_market.setAdapter(adapter);
    }

    private void requestMartketList(final int page) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("page",page+""));
        new OkHttpUtils(MyMarketsActivity.this).post(params, HTConstant.URL_MY_SALE_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("requestMartketList",jsonObject.toString());
                srl_market.setRefreshing(false);
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        JSONArray data = jsonObject.getJSONArray("data");
                        List<JSONObject> list = JSONArray.parseArray(data.toJSONString(),JSONObject.class);
                        if (page == 1) {
                            adapter.refreshList(list);
                        } else {
                            adapter.addList(list);
                        }
                        break;
                    default:
                        Toast.makeText(MyMarketsActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                srl_market.setRefreshing(false);
                Toast.makeText(MyMarketsActivity.this,R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh(int index) {
        page = 1;
        requestMartketList(page);
    }

    @Override
    public void onLoad(int index) {
        page ++;
        requestMartketList(page);
    }
}
