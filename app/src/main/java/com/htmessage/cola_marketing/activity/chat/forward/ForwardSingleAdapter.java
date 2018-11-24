package com.htmessage.cola_marketing.activity.chat.forward;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.domain.User;

import java.util.ArrayList;
import java.util.HashMap;

public class ForwardSingleAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<User> beans = new ArrayList<>();

    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;

    public ForwardSingleAdapter(Context mContext, ArrayList<User> beans) {
        this.mContext = mContext;
        this.beans = beans;
        isSelected = new HashMap<Integer, Boolean>();
        initDate();
    }
    @Override
    public int getCount() {
        return beans.size();
    }
    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < beans.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    @Override
    public User getItem(int position) {
        return beans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.item_friends_list,null);
            holder = new ViewHolder();
            holder.iv_game_avatar = convertView.findViewById(R.id.iv_friend_avatar);
            holder.tv_username = convertView.findViewById(R.id.tv_friend_name);
            holder.checkbox = convertView.findViewById(R.id.cb_friends_list);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        User gameBean = beans.get(position);
        holder.tv_username.setText(gameBean.getNick());
        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar);
        Glide.with(mContext).load(gameBean.getAvatar()).apply(options).into(holder.iv_game_avatar);
        // 根据isSelected来设置checkbox的选中状况
        holder.checkbox.setChecked(getIsSelected().get(position));
        return convertView;
    }

    public static class ViewHolder{
        public TextView tv_username;
        public ImageView iv_game_avatar;
        public CheckBox checkbox;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

}
