package com.htmessage.cola_marketing.activity.myLists.myProject;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.myLists.ListAdapterListener;

import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<JSONObject> list;
    private Context context;

    private View footerView;
    private static final int TYPE_FOOTER = -1;

    ProjectsAdapter(Context context, List<JSONObject> list) {
        this.list = list;
        this.context = context;
    }

    private ListAdapterListener listener;
    public void setListener(ListAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView != null && position > list.size()-1)
            return TYPE_FOOTER;
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new FooterHolder(footerView);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_hint_set,viewGroup,false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClick(holder.getAdapterPosition(),v);
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_FOOTER)
            return;
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.tv_id.setText("项目 " + (position+1));
        holder.tv_name.setText(list.get(position).getString("explain_name"));
    }

    @Override
    public int getItemCount() {
        int i = list.size();
        if (footerView != null)
            i++;
        return i;
    }

    public void addFooter(View view) {
        this.footerView = view;
    }

    public void refreshList(List<JSONObject> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(List<JSONObject> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText tv_name;
        TextView tv_id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_id = itemView.findViewById(R.id.tv_key_text);
            tv_name = itemView.findViewById(R.id.et_hint_text);
        }
    }

    static class FooterHolder extends RecyclerView.ViewHolder {
        public FooterHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
