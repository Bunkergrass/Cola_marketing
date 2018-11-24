package com.htmessage.cola_marketing.activity.homepageFunc.tiyan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.R;

import java.util.List;

public class TaskScheduleAdapter extends RecyclerView.Adapter<TaskScheduleAdapter.ViewHolder> {
    private Context context;
    private List<JSONObject> list;

    public TaskScheduleAdapter(Context context, List<JSONObject> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_schedule,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        JSONObject object = list.get(i);
        int state = object.getInteger("finish_con_status");
        String text = object.getString("con_text");
        String sum = object.getString("con_nums");
        String cur = object.getString("finish_con_go_num");

        switch (state) {
            case 0:
                viewHolder.imageView.setImageResource(R.drawable.icon_tiyan_undone);
                viewHolder.textView.setTextColor(context.getResources().getColor(R.color.text_color_light));
                break;
            case 1:
                viewHolder.imageView.setImageResource(R.drawable.icon_tiyan_done);
                viewHolder.textView.setTextColor(context.getResources().getColor(R.color.LightBlue));
                break;
        }
        viewHolder.textView.setText(text + "\n" + cur + "/" + sum);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public String getCurrentTask() {
        for (JSONObject object : list) {
            if (object.getInteger("finish_con_status") == 0) {
                return object.getString("only_id");
            }
        }
        return "";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_task_schedule);
            textView = itemView.findViewById(R.id.tv_task_schedule);
        }
    }
}
