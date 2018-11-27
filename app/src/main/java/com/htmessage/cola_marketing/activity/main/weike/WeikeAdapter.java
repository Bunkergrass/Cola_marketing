package com.htmessage.cola_marketing.activity.main.weike;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.common.BigImageActivity;
import com.htmessage.cola_marketing.activity.moments.widget.SquareImageView;
import com.htmessage.cola_marketing.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class WeikeAdapter extends RecyclerView.Adapter<WeikeAdapter.ViewHolder> {
    private Context context;
    private List<JSONObject> list;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView title;
        LinearLayout images;
        ImageView avatar;
        TextView nick;
        TextView time;
        TextView comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.tv_weike_content);
            title = itemView.findViewById(R.id.tv_weike_title);
            images = itemView.findViewById(R.id.ll_weike_imgs);
            avatar = itemView.findViewById(R.id.iv_weike_avatar);
            nick = itemView.findViewById(R.id.tv_weike_nick);
            time = itemView.findViewById(R.id.tv_weike_time);
            comments = itemView.findViewById(R.id.tv_weike_comments);
        }
    }

    public WeikeAdapter(Context context, List<JSONObject> list) {
        this.context = context;
        this.list = list;
    }

    public void setListener(AdapterListener listener) {
        this.listener = listener;
    }

    private AdapterListener listener;
    interface AdapterListener {
        void onLongClick(int position,String uid,String pid);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weike,viewGroup,false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,WeikeCommentsActivity.class);
                intent.putExtra("post_id",list.get(holder.getAdapterPosition()).getString("post_id"));
                context.startActivity(intent);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int i = holder.getAdapterPosition();
                listener.onLongClick(i,list.get(i).getString("uid"),list.get(i).getString("post_id"));
                return true;
            }
        });
        return  holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        JSONObject item = list.get(pos);
        String post_id = item.getString("post_id");
        String uid = item.getString("uid");
        int comments = item.getInteger("comments");
        long add_time = item.getInteger("add_time");
        String title = item.getString("title");
        String text = item.getString("text");
        String usernick = item.getString("usernick");
        String avatar = item.getString("avatar");
        JSONArray images = item.getJSONArray("url");

        holder.title.setText(title);
        holder.content.setText(text);
        holder.nick.setText(usernick);
        holder.comments.setText(comments+"");
        holder.time.setText(CommonUtils.getDuration(context,add_time*1000,System.currentTimeMillis()));
        RequestOptions options = new RequestOptions().placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar);
        Glide.with(context).load(avatar).apply(options).into(holder.avatar);
        initImageView(JSONArray.parseArray(images.toJSONString(),JSONObject.class), holder.images);
    }

    private void initImageView(List<JSONObject> objects, LinearLayout linearLayout) {
        linearLayout.removeAllViews();
        final ArrayList<String> images = new ArrayList<>();
        for (int i = 0;i < 3;i++) {
            if (i >= objects.size()) break;
            images.add(objects.get(i).getString("url"));
        }
        for (int i =0;i<images.size();i++ ) {
            String imgUrl = images.get(i);
            SquareImageView imageView = new SquareImageView(context);
            imageView.setPadding(0,0,20,0);

            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_image2).centerCrop();
            Glide.with(context).load(HTConstant.baseImgUrl+imgUrl).apply(options).into(imageView);
            linearLayout.addView(imageView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1));

            final int index = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, BigImageActivity.class);
                    intent.putExtra("images", images.toArray(new String[images.size()]));
                    intent.putExtra("page", index);
                    context.startActivity(intent);
                }
            });
        }
    }

    public void addItems(List<JSONObject> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void refreshList(List<JSONObject> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void deleteItem(int i) {
        this.list.remove(i);
        notifyItemRemoved(i);
    }

}
