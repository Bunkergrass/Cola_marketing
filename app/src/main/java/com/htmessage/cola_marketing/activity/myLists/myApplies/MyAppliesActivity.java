package com.htmessage.cola_marketing.activity.myLists.myApplies;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
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
import com.htmessage.cola_marketing.activity.myLists.RecyclerItemClickListener;
import com.htmessage.cola_marketing.utils.CommonUtils;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.swipyrefresh.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MyAppliesActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private TabLayout tl_applies;
    private RecyclerView rv_applies;
    private SwipyRefreshLayout srl_applies;

    private AppliesAdapter adapter;
    private int page = 1;
    private List<JSONObject> allList = new ArrayList<>(),waitList,passList,failList; //显示的list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applies);
        setTitle("我的申请");

        waitList = new ArrayList<>();
        passList = new ArrayList<>();
        failList = new ArrayList<>();

        tl_applies = findViewById(R.id.tl_applies);
        rv_applies = findViewById(R.id.rv_applies);
        srl_applies = findViewById(R.id.srl_applies);
        setView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        requestAppliesList(page);
    }

    private void setView() {
        srl_applies.setOnRefreshListener(this);

        adapter = new AppliesAdapter(this,new ArrayList<JSONObject>());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rv_applies.setLayoutManager(manager);
        rv_applies.setAdapter(adapter);

        rv_applies.addOnItemTouchListener(new RecyclerItemClickListener(this, rv_applies, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onClick(int position, View itemView) {

            }

            @Override
            public void onLongClick(final int position, View itemView) {
                CommonUtils.showDeleteDialog(MyAppliesActivity.this, new CommonUtils.AlertDialogCallback() {
                    @Override
                    public void onPositive() {
                        deleteApply(position,adapter.getApplyId(position));
                    }

                    @Override
                    public void onNegative() {}
                });
            }
        }));

        tl_applies.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        adapter.refreshList(allList);
                        break;
                    case 1:
                        adapter.refreshList(waitList);
                        break;
                    case 2:
                        adapter.refreshList(passList);
                        break;
                    case 3:
                        adapter.refreshList(failList);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        tl_applies.addTab(tl_applies.newTab().setText("全部"));
        tl_applies.addTab(tl_applies.newTab().setText("待审核"),true);
        tl_applies.addTab(tl_applies.newTab().setText("审核通过"));
        tl_applies.addTab(tl_applies.newTab().setText("审核未通过"));
    }

    private void listClassify(List<JSONObject> list,boolean isRefresh) {
        if (isRefresh){
            allList.clear();
            waitList.clear();
            passList.clear();
            failList.clear();
        }
        allList.addAll(list);
        for (JSONObject object : list) {
            switch (object.getInteger("apply_status")) {
                case 0:
                    waitList.add(object);
                    break;
                case 1:
                    passList.add(object);
                    break;
                case 2:
                    failList.add(object);
                    break;
            }
        }
    }

    @Override
    public void onRefresh(int index) {
        page = 1;
        requestAppliesList(page);
    }

    @Override
    public void onLoad(int index) {
        page ++;
        requestAppliesList(page);
    }

    private void requestAppliesList(final int page) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("page",page+""));
        new OkHttpUtils(MyAppliesActivity.this).post(params, HTConstant.URL_MY_APPLY_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                srl_applies.setRefreshing(false);
                Log.d("requestAppliesList",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        JSONArray data = jsonObject.getJSONArray("data");
                        List<JSONObject> list = JSONArray.parseArray(data.toJSONString(),JSONObject.class);
                        if (page == 1) {
                            listClassify(list,true);
                        } else {
                            listClassify(list,false);
                        }
                        tl_applies.getTabAt(0).select();
                        break;
                    default:
                        Toast.makeText(MyAppliesActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                srl_applies.setRefreshing(false);
                Toast.makeText(MyAppliesActivity.this,R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteApply(final int pos, String apply_id) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("apply_id", apply_id));
        new OkHttpUtils(this).post(params, HTConstant.URL_DELETE_APPLY, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("deleteApply",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        page = 1;
                        requestAppliesList(page);
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        Toast.makeText(MyAppliesActivity.this,R.string.delete_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(MyAppliesActivity.this,R.string.delete_failed,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
