package com.htmessage.cola_marketing.activity.main.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.homepageFunc.xiangqing.XiangqingActivity;
import com.htmessage.cola_marketing.activity.homepageFunc.xiaoshou.XiaoShouActivity;
import com.htmessage.cola_marketing.widget.RCLayout.RCRelativeLayout;

public class TradeItemView extends RCRelativeLayout {
    ImageView iv_trade;
    TextView tv_trade;
    TextView tv_price;
    ImageView iv_xiangqing,iv_xiaoshou;

    public String trade_id,trade_url,onlyId;

    public TradeItemView(Context context) {
        super(context);
        init(context);
    }

    public TradeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TradeItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_trade_item,this,true);
        iv_trade = findViewById(R.id.iv_trade);
        tv_trade = findViewById(R.id.tv_trade);
        tv_price = findViewById(R.id.tv_trade_price);
        iv_xiangqing = findViewById(R.id.iv_hot_xiangqing);
        iv_xiaoshou = findViewById(R.id.iv_hot_xiaoshou);
    }

    public void initView(final JSONObject data, final Context context) {
        String name = data.getString("only_good_name");
        int price = data.getFloat("only_money").intValue();
        String imgUrl = data.getString("only_good_pic");
        trade_id = data.getString("goods_id");
        trade_url = data.getString("only_url");
        onlyId = data.getString("only_id");

        tv_trade.setText(name);
        tv_price.setText(price + "");

        RequestOptions options = new RequestOptions().error(R.drawable.default_image2).placeholder(R.drawable.default_image2);
        Glide.with(context).load(HTConstant.baseGoodsUrl + imgUrl).apply(options).into(iv_trade);

        iv_xiaoshou.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, XiaoShouActivity.class).putExtra("onlyId",onlyId));
            }
        });
        iv_xiangqing.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, XiangqingActivity.class).putExtra("onlyId",onlyId));
            }
        });
    }


}
