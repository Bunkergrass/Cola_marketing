package com.htmessage.cola_marketing.activity.myLists.myMarkets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.myLists.ListAdapterListener;
import com.htmessage.cola_marketing.activity.myLists.myApplies.MyAppliesActivity;
import com.htmessage.cola_marketing.utils.CommonUtils;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.swipyrefresh.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MyMarketsActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private RecyclerView rv_market;
    private SwipyRefreshLayout srl_market;

    private MarketsAdapter adapter;
    private List<JSONObject> list = new ArrayList<>();
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
        adapter = new MarketsAdapter(MyMarketsActivity.this,list);
        rv_market.setLayoutManager(manager);
        rv_market.setAdapter(adapter);

        adapter.setListener(new ListAdapterListener() {
            @Override
            public void onClick(int position, View itemView) {}

            @Override
            public void onLongClick(final int position, View itemView) {
                CommonUtils.showDeleteDialog(MyMarketsActivity.this, new CommonUtils.AlertDialogCallback() {
                    @Override
                    public void onPositive() {
                        deleteMarket(position, list.get(position).getString("onlygood_id"));
                    }

                    @Override
                    public void onNegative() {}
                });
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
                        if (page == 1) {
                            list.clear();
                            list.addAll(JSONArray.parseArray(data.toJSONString(),JSONObject.class));
                        } else {
                            list.addAll(JSONArray.parseArray(data.toJSONString(),JSONObject.class));
                        }
                        adapter.notifyDataSetChanged();
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

    private void deleteMarket(final int pos, String onlygood_id) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("onlygood_id", onlygood_id));
        new OkHttpUtils(this).post(params, HTConstant.URL_DELETE_ONLYGOOD, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("deleteMarket",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        list.remove(pos);
                        adapter.notifyItemRemoved(pos);
                        break;
                    default:
                        Toast.makeText(MyMarketsActivity.this,R.string.delete_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(MyMarketsActivity.this,R.string.delete_failed,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
