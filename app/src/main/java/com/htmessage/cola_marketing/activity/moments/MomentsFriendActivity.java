package com.htmessage.cola_marketing.activity.moments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.utils.ACache;
import com.htmessage.cola_marketing.utils.DateUtils;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.swipyrefresh.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MomentsFriendActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {

    private SwipyRefreshLayout pull_refresh_list;
    private List<JSONObject> articles = new ArrayList<JSONObject>();
    private MomentsFriendAdapter adapter;
    private ListView actualListView;
    private String isFriend = "0";//默认不经过是否是好友判断
    private String userId;
    private String avatar;
    private String userNick;
    private List<String> backgroundMoment = new ArrayList<>();
    private List<String> serviceTimes = new ArrayList<>();
    private String cacheKeyTime;
    private String cacheKey;
    private String cacheKeyBg;


    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);
        setContentView(R.layout.activity_moments_friend);
        getIntentData();
        if (userId == null) {
            finish();
            return;
        }

        if (HTApp.getInstance().getUsername().equals(userId)) {
            userNick = getString(R.string.my_image_manager);
            avatar = HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_AVATAR);
            showRightView(R.drawable.icon_moments_notice, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MomentsFriendActivity.this, MomentsNoticeActivity.class));
                }
            });
        }
        setTitle(userNick);
        initView();
    }

    private void getIntentData() {
        userId = this.getIntent().getStringExtra("userId");
        userNick = getIntent().getStringExtra("userNick");
        avatar = getIntent().getStringExtra("avatar");
        cacheKey = "mymoments" + userId;
        cacheKeyBg = userId + "mymomentbg";
        cacheKeyTime = userId + "cacheKeyTime";
        getMoments();
        getBackgroundMoment();
        getCatcheTime();
    }

    private void initView() {
        pull_refresh_list = findViewById(R.id.pull_refresh_list);
        actualListView = findViewById(R.id.refresh_list);
        pull_refresh_list.setOnRefreshListener(this);
        adapter = new MomentsFriendAdapter(MomentsFriendActivity.this, articles, avatar, backgroundMoment, serviceTimes);
        actualListView.setAdapter(adapter);
        getData(userId, 1);
    }

    private void getData(String friendID, final int pageIndex) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("currentPage", pageIndex + ""));
        params.add(new Param("pageSize", 20 + ""));
        params.add(new Param("userId", friendID));
//        params.add(new Param("isFriend", isFriend));
        new OkHttpUtils(MomentsFriendActivity.this).post(params, HTConstant.URL_SOCIAL_FRIEND, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getInteger("code");
                switch (code) {
                    case 1:
                        backgroundMoment.clear();
                        String background = jsonObject.getString("backgrounds");
                        String time = jsonObject.getString("time");
                        JSONArray data = jsonObject.getJSONArray("data");
                        if (data != null) {
                            List<JSONObject> list = JSONArray.parseArray(data.toJSONString(), JSONObject.class);
                            if (pageIndex == 1) {
                                articles.clear();
                                ACache.get(MomentsFriendActivity.this).put(cacheKey, data);
                                ACache.get(MomentsFriendActivity.this).put(cacheKeyBg, background);
                                ACache.get(MomentsFriendActivity.this).put(cacheKeyTime, time);
                            }
                            articles.addAll(list);
                            backgroundMoment.add(background);
                            serviceTimes.add(time);
                        }
                        break;
                    case -1:
                        if (pageIndex == 1) {
                            ACache.get(MomentsFriendActivity.this).put(cacheKey, "");
                            articles.clear();
                            serviceTimes.clear();
                        }
                        Toast.makeText(MomentsFriendActivity.this, R.string.has_nothing,Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        if (pageIndex == 1) {
                            ACache.get(MomentsFriendActivity.this).put(cacheKey, "");
                            ACache.get(MomentsFriendActivity.this).put(cacheKeyBg, "");
                            ACache.get(MomentsFriendActivity.this).put(cacheKeyTime, "");
                            articles.clear();
                            serviceTimes.clear();
                            backgroundMoment.clear();
                        }
                        Toast.makeText(MomentsFriendActivity.this, R.string.has_nothing,Toast.LENGTH_SHORT).show();
                        break;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(MomentsFriendActivity.this, getString(R.string.request_failed_msg) + errorMsg,Toast.LENGTH_SHORT).show();
                if (pageIndex == 1) {
                    ACache.get(MomentsFriendActivity.this).put(cacheKey, "");
                    ACache.get(MomentsFriendActivity.this).put(cacheKeyBg, "");
                    ACache.get(MomentsFriendActivity.this).put(cacheKeyTime, "");
                    articles.clear();
                    serviceTimes.clear();
                    backgroundMoment.clear();
                }
                adapter.notifyDataSetChanged();
            }
        });
        pull_refresh_list.setRefreshing(false);
    }

    private List<String> getBackgroundMoment() {
        backgroundMoment.clear();
        String bacground = ACache.get(MomentsFriendActivity.this).getAsString(cacheKeyBg);
        if (!TextUtils.isEmpty(bacground)) {
            backgroundMoment.add(bacground);
        }
        return backgroundMoment;
    }

    private List<JSONObject> getMoments() {
        JSONArray jsonArray = ACache.get(MomentsFriendActivity.this).getAsJSONArray(cacheKey);
        if (jsonArray != null) {
            List<JSONObject> list = JSONArray.parseArray(jsonArray.toJSONString(), JSONObject.class);
            articles.addAll(list);
        }
        return articles;
    }

    private List<String> getCatcheTime() {
        serviceTimes.clear();
        String time = ACache.get(MomentsFriendActivity.this).getAsString(cacheKeyTime);
        if (!TextUtils.isEmpty(time)) {
            serviceTimes.add(time);
        } else {
            serviceTimes.add(DateUtils.getStringTime(System.currentTimeMillis()));
        }
        return serviceTimes;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  getData(pageIndex, userId);
    }


    @Override
    public void onRefresh(int index) {
        index = 1;
        getData(userId, index);
    }

    @Override
    public void onLoad(int index) {
        index++;
        getData(userId, index);
    }
}
