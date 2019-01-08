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
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            textView= itemView.findViewById(R.id.tv_name);
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("userId",list.get(holder.getAdapterPosition()).getString("id"))
                        .putExtra("chatType", MessageUtils.CHAT_GROUP));
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
