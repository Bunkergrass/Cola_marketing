package com.htmessage.cola_marketing.activity.homepageFunc.tutor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.R;

import java.util.List;

public class TutorTagAdapter extends RecyclerView.Adapter<TutorTagAdapter.ViewHolder> {
    private Context context;
    private List<JSONObject> list;
    private OnItemClickListener listener;

    public TutorTagAdapter(Context context, List<JSONObject> list) {
        this.context = context;
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_tutor_tag_item);
        }
    }

    interface OnItemClickListener {
        void OnItemClick(View view,int position);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tutor_tag,viewGroup,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null)
                    listener.OnItemClick(v,holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        JSONObject object = list.get(i);
        viewHolder.textView.setText(object.getString("label_name"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
