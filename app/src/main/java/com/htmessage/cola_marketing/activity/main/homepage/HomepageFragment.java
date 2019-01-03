package com.htmessage.cola_marketing.activity.main.homepage;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.WebViewActivity;
import com.htmessage.cola_marketing.activity.chat.file.util.MD5;
import com.htmessage.cola_marketing.activity.chat.group.GroupListActivity;
import com.htmessage.cola_marketing.activity.homepageFunc.AptitudeExam.ExamActivity;
import com.htmessage.cola_marketing.activity.homepageFunc.baokuan.BaokuanActivity;
import com.htmessage.cola_marketing.activity.homepageFunc.danpin.DanpinActivity;
import com.htmessage.cola_marketing.activity.homepageFunc.squad.SquadListActivity;
import com.htmessage.cola_marketing.activity.homepageFunc.tiyan.TiyanActivity;
import com.htmessage.cola_marketing.activity.homepageFunc.tutor.TutorListActivity;
import com.htmessage.cola_marketing.activity.homepageFunc.tutor.TutorListAdapter;
import com.htmessage.cola_marketing.activity.main.weike.WeikeActivity;
import com.htmessage.cola_marketing.activity.main.widget.HomepageFuncView;
import com.htmessage.cola_marketing.activity.main.widget.TradeItemView;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HomepageFragment extends Fragment implements View.OnClickListener {
    private RecyclerView rv_home_incomeranking;
    private HomepageFuncView home_danpin,home_baokuan,home_cuxiao,home_tiyan,
            home_ziyuan,home_zhanlue,home_daoshi,home_tutor;
    private HomepageFuncView home_data,home_func,home_test;
    private TradeItemView tiv_home0,tiv_home1;
    private TextView tv_nothing;

    private Timer timer;
    private static int listShow = 3;
    private List<JSONObject> rankList;
    private RankAdapter rankAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_homepage, container, false);
        initView(root);
        getData();
        setView();
        return root;
    }

    private void getData() {
        rankList = new ArrayList<>();
        rankList.add(new JSONObject());
        rankList.add(new JSONObject());
        rankList.add(new JSONObject());
        rankList.add(new JSONObject());
        rankList.add(new JSONObject());
        rankAdapter = new RankAdapter(getActivity(),rankList);
        requestOnlyList();
    }

    private void initView(View root) {
        rv_home_incomeranking = root.findViewById(R.id.rv_home_incomeranking);
        tiv_home0 = root.findViewById(R.id.tiv_home0);
        tiv_home1 = root.findViewById(R.id.tiv_home1);
        tv_nothing = root.findViewById(R.id.tv_home_nothing);
        home_baokuan = root.findViewById(R.id.home_baokuan);
        home_cuxiao = root.findViewById(R.id.home_cuxiao);
        home_danpin = root.findViewById(R.id.home_danpin);
        home_daoshi = root.findViewById(R.id.home_daoshi);
        home_tiyan = root.findViewById(R.id.home_tiyan);
        home_tutor = root.findViewById(R.id.home_tutor);
        home_zhanlue = root.findViewById(R.id.home_zhanlue);
        home_ziyuan = root.findViewById(R.id.home_ziyuan);
        home_data = root.findViewById(R.id.hfv_data);
        home_test = root.findViewById(R.id.hfv_test);
        home_func = root.findViewById(R.id.hfv_func);
    }

    private void setView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        rv_home_incomeranking.setLayoutManager(layoutManager);
        rv_home_incomeranking.setAdapter(rankAdapter);

        home_ziyuan.setOnClickListener(this);
        home_zhanlue.setOnClickListener(this);
        home_tutor.setOnClickListener(this);
        home_tiyan.setOnClickListener(this);
        home_daoshi.setOnClickListener(this);
        home_danpin.setOnClickListener(this);
        home_cuxiao.setOnClickListener(this);
        home_baokuan.setOnClickListener(this);
        home_func.setOnClickListener(this);
        home_test.setOnClickListener(this);
        home_data.setOnClickListener(this);

        timer = new Timer();
        timer.schedule(new ScrollTask(),0,1500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_baokuan:
                startActivity(new Intent(getActivity(), BaokuanActivity.class));
                break;
            case R.id.home_cuxiao:
                openShop("http://cx.kakusi.cn/index.php?s=/wap","促销商城");
                break;
            case R.id.home_danpin:
                startActivity(new Intent(getActivity(), DanpinActivity.class));
                break;
            case R.id.home_daoshi:
                startActivity(new Intent(getActivity(), TiyanActivity.class));
                break;
            case R.id.home_tiyan:
                openShop("http://klshop.kakusi.cn/index.php?s=/wap","体验商城");
                break;
            case R.id.home_tutor:
                startActivity(new Intent(getActivity(), TutorListActivity.class));
                break;
            case R.id.home_zhanlue:
                startActivity(new Intent(getActivity(), SquadListActivity.class));
                break;
            case R.id.home_ziyuan:
                startActivity(new Intent(getActivity(), WeikeActivity.class));
                break;
            case R.id.hfv_data:
                break;
            case R.id.hfv_func:
                break;
            case R.id.hfv_test:
                startActivity(new Intent(getActivity(), ExamActivity.class));
                break;
        }
    }

    class ScrollTask extends TimerTask {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                rankAdapter.refreshTime();
                rv_home_incomeranking.scrollToPosition(listShow);
            }
        };

        @Override
        public void run() {
            if (listShow >= rankAdapter.getItemCount()) {
                listShow = 0;
            }
            handler.sendMessage(handler.obtainMessage());
            listShow++;
        }
    }

    @Override
    public void onPause() {
        timer.cancel();
        super.onPause();
    }

    private void openShop(String baseIP,String title) {
        String sign = MD5.getStringMD5("3F7FE95D1C0550BB5657709A296408964CD4A7E8" //token
                + "0A82F47CAFC58C47BEAC1B2B840826F01845BC39" //app_secret
                + "klyx-app" //app_id
                + "70a37f7e86b12404b0a5566d2bedd629c1286eec"); //encryption_mode

        startActivity(new Intent(getActivity(), WebViewActivity.class)
                .putExtra("title",title)
                .putExtra("url",baseIP+"&userID="
                        + HTApp.getInstance().getUsername()
                        + "&usernick=" + HTApp.getInstance().getUserNick()
                        + "&tel=" + HTApp.getInstance().getUserTel() + "&sign=" +sign));
    }

    private void requestOnlyList() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("page","1"));
        params.add(new Param("type","6"));//6：首页推广
        new OkHttpUtils(getActivity()).post(params, HTConstant.URL_TRADE_ONLY_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("requestOnlyList",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        tv_nothing.setVisibility(View.GONE);
                        JSONArray datas = jsonObject.getJSONArray("data");
                        tiv_home0.setVisibility(View.VISIBLE);
                        tiv_home1.setVisibility(View.VISIBLE);
                        tiv_home0.initView(datas.getJSONObject(0),getContext());
                        tiv_home1.initView(datas.getJSONObject(1),getContext());
                        break;
                    default:

                }
            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });
    }

}
