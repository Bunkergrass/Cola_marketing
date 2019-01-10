package com.htmessage.cola_marketing.activity.homepageFunc.tutor;

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
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.swipyrefresh.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class TutorListActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private SwipyRefreshLayout srl_tutors;

    private TutorListAdapter adapter;
    private List<JSONObject> list = new ArrayList<>();

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_list);
        setTitle("导师列表");

        srl_tutors = findViewById(R.id.srl_tutor_list);
        RecyclerView rv_tutors = findViewById(R.id.rv_tutor_list);

        srl_tutors.setOnRefreshListener(this);
        adapter = new TutorListAdapter(this,list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rv_tutors.setLayoutManager(manager);
        rv_tutors.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        getTutorList(page);
    }

    @Override
    public void onRefresh(int index) {
        page = 1;
        getTutorList(page);
    }

    @Override
    public void onLoad(int index) {
        page++;
        getTutorList(page);
    }

    private void getTutorList(final int page) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",HTApp.getInstance().getUsername()));
        params.add(new Param("page",page+""));
        new OkHttpUtils(this).post(params, HTConstant.URL_TUTOR_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                srl_tutors.setRefreshing(false);
                Log.d("getTutorList",jsonObject.toString());
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
                        Toast.makeText(TutorListActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                srl_tutors.setRefreshing(false);
                Toast.makeText(TutorListActivity.this,R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
