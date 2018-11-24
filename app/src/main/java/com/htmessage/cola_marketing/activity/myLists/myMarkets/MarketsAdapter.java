package com.htmessage.cola_marketing.activity.myLists.myMarkets;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.homepageFunc.xiaoshou.XiaoShouActivity;

import java.util.List;

public class MarketsAdapter extends RecyclerView.Adapter<MarketsAdapter.ViewHolder> {
    private Context context;
    private List<JSONObject> list;

    public MarketsAdapter(Context context, List<JSONObject> list) {
        this.context = context;
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_market;
        TextView tv_name,tv_desc,tv_price,tv_sales,tv_detail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_market = itemView.findViewById(R.id.iv_market);
            tv_desc = itemView.findViewById(R.id.tv_market_desc);
            tv_detail = itemView.findViewById(R.id.tv_market_detail);
            tv_name = itemView.findViewById(R.id.tv_market_name);
            tv_price = itemView.findViewById(R.id.tv_market_price);
            tv_sales = itemView.findViewById(R.id.tv_market_sales);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_market,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        JSONObject data = list.get(i);
        final String only_id = data.getString("only_id");
        final String onlyGoodId = data.getString("onlygood_id");
        String name = data.getString("onlygood_name");
        String desc = data.getString("onlygood_text");
        String imgUrl = data.getString("onlygood_pic");
        int sales = data.getFloat("onlygood_sales").intValue();
        String price = data.getString("promotion_price");

        holder.tv_sales.setText("销量 " + sales);
        holder.tv_name.setText(name);
        holder.tv_desc.setText(desc);

        SpannableString string = new SpannableString("￥" + price);
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.75f);
        string.setSpan(sizeSpan,0,1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.tv_price.setText(string);

        RequestOptions options = new RequestOptions().placeholder(R.drawable.default_image).error(R.drawable.default_image);
        Glide.with(context).load(HTConstant.baseGoodsUrl + imgUrl).apply(options).into(holder.iv_market);

        holder.tv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, XiaoShouActivity.class)
                        .putExtra("onlyId",only_id)
                        .putExtra("state",true)
                        .putExtra("onlyGoodId",onlyGoodId));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addList(List<JSONObject> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void refreshList(List<JSONObject> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

}
