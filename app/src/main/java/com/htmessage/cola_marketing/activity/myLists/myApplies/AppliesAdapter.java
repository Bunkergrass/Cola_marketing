package com.htmessage.cola_marketing.activity.myLists.myApplies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.homepageFunc.xiaoshou.XiaoShouActivity;
import com.htmessage.cola_marketing.activity.myLists.ListAdapterListener;

import java.util.List;

public class AppliesAdapter extends RecyclerView.Adapter<AppliesAdapter.ViewHolder> {
    private Context context;
    private List<JSONObject> list;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_apply,iv_state;
        TextView tv_name,tv_desc;
        Button btn_detail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_apply = itemView.findViewById(R.id.iv_apply);
            iv_state = itemView.findViewById(R.id.iv_apply_state);
            tv_desc = itemView.findViewById(R.id.tv_apply_desc);
            tv_name = itemView.findViewById(R.id.tv_apply_name);
            btn_detail = itemView.findViewById(R.id.btn_apply_detail);
        }
    }

    public AppliesAdapter(Context context, List<JSONObject> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_apply,viewGroup,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        final JSONObject data = list.get(i);
        String name = data.getString("only_good_name");
        String desc = data.getString("only_good_text");
        String imgUrl = data.getString("only_good_pic");
        int state = data.getInteger("apply_status");

        holder.tv_name.setText(name);
        holder.tv_desc.setText(desc);
        switch (state) {
            case 0:
                holder.iv_state.setImageResource(R.drawable.img_me_daishenhe);
                holder.btn_detail.setEnabled(false);
                break;
            case 1:
                holder.iv_state.setImageResource(R.drawable.img_me_pass);
                holder.btn_detail.setEnabled(true);
                break;
            case 2:
                holder.iv_state.setImageResource(R.drawable.img_me_fail);
                holder.btn_detail.setEnabled(false);
                break;
        }
        RequestOptions options = new RequestOptions().placeholder(R.drawable.default_image2).error(R.drawable.default_image2);
        Glide.with(context).load(HTConstant.baseGoodsUrl + imgUrl).apply(options).into(holder.iv_apply);

        holder.btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, XiaoShouActivity.class)
                        .putExtra("onlyId",data.getString("only_id")).putExtra("state",true)
                        .putExtra("isAdd",true));
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

    public String getApplyId(int pos) {
        return list.get(pos).getString("apply_id");
    }

}
