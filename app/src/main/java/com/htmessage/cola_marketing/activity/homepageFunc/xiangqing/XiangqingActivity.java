package com.htmessage.cola_marketing.activity.homepageFunc.xiangqing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;

import java.util.ArrayList;
import java.util.List;

public class XiangqingActivity extends BaseActivity {
    Button btn_apply;
    WebView wb_xiangqing;
    TextView tv_episode,tv_story,tv_video;

    private String onlyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiangqing);
        setTitle("商品详情");

        onlyId = getIntent().getStringExtra("onlyId");
        requestOnlyDetail();
        initView();
        viewListener();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        tv_episode = findViewById(R.id.tv_trade_episode);
        tv_story = findViewById(R.id.tv_trade_story);
        tv_video = findViewById(R.id.tv_trade_video);
        wb_xiangqing = findViewById(R.id.wb_xiangqing);
        btn_apply = findViewById(R.id.btn_trade_apply);

        WebSettings webSettings = wb_xiangqing.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
    }

    private void viewListener() {
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(XiangqingActivity.this,ConfirmApplyActivity.class).putExtra("onlyId",onlyId));
            }
        });
    }

    private void showView(JSONObject jsonObject) {
        String description = jsonObject.getString("description");
        String episode = jsonObject.getString("only_episode");
        String story = jsonObject.getString("only_story");
        String video = jsonObject.getString("only_story");

        wb_xiangqing.loadDataWithBaseURL(HTConstant.baseGoodsUrl,description,"text/html", "utf-8",null);
        tv_video.setText(video);
        tv_story.setText(story);
        tv_episode.setText(episode);
    }

    private void requestOnlyDetail() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("only_id",onlyId));
        new OkHttpUtils(this).post(params, HTConstant.URL_TRADE_ONLY_DETAIL, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("requestOnlyDetail",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        showView(jsonObject.getJSONObject("data"));
                        break;
                    default:
                        Toast.makeText(XiangqingActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(XiangqingActivity.this,R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        Context context;
        List<JSONObject> images;

        public ImageAdapter(Context context, List<JSONObject> images) {
            this.context = context;
            this.images = images;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = (ImageView) itemView;
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            ImageView imageView = new ImageView(context);
            return new ViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return images.size();
        }

    }
}
