package com.htmessage.cola_marketing.activity.homepageFunc.tutor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.chat.ChatActivity;
import com.htmessage.sdk.utils.MessageUtils;

import java.util.List;

public class TutorListAdapter extends RecyclerView.Adapter<TutorListAdapter.ViewHolder> {
    private Context context;
    private List<JSONObject> list;
    private RequestOptions options;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_avatar;
        TextView tv_name,tv_num,tv_tag,tv_desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_avatar = itemView.findViewById(R.id.iv_tutor_avatar);
            tv_desc = itemView.findViewById(R.id.tv_tutor_desc);
            tv_name = itemView.findViewById(R.id.tv_tutor_name);
            tv_num = itemView.findViewById(R.id.tv_tutor_num);
            tv_tag = itemView.findViewById(R.id.tv_tutor_tag);
        }
    }

    public TutorListAdapter(Context context, List<JSONObject> list) {
        this.context = context;
        this.list = list;
        options = new RequestOptions().optionalCircleCrop().placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_all_tutor,viewGroup,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final JSONObject object = list.get(i);
        final String tutor_id = object.getString("tutor_id");
        final int is_group_members = object.getInteger("is_group_members");
        final String gid = object.getString("gid");
        String tutor_name = object.getString("tutor_name");
        String avatar = object.getString("avatar");
        String tutor_mysign = object.getString("tutor_mysign");
        String group_nums = object.getString("group_nums");
        JSONArray lables = object.getJSONArray("label");

        if (!lables.isEmpty()) {
            String label = lables.getJSONObject(0).getString("label_name");
            viewHolder.tv_tag.setText(label);
        } else {
            viewHolder.tv_tag.setVisibility(View.GONE);
        }

        viewHolder.tv_name.setText(tutor_name + " 老师");
        viewHolder.tv_num.setText(group_nums + "人");
        viewHolder.tv_desc.setText(tutor_mysign);
        RequestOptions options = new RequestOptions().error(R.drawable.default_avatar).placeholder(R.drawable.default_avatar).optionalCircleCrop();
        Glide.with(context).load(avatar).apply(options).into(viewHolder.iv_avatar);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_group_members == 1){
                    context.startActivity(new Intent(context,TutorDetailActivity.class)
                            .putExtra("tutor_json",object.toJSONString()));
                } else {
                    context.startActivity(new Intent(context,ChatActivity.class)
                            .putExtra("userId",gid)
                            .putExtra("chatType", MessageUtils.CHAT_GROUP));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
