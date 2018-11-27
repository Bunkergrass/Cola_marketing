package com.htmessage.cola_marketing.activity.homepageFunc.baokuan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;

import java.util.List;

public class BaokuanAdapter extends RecyclerView.Adapter<BaokuanAdapter.ViewHolder> {
    private Context context;
    private List<JSONObject> list;
    private BaokuanListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewPager viewPager;
        LinearLayout ll_baokuan;
        TextView tv_trade;
        TextView tv_desc;
        PhotoAdapter adapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPager = itemView.findViewById(R.id.vp_baokuan);
            ll_baokuan = itemView.findViewById(R.id.ll_baokuan);
            tv_trade = itemView.findViewById(R.id.tv_baokuan_trade);
            tv_desc = itemView.findViewById(R.id.tv_baokuan_desc);
        }
    }

    public BaokuanAdapter(Context context, List<JSONObject> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_baokuan,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.ll_baokuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        JSONObject data = list.get(i);
        String name = data.getString("only_good_name");
        String desc = data.getString("only_good_text");
        String imgUrls = data.getString("only_good_pics");

        holder.tv_trade.setText(name);
        holder.tv_desc.setText(desc);
        PhotoAdapter adapter = new PhotoAdapter(imgUrls.split(","));
        holder.viewPager.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setListener(BaokuanListener listener) {
        this.listener = listener;
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

    public interface BaokuanListener {
        void onClick(int ID);
    }


    /* *
    * ViewPaper
    * */
    private class PhotoAdapter extends PagerAdapter {
        private String[] images;

        public PhotoAdapter(String[] images) {
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public ImageView instantiateItem(@NonNull ViewGroup container, int position) {
            String image = images[position];
            ImageView imageView = new ImageView(context);
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_image).error(R.drawable.default_image);
            Glide.with(context).load(HTConstant.baseGoodsUrl+image).apply(options).into(imageView);
            container.addView(imageView);
            return  imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container = null;
//            super.destroyItem(container, position, object);
        }
    }

}
