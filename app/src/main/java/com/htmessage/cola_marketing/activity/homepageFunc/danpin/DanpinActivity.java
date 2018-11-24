package com.htmessage.cola_marketing.activity.homepageFunc.danpin;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.main.widget.TradeItemView;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.swipyrefresh.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class DanpinActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private RecyclerView rv_danpin;
    private SwipyRefreshLayout srl_danpin;

    private DanpinAdapter adapter;
    private int page = 1;

    private static final String REQUEST_TYPE = "0";//0：单品市场

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danpin);
        setTitle("单品市场");

        srl_danpin = findViewById(R.id.srl_danpin);
        rv_danpin = findViewById(R.id.rv_danpin);
        setView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        requestDanpinList(page);
    }

    private void setView() {
        srl_danpin.setOnRefreshListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new DanpinAdapter(this,new ArrayList<JSONObject>());
        rv_danpin.setLayoutManager(manager);
        rv_danpin.setAdapter(adapter);
    }

    private void requestDanpinList(final int page) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("page",page+""));
        params.add(new Param("type",REQUEST_TYPE));
        new OkHttpUtils(this).post(params, HTConstant.URL_TRADE_ONLY_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                srl_danpin.setRefreshing(false);
                Log.d("requestAppliesList",jsonObject.toString());
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
                        Toast.makeText(DanpinActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                srl_danpin.setRefreshing(false);
                Toast.makeText(DanpinActivity.this,R.string.request_failed_msg + errorMsg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh(int index) {
        page = 1;
        requestDanpinList(page);
    }

    @Override
    public void onLoad(int index) {
        page ++;
        requestDanpinList(page);
    }


    private static class DanpinAdapter extends RecyclerView.Adapter<DanpinAdapter.ViewHolder> {
        Context context;
        List<JSONObject> list;

        public DanpinAdapter(Context context, List<JSONObject> list) {
            this.context = context;
            this.list = list;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TradeItemView trade;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                trade = (TradeItemView) itemView;
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            TradeItemView view = new TradeItemView(context);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.trade.initView(list.get(i),context);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void addList(List<JSONObject> list) {
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        public void refreshList(List<JSONObject> list) {
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }

    }
}
