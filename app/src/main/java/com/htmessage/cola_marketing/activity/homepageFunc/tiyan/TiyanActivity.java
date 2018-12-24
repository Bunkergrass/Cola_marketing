package com.htmessage.cola_marketing.activity.homepageFunc.tiyan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.homepageFunc.xiaoshou.XiaoShouActivity;
import com.htmessage.cola_marketing.utils.DensityUtil;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.utils.ShareUtils;
import com.htmessage.cola_marketing.widget.swipyrefresh.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class TiyanActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    TabLayout tl_tiyan;
    RecyclerView rv_tiyan;
    SwipyRefreshLayout srl_tiyan;

    private int page = 1;
    private TasksAdapter adapter;
    List<JSONObject> allList,curList,doneList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiyan);
        setTitle("营销学堂");

        tl_tiyan = findViewById(R.id.tl_tiyan);
        rv_tiyan = findViewById(R.id.rv_tiyan);
        srl_tiyan = findViewById(R.id.srl_tiyan);

        allList = new ArrayList<>();
        curList = new ArrayList<>();
        doneList = new ArrayList<>();

        setView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        requestTaskList();
    }

    private void setView() {
        srl_tiyan.setOnRefreshListener(this);

        adapter = new TasksAdapter(this,new ArrayList<JSONObject>());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rv_tiyan.setLayoutManager(manager);
        rv_tiyan.setAdapter(adapter);

        tl_tiyan.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        adapter.refreshList(allList);
                        break;
                    case 1:
                        adapter.refreshList(curList);
                        break;
                    case 2:
                        adapter.refreshList(doneList);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        tl_tiyan.addTab(tl_tiyan.newTab().setText("全部"));
        tl_tiyan.addTab(tl_tiyan.newTab().setText("正在做的"),true);
        tl_tiyan.addTab(tl_tiyan.newTab().setText("已完成的"));

        adapter.setListener(new TasksAdapter.TaskButtonListener() {
            @Override
            public void onClick(int state, int position, String taskId) {
                switch (state) {
                    case 0:
                        getTask(position,taskId);
                        break;
                    case 1:
                        makeTask(taskId);
                        break;
                    case 2:
                        JSONObject data = adapter.getItemData(position);
                        startActivity(new Intent(TiyanActivity.this,AwardActivity.class)
                                .putExtra("taskId",taskId)
                                .putExtra("only_good_name",data.getString("only_good_name"))
                                .putExtra("only_good_text",data.getString("only_good_text"))
                                .putExtra("only_good_pic",data.getString("only_good_pic")));
                        break;
                }
            }
        });
    }

    private void listClassify(List<JSONObject> list,boolean isRefresh) {
        if (isRefresh) {
            allList.clear();
            curList.clear();
            doneList.clear();
        }
        allList.addAll(list);
        for (JSONObject object : list) {
            switch (object.getInteger("task_finish")) {
                case 0:
                    break;
                case 1:
                    curList.add(object);
                    break;
                case 2:
                    doneList.add(object);
                    break;
            }
        }
    }

    @Override
    public void onRefresh(int index) {
        page = 1;
        requestTaskList();
    }

    @Override
    public void onLoad(int index) {
        page ++ ;
        requestTaskList();
    }

    private void showTaskDialog(final JSONArray data) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_task_schedule,null);
        RecyclerView rv_schedule = view.findViewById(R.id.rv_task_schedule);
        Button btn_start = view.findViewById(R.id.btn_make_task);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        final TaskScheduleAdapter adapter = new TaskScheduleAdapter(this
                ,JSONArray.parseArray(data.toJSONString(),JSONObject.class));
        rv_schedule.setLayoutManager(manager);
        rv_schedule.setAdapter(adapter);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                requestEncryptUrl(adapter.getCurrentTask());
                startActivity(new Intent(TiyanActivity.this, XiaoShouActivity.class)
                        .putExtra("onlyId",adapter.getCurrentTask()));
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = DensityUtil.dp2px(this,320);
        window.setAttributes(lp);
    }

    private void requestTaskList() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("type","0"));
        params.add(new Param("page",page + ""));
        new OkHttpUtils(this).post(params, HTConstant.URL_TASK_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                srl_tiyan.setRefreshing(false);
                Log.d("requestTaskList",jsonObject.toString());
                int code = jsonObject.getInteger("code");
                switch (code) {
                    case 1:
                        JSONArray array = jsonObject.getJSONArray("data");
                        List<JSONObject> list = JSONArray.parseArray(array.toJSONString(),JSONObject.class);
                        if (page == 1) {
                            listClassify(list,true);
                        } else {
                            listClassify(list,false);
                        }
                        tl_tiyan.getTabAt(0).select();
                        break;
                    default:
                        Toast.makeText(TiyanActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                srl_tiyan.setRefreshing(false);
                Toast.makeText(TiyanActivity.this,R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTask(final int position, String tid) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("task_id",tid));
        new OkHttpUtils(this).post(params, HTConstant.URL_GET_TASK, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("getTask",jsonObject.toString());
                int code = jsonObject.getInteger("code");
                switch (code) {
                    case 1:
                        adapter.refreshItemState(position);
                        break;
                    default:
                        Toast.makeText(TiyanActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(TiyanActivity.this,R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makeTask(String tid) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("task_id",tid));
        new OkHttpUtils(this).post(params, HTConstant.URL_MAKE_TASK, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("makeTask",jsonObject.toString());
                int code = jsonObject.getInteger("code");
                switch (code) {
                    case 1:
                        JSONArray data = jsonObject.getJSONArray("data");
                        showTaskDialog(data);
                        break;
                    default:
                        Toast.makeText(TiyanActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(TiyanActivity.this,R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestEncryptUrl(String onlyId) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("type","0"));
        params.add(new Param("only_id",onlyId));
        new OkHttpUtils(this).post(params, HTConstant.URL_ENCRYPT_URL, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("requestEncryptUrl",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        ShareUtils.shareText(TiyanActivity.this
                                ,HTConstant.baseGoodsUrl+jsonObject.getString("data")
                                ,"分享商品动态链接");
                        break;
                    default:
                        Toast.makeText(TiyanActivity.this,"获取动态链接失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(TiyanActivity.this,"获取动态链接失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
