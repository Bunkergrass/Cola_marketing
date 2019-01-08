package com.htmessage.cola_marketing.activity.homepageFunc.tutor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.main.weike.WeikeContract;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.swipyrefresh.SwipyRefreshLayout;
import com.htmessage.sdk.client.HTClient;

import java.util.ArrayList;
import java.util.List;

public class MyTutorListActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private TextView tv_none_tutor;
    private MyTutorsAdapter adapter;
    private List<JSONObject> list = new ArrayList<>();
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tutor_list);
        setTitle("导师组");

        SwipyRefreshLayout srl = findViewById(R.id.srl_my_tutors);
        RecyclerView rl_tutors = findViewById(R.id.rv_my_tutors);
        tv_none_tutor = findViewById(R.id.tv_none_tutor);
        RelativeLayout rl_new_tutor = findViewById(R.id.rl_new_tutor);

        srl.setOnRefreshListener(this);

        adapter = new MyTutorsAdapter(this,list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rl_tutors.setLayoutManager(manager);
        rl_tutors.setAdapter(adapter);

        rl_new_tutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyTutorListActivity.this,NewTutorActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        requestTutorList(page);
    }

    @Override
    public void onRefresh(int index) {
        page = 1;
        requestTutorList(page);
    }

    @Override
    public void onLoad(int index) {
        page ++;
        requestTutorList(page);
    }

    private void requestTutorList(final int page) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",HTApp.getInstance().getUsername()));
        params.add(new Param("type","2"));
        params.add(new Param("page",page+""));
        new OkHttpUtils(this).post(params, HTConstant.URL_GROUP_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("getMyTutorList",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        tv_none_tutor.setVisibility(View.GONE);
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
                        Toast.makeText(MyTutorListActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                requestTutorList(page);
            }
        });
    }
}
