package com.htmessage.cola_marketing.activity.myLists.myProject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.R;

import java.util.List;

public class ProjectAdapter extends BaseAdapter {
    private List<JSONObject> list;
    private Context context;

    public ProjectAdapter(Context context, List<JSONObject> list) {
        this.list = list;
        this.context = context;
    }

    private static class ViewHolder {
        EditText tv_name;
        TextView tv_id;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_hint_set,parent,false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.tv_id = convertView.findViewById(R.id.tv_key_text);
            holder.tv_name = convertView.findViewById(R.id.et_hint_text);
            convertView.setTag(holder);
        }

        holder.tv_id.setText("项目 " + (position+1));
        holder.tv_name.setText(list.get(position).getString("explain_name"));

        return convertView;
    }

    public void refreshList(List<JSONObject> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addList(List<JSONObject> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

}
