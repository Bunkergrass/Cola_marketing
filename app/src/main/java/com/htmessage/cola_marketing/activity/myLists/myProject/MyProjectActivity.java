package com.htmessage.cola_marketing.activity.myLists.myProject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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


public class MyProjectActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {
//    ListView lv_projects;
    RecyclerView rv_projects;
    SwipyRefreshLayout srl_projects;
    ProjectsAdapter adapter;

    List<JSONObject> list = new ArrayList<>();
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_project);
        setTitle("项目列表");

        srl_projects = findViewById(R.id.srl_projects);
        srl_projects.setOnRefreshListener(this);
//        lv_projects = findViewById(R.id.lv_projects);
//        lv_projects.setAdapter(adapter);
//        lv_projects.addFooterView(initFootView());
//        lv_projects.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                startActivity(new Intent(MyProjectActivity.this,AddProjectActivity.class)
//                        .putExtra("project",adapter.getItem(position).toString()));
//                return true;
//            }
//        });
        rv_projects = findViewById(R.id.rv_projects);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new ProjectsAdapter(this,new ArrayList<JSONObject>());
        adapter.addFooter(initFootView());
        rv_projects.setLayoutManager(manager);
        rv_projects.setAdapter(adapter);
    }

    private View initFootView() {
        View view = LayoutInflater.from(this).inflate(R.layout.button_radius_blue,null);
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Button button = view.findViewById(R.id.btn_radius_blue); //new Button(this);
        button.setText("添加项目");
//        button.setTextColor(ContextCompat.getColor(this,R.color.white));
//        button.setTextSize(16);
//        button.setBackgroundResource(R.drawable.btn_radious_blue);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyProjectActivity.this,AddProjectActivity.class));
            }
        });
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        getProjects(page,true);
    }

    private void getProjects(int page, final boolean isRefresh) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("page",page + ""));
        new OkHttpUtils(MyProjectActivity.this).post(params, HTConstant.URL_MY_PROJECT_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                srl_projects.setRefreshing(false);
                Log.d("getProjects",jsonObject.toString());
                int code = jsonObject.getInteger("code");
                switch (code) {
                    case 1:
                        JSONArray data = jsonObject.getJSONArray("data");
                        list = JSONArray.parseArray(data.toJSONString(), JSONObject.class);
                        if (isRefresh)
                            adapter.refreshList(list);
                        else
                            adapter.addList(list);
                        break;
                    default:
                        Toast.makeText(MyProjectActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                srl_projects.setRefreshing(false);
                Toast.makeText(MyProjectActivity.this,R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh(int index) {
        page = 1;
        getProjects(page,true);
    }

    @Override
    public void onLoad(int index) {
        page++;
        getProjects(page,false);
    }
}
