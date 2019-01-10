package com.htmessage.cola_marketing.activity.homepageFunc.squad;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.chat.ChatActivity;
import com.htmessage.cola_marketing.activity.chat.group.GroupListActivity;
import com.htmessage.sdk.utils.MessageUtils;

import java.util.List;

public class SquadListAdapter extends RecyclerView.Adapter<SquadListAdapter.ViewHolder> {
    private Context context;
    private List<JSONObject> list;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }

    public SquadListAdapter(Context context, List<JSONObject> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_groups,viewGroup,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        JSONObject object = list.get(i);
        final String gid = object.getString("gid");
        String imgurl = object.getString("imgurl");
        String name = object.getString("name");

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("userId",gid)
                        .putExtra("chatType", MessageUtils.CHAT_GROUP));
            }
        });
        viewHolder.tv_name.setText(name);
        RequestOptions options = new RequestOptions().placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar);
        Glide.with(context).load(imgurl).apply(options).into(viewHolder.iv_avatar);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
