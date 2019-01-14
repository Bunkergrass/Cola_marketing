package com.htmessage.cola_marketing.activity.homepageFunc.squad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.chat.ChatActivity;
import com.htmessage.cola_marketing.utils.ACache;
import com.htmessage.cola_marketing.utils.RecyclerItemClickListener;
import com.htmessage.sdk.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

public class SquadClassifyActivity extends BaseActivity {
    private RecyclerView rv_squads;
    private SquadListAdapter adapter;
    private List<JSONObject> list = new ArrayList<>();

    private static final String KEY_SQUAD_PROMOTE = HTApp.getInstance().getUsername()+"SQUAD_PROMOTE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squad_classify);
        setTitle("置顶小组");

        list.add(ACache.get(this).getAsJSONObject(KEY_SQUAD_PROMOTE));

        initView();
    }

    private void initView() {
        rv_squads = findViewById(R.id.rv_squad_classify);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new SquadListAdapter(this,list);
        rv_squads.setLayoutManager(manager);
        rv_squads.setAdapter(adapter);

        rv_squads.addOnItemTouchListener(new RecyclerItemClickListener(this, rv_squads, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onClick(int position, View itemView) {
                startActivity(new Intent(SquadClassifyActivity.this, ChatActivity.class)
                        .putExtra("userId",list.get(position).getString("gid"))
                        .putExtra("chatType", MessageUtils.CHAT_GROUP));
            }

            @Override
            public void onLongClick(int position, View itemView) {}
        }));
    }
}
