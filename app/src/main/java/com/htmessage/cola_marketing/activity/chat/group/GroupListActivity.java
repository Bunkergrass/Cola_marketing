package com.htmessage.cola_marketing.activity.chat.group;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.chat.ChatActivity;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;


public class GroupListActivity extends BaseActivity implements View.OnClickListener {
    private ListView groupListView;
    private TextView tv_total;
    private LinearLayout ll_new_group;

    protected List<HTGroup> grouplist = new ArrayList<>();
    private GroupsListAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        grouplist = HTClient.getInstance().groupManager().getAllGroups();

        initView();
        setView();
    }

    private void initView() {
        setTitle(R.string.group_chat);
        groupListView = findViewById(R.id.groupListView);
        ll_new_group = findViewById(R.id.ll_new_group);

        View footerView = LayoutInflater.from(this).inflate(R.layout.item_group_footer, null);
        tv_total = footerView.findViewById(R.id.tv_total);
        //        View headerView = LayoutInflater.from(this).inflate(R.layout.item_group_header, null);
        //        groupListView.addHeaderView(headerView, null, false);
        groupListView.addFooterView(footerView, null, false);
    }

    private void setView() {
        tv_total.setText(String.valueOf(grouplist.size()) + getString(R.string.group_size));

        groupAdapter = new GroupsListAdapter(this, grouplist);
        groupListView.setAdapter(groupAdapter);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(GroupListActivity.this, ChatActivity.class)
                        .putExtra("userId",groupAdapter.getItem(position).getGroupId())
                        .putExtra("chatType", MessageUtils.CHAT_GROUP));
//                    finish();
            }
        });

        ll_new_group.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_new_group:
                startActivity(new Intent(GroupListActivity.this, GroupAddMembersActivity.class));
                break;
        }
    }

    @Override
    public void onResume() {
        refresh();
        super.onResume();
    }

    private void refresh() {
        grouplist.clear();
        grouplist.addAll(HTClient.getInstance().groupManager().getAllGroups());
        groupAdapter.notifyDataSetChanged();
        tv_total.setText(grouplist.size()+getString(R.string.group_size));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
