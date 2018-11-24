package com.htmessage.cola_marketing.activity.main.weike;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WeikeCommentsAdapter extends RecyclerView.Adapter <WeikeCommentsAdapter.ViewHolder> {
    private static int ITEM_TYPE_NORMAL = 0;
    private static int ITEM_TYPE_HEADER = 1;
    private static int ITEM_TYPE_FOOTER = 2;
    private View headerView,footerView;

    private AdpterListener listener;
    private Context context;
    private List<JSONObject> list;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_avatar;
        TextView tv_nick;
        TextView tv_time;
        ImageView iv_fabulous;
        TextView tv_fabulous;
        ImageView iv_reply;
        TextView tv_content;
        ListView lv_inside;
        int page = 1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_avatar = itemView.findViewById(R.id.iv_weike_comments_avatar);
            tv_nick = itemView.findViewById(R.id.tv_weike_comments_nick);
            tv_time = itemView.findViewById(R.id.tv_weike_comments_time);
            iv_fabulous = itemView.findViewById(R.id.iv_weike_comments_fabulous);
            tv_fabulous = itemView.findViewById(R.id.tv_weike_comments_fabulous);
            iv_reply = itemView.findViewById(R.id.iv_weike_comments_reply);
            tv_content = itemView.findViewById(R.id.tv_weike_comments_contents);
            lv_inside = itemView.findViewById(R.id.lv_weike_inside);
        }
    }

    public WeikeCommentsAdapter(Context context,List<JSONObject> list) {
        this.context = context;
        this.list = list;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
        notifyItemInserted(0);
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null)
            return ITEM_TYPE_HEADER;
        if (position == getItemCount()+1 && footerView != null)
            return ITEM_TYPE_FOOTER;
        return ITEM_TYPE_NORMAL;
        //return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        int count = list.size();
        if (headerView != null)
            count++;
        if (footerView != null)
            count++;
        return count;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == ITEM_TYPE_HEADER){
            return new ViewHolder(headerView);
        }
        if (viewType == ITEM_TYPE_FOOTER) {
            return new ViewHolder(footerView);
        }
        //View view = View.inflate(context, R.layout.item_weike_comments,null);
        View view = LayoutInflater.from(context).inflate(R.layout.item_weike_comments,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        int position = holder.getAdapterPosition();
        holder.lv_inside.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int pos) {
        int viewType = getItemViewType(pos);
        if (viewType == ITEM_TYPE_HEADER) {return;}
        if (viewType == ITEM_TYPE_FOOTER) {return;}

        int realPosition = pos;
        if (headerView != null)
            realPosition--;
        JSONObject item = list.get(realPosition);

        int comment_nums = item.getInteger("comment_nums");
        final int is_good = item.getInteger("is_good");
        int time = item.getInteger("comment_time");
        final String post_comment_id = item.getString("post_comment_id");
        final String post_id = item.getString("post_id");
        final String uid = item.getString("uid");
        String comment_text = item.getString("comment_text");
        Integer goods = item.getInteger("goods");
        String usernick = item.getString("usernick");
        String avatar = item.getString("avatar");
        JSONArray replys = item.getJSONArray("comment_reply_list");

        final InsideAdapter adapter = new InsideAdapter(context,replys);
        viewHolder.lv_inside.setAdapter(adapter);
        View footer = View.inflate(context,R.layout.item_weike_reply,null);
        if (comment_nums > 3 && viewHolder.lv_inside.getFooterViewsCount() == 0) {
            TextView textView = footer.findViewById(R.id.tv_weike_reply);
            textView.setText("更多"+(comment_nums - viewHolder.lv_inside.getCount())+"条回复");
            textView.setTextColor(context.getColor(R.color.LightBlue));
            viewHolder.lv_inside.addFooterView(footer);
        }
        if (viewHolder.lv_inside.getCount() == comment_nums+1 && viewHolder.lv_inside.getFooterViewsCount() != 0) {
            viewHolder.lv_inside.removeFooterView(footer);
        }
        setListViewHeightBasedOnChildren(viewHolder.lv_inside);

        viewHolder.tv_time.setText(getStringTime(time*1000));
        viewHolder.tv_content.setText(comment_text);
        if (goods != 0){
            viewHolder.iv_fabulous.setImageResource(R.drawable.icon_fabulous);
            viewHolder.tv_fabulous.setVisibility(View.VISIBLE);
            viewHolder.tv_fabulous.setText(goods+"");
        }
        viewHolder.tv_nick.setText(usernick);
        RequestOptions options = new RequestOptions().placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar);
        Glide.with(context).load(avatar).apply(options).into(viewHolder.iv_avatar);

        viewHolder.iv_fabulous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.postFabulous(pos,post_id,post_comment_id,is_good);
            }
        });
        viewHolder.iv_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.sendReply(pos,post_comment_id,"");
            }
        });
        viewHolder.lv_inside.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (viewHolder.lv_inside.getFooterViewsCount() != 0
                        && position == viewHolder.lv_inside.getCount()-1){
                    viewHolder.page++;
                    listener.getReplyList(pos,viewHolder.page,post_comment_id);
                } else {
                    listener.sendReply(pos,post_comment_id,adapter.getUserId(position));
                }
            }
        });
    }

    private String getStringTime(long ms){
        Date date = new Date(ms);
        Long now = System.currentTimeMillis();
        if (now-ms > 24*60*60*1000){
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return format.format(date);
        } else if (now-ms < (long)365*24*60*60*1000) {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
            return format.format(date);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return format.format(date);
        }
    }

    public void updateFabulous(int position,int goods,int type){
        list.get(position - 1).remove("goods");
        list.get(position - 1).put("goods",goods);
        list.get(position - 1).remove("is_good");
        list.get(position - 1).put("is_good",type == 0 ? 1:0);
        notifyItemChanged(position);
    }

    public void refreshComment(List<JSONObject> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void refreshOne(int position,JSONObject object) {
        this.list.set(position-1,object);
        notifyItemChanged(position);
    }

    public void addComment(List<JSONObject> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void updateInside(int position,JSONArray data) {
        this.list.get(position - 1).getJSONArray("comment_reply_list").addAll(data);
        notifyItemChanged(position);
    }

    public void setListener(AdpterListener listener) {
        this.listener = listener;
    }

    interface AdpterListener {
        void postFabulous(int position, String tid, String pid, int type);

        void sendReply(int position, String pid, String tid);

        void getReplyList(int position, int page, String pid);
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }


    /**
     * Inside ListView
     * */
    class InsideAdapter extends BaseAdapter {
        List<JSONObject> list;
        Context context;

        public InsideAdapter(Context context, JSONArray array) {
            this.context = context;
            list = JSONArray.parseArray(array.toJSONString(),JSONObject.class);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject object = list.get(position);
            String from = object.getString("usernick");
            String to = object.getString("to_usernick");
            String content = object.getString("reply_text");

            View view = View.inflate(context, R.layout.item_weike_reply,null);
            TextView textView = view.findViewById(R.id.tv_weike_reply);

            ForegroundColorSpan span = new ForegroundColorSpan(context.getResources().getColor(R.color.LightBlue));
            SpannableString spanFrom = new SpannableString(from);
            SpannableString spanTo = new SpannableString(to);
            spanFrom.setSpan(span,0,from.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanTo.setSpan(span,0,to.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            textView.append(spanFrom);
            textView.append(" 回复 ");
            textView.append(spanTo);
            textView.append("："+content);

            return view;
        }

        public String getUserId(int position) {
            return list.get(position).getString("uid");
        }

    }
}
