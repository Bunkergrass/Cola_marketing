package com.htmessage.cola_marketing.activity.chat;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.chat.group.ChatSettingGroupActivity;
import com.htmessage.cola_marketing.activity.contacts.details.UserDetailsActivity;

import java.util.List;

/**
 * Created by huangfangyi on 2016/10/8.
 * qq 84543217
 */

public class GroupSetingsGridApdater extends BaseAdapter {
    private Context context;
    private List<JSONObject> datas;
    private boolean isOwner;

    public boolean isInDeleteMode = false;

    public GroupSetingsGridApdater(Context context, List<JSONObject> datas, boolean isOwner) {
        this.isOwner = isOwner;
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (isOwner && isInDeleteMode) {
            return datas.size();
        } else if (isOwner && !isInDeleteMode) {
            return datas.size() + 2;
        } else if (!isOwner) {
            return datas.size() + 1;
        }
        return 0;

    }

    @Override
    public JSONObject getItem(int position) {
        if (position < datas.size()) {
            return datas.get(position);
        } else {
            return null;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
           convertView = LayoutInflater.from(context).inflate(R.layout.item_group_setting_grid, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.iv_avatar);
            holder.textView = convertView.findViewById(R.id.tv_name);
            holder.badgeDeleteView = convertView.findViewById(R.id.badge_delete);
            convertView.setTag(holder);
        }
        final JSONObject jsonObject = getItem(position);

        if (jsonObject!=null) {
            final String username = jsonObject.getString(HTConstant.JSON_KEY_HXID);
            holder.textView.setText(jsonObject.getString(HTConstant.JSON_KEY_NICK));
            String avatar = jsonObject.getString(HTConstant.JSON_KEY_AVATAR);
            if (!TextUtils.isEmpty(avatar)){
                if (!avatar.contains("http")){
                    avatar = HTConstant.URL_AVATAR+avatar;
                }
            }
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar);
            Glide.with(context).load(avatar).apply(options).into(holder.imageView);
//            holder.imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    context.startActivity(new Intent(context, UserDetailsActivity.class).putExtra(HTConstant.KEY_USER_INFO,jsonObject.toJSONString()));
//                }
//            });
        }

        if (isOwner && isInDeleteMode) {
            final String username = jsonObject.getString(HTConstant.JSON_KEY_HXID);
            holder.badgeDeleteView.setVisibility(View.VISIBLE);
            holder.badgeDeleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteUser(username);
                    isInDeleteMode = false;
                    notifyDataSetChanged();
                }
            });
        } else if (isOwner && !isInDeleteMode) {
            holder.badgeDeleteView.setVisibility(View.INVISIBLE);
            if (position == getCount() - 1) {
                RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.smiley_minus_btn);
                Glide.with(context).load("").apply(options).into(holder.imageView);
              //  holder.imageView.setImageResource(R.drawable.);
                holder.textView.setText("");
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isInDeleteMode = true;
                        notifyDataSetChanged();
                    }
                });
            } else if (position == getCount() - 2) {
                RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.smiley_add_btn);
                Glide.with(context).load("").apply(options).into(holder.imageView);
                holder.textView.setText("");
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      ((ChatSettingGroupActivity)context).startAddMembers();
                    }
                });
            }
        } else if (!isOwner) {
            if (position == getCount() - 1) {
                holder.imageView.setImageResource(R.drawable.smiley_add_btn);
                holder.textView.setText("");
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     ((ChatSettingGroupActivity)context).startAddMembers();
                    }
                });
            }
        }

        return convertView;
    }


    private static class ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private ImageView badgeDeleteView;
    }

    private void deleteUser(final String userId) {
        ((ChatSettingGroupActivity)context).deleteMembers(userId);
    }
}


