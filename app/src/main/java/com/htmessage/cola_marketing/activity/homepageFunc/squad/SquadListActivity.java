package com.htmessage.cola_marketing.activity.homepageFunc.squad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.chat.group.GroupAddMembersActivity;
import com.htmessage.sdk.model.HTGroup;

import java.util.ArrayList;
import java.util.List;

public class SquadListActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView rv_squads;
    private LinearLayout ll_new_group,ll_top_groups,ll_my_groups;

    private SquadListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squad_list);
        setTitle("战略小组");

        ll_my_groups = findViewById(R.id.ll_my_groups);
        ll_new_group = findViewById(R.id.ll_new_group);
        ll_top_groups = findViewById(R.id.ll_top_groups);
        rv_squads = findViewById(R.id.rv_squad_list);

        ll_my_groups.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_new_group:
                startActivity(new Intent(SquadListActivity.this, GroupAddMembersActivity.class));
                break;
        }
    }
}
