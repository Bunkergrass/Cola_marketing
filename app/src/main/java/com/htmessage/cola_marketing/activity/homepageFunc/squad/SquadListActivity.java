package com.htmessage.cola_marketing.activity.homepageFunc.squad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.chat.group.GroupAddMembersActivity;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.swipyrefresh.SwipyRefreshLayout;
import com.htmessage.sdk.model.HTGroup;

import java.util.ArrayList;
import java.util.List;

public class SquadListActivity extends BaseActivity implements View.OnClickListener, SwipyRefreshLayout.OnRefreshListener {
    private RecyclerView rv_squads;
    private SwipyRefreshLayout srl_squads;
    private TextView tv_none;
    private LinearLayout ll_new_group,ll_top_groups,ll_my_groups;

    private SquadListAdapter adapter;
    private List<JSONObject> list = new ArrayList<>();
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squad_list);
        setTitle("战略小组");

//        ll_my_groups = findViewById(R.id.ll_my_groups);
        ll_new_group = findViewById(R.id.ll_new_group);
//        ll_top_groups = findViewById(R.id.ll_top_groups);
        rv_squads = findViewById(R.id.rv_squad_list);
        srl_squads = findViewById(R.id.srl_squads);
        tv_none = findViewById(R.id.tv_none_squad);
        setView();
    }

    private void setView() {
        srl_squads.setOnRefreshListener(this);
        ll_new_group.setOnClickListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new SquadListAdapter(this,list);
        rv_squads.setLayoutManager(manager);
        rv_squads.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        requestSquadList(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_new_group:
                startActivity(new Intent(SquadListActivity.this, GroupAddMembersActivity.class));
                break;
        }
    }

    @Override
    public void onRefresh(int index) {
        page = 1;
        requestSquadList(1);
    }

    @Override
    public void onLoad(int index) {
        page++;
        requestSquadList(page);
    }

    private void requestSquadList(final int page) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",HTApp.getInstance().getUsername()));
        params.add(new Param("type","1"));
        params.add(new Param("page",page+""));
        new OkHttpUtils(this).post(params, HTConstant.URL_GROUP_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                srl_squads.setRefreshing(false);
                Log.d("requestSquadList",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        tv_none.setVisibility(View.GONE);
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
                        Toast.makeText(SquadListActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                requestSquadList(page);
            }
        });
    }

}
