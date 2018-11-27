package com.htmessage.cola_marketing.activity.homepageFunc.xiaoshou;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.common.BigImageActivity;
import com.htmessage.cola_marketing.activity.main.weike.WeikeContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class ChooseImageFragment extends Fragment {
    RecyclerView gridView;
    TextView tv_preview;

    String[] imgs;
    boolean isPics;
    ChooseAdapter adapter;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        final OnlyEditActivity activity = (OnlyEditActivity) getActivity();
        if (activity != null) {
            activity.showRightTextView("保存", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nameList = adapter.choosenList.toString();
                    if (isPics)
                        activity.pics = nameList.substring(1,nameList.length()-1);
                    else
                        activity.main_pic = nameList.substring(1,nameList.length()-1);
                    activity.onBackPressed();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_choose_image, container, false);

        assert getArguments() != null;
        imgs = getArguments().getStringArray("data");
        isPics = getArguments().getBoolean("isPics");

        initView(root);
        return root;
    }

    private void initView(View root) {
        final OnlyEditActivity activity = (OnlyEditActivity) getActivity();

        tv_preview = root.findViewById(R.id.tv_choose_preview);
        gridView = root.findViewById(R.id.rv_choose_img);

        GridLayoutManager manager = new GridLayoutManager(getActivity(),4);
        adapter = new ChooseAdapter(getActivity(), Arrays.asList(imgs));
        gridView.setLayoutManager(manager);
        gridView.setAdapter(adapter);

        if (isPics)
            adapter.setTotal(Integer.MAX_VALUE);
        else
            adapter.setTotal(1);

        tv_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, BigImageActivity.class)
                        .putExtra("images",adapter.getChoosenList())
                        .putExtra("isNetUrl",true)
                        .putExtra("imageBaseUrl",HTConstant.baseGoodsUrl));
            }
        });

        if (activity != null) {
            activity.showRightTextView("保存", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nameList = adapter.choosenList.toString();
                    if (isPics)
                        activity.pics = nameList.substring(1,nameList.length()-1);
                    else
                        activity.main_pic = nameList.substring(1,nameList.length()-1);
                    activity.onBackPressed();
                }
            });
        }
    }

    private static final class ChooseAdapter extends RecyclerView.Adapter<ChooseAdapter.ViewHolder> {
        private Context context;
        private List<String> list;
        private int total = Integer.MAX_VALUE;
        private ArrayList<String> choosenList = new ArrayList<>();

        ChooseAdapter(Context context, List<String> list) {
            this.context = context;
            this.list = list;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_state,iv_choose;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                iv_choose = itemView.findViewById(R.id.iv_choose);
                iv_state = itemView.findViewById(R.id.iv_choose_state);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_choose_img,viewGroup,false);
            final ViewHolder holder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (holder.iv_state.isSelected()) {
                        holder.iv_state.setSelected(false);
                        choosenList.remove(list.get(position));
                    } else if (choosenList.size() < total) {
                        holder.iv_state.setSelected(true);
                        choosenList.add(list.get(position));
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            RequestOptions options = new RequestOptions().placeholder(R.drawable.default_image2).error(R.drawable.default_image2);
            Glide.with(context).load(HTConstant.baseGoodsUrl+list.get(position)).apply(options).into(holder.iv_choose);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public String[] getChoosenList() {
            String[] choosen = new String[choosenList.size()];
            choosenList.toArray(choosen);
            return choosen;
        }
    }

}
