package com.htmessage.cola_marketing.activity.homepageFunc.tiyan;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
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

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    Context context;
    List<JSONObject> list;

    TasksAdapter(Context context, List<JSONObject> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        JSONObject object = list.get(i);
        final String taskId = object.getString("task_id");
        String img = object.getString("only_good_pic");
        String name = object.getString("only_good_name");
        String desc = object.getString("task_describe");
        int state = object.getInteger("task_finish");

        viewHolder.tv_name.setText(name);
        viewHolder.tv_desc.setText(desc);
        RequestOptions options = new RequestOptions().placeholder(R.drawable.default_image2).error(R.drawable.default_image2);
        Glide.with(context).load(HTConstant.baseGoodsUrl+img).apply(options).into(viewHolder.iv_task);

        Drawable drawable = context.getDrawable(R.drawable.btn_radious_blue);
        switch (state) {
            case 0:
                DrawableCompat.setTint(drawable,context.getResources().getColor(R.color.LightBlue));
                viewHolder.btn_task.setText("开始任务");
                break;
            case 1:
                DrawableCompat.setTint(drawable,context.getResources().getColor(R.color.green2));
                viewHolder.btn_task.setText("继续任务");
                break;
            case 2:
                DrawableCompat.setTint(drawable,context.getResources().getColor(R.color.orange));
                viewHolder.btn_task.setText("领取奖励");
                break;
        }
        viewHolder.btn_task.setBackground(drawable);
        viewHolder.btn_task.setTag(state);

        viewHolder.btn_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick((int) v.getTag(),viewHolder.getAdapterPosition(),taskId);
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

    public void refreshItemState(int position) {
        JSONObject object = list.get(position);
        object.put("task_finish",1);
        list.set(position,object);
        notifyItemChanged(position);
    }

    public JSONObject getItemData(int pos) {
        return list.get(pos);
    }

    interface TaskButtonListener {
        void onClick(int state,int position,String taskId);
    }
    private TaskButtonListener listener;

    public void setListener(TaskButtonListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_task;
        TextView tv_name,tv_desc;
        Button btn_task;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_task = itemView.findViewById(R.id.iv_task);
            tv_desc = itemView.findViewById(R.id.tv_task_desc);
            tv_name = itemView.findViewById(R.id.tv_task);
            btn_task = itemView.findViewById(R.id.btn_task);
        }
    }
}
