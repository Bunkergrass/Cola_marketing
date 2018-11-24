package com.htmessage.cola_marketing.activity.main.homepage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.utils.CommonUtils;
import com.htmessage.cola_marketing.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {
    private List<JSONObject> list;
    private Context context;
    private Date now_time;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_home_incomeranking);
        }
    }

    public RankAdapter(Context context, List<JSONObject> list) {
        this.context = context;
        this.list = list;
        this.now_time = new Date();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_incomeranking, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        JSONObject object = list.get(i);
        String name = object.getString("name");
        String time = object.getString("time");
        String money = object.getString("money");

        //delete
        name = "pos"+i+i+i;time = "2018-10-12 11:31:00";money = "1234567890";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String duration = CommonUtils.getDuration(context,time, format.format(now_time));

        SpannableString text = new SpannableString("• " + name + "，" + duration + "赚了" + money);
        ForegroundColorSpan span = new ForegroundColorSpan(context.getResources().getColor(R.color.Red));
        text.setSpan(span,text.length()-money.length(),text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewHolder.textView.setText(text);
    }

    public void refreshTime(){
        now_time = new Date();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (list.size() > 6){
            return 6;
        }
        return list.size();
    }
}
