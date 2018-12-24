package com.htmessage.cola_marketing.activity.chat.group;

import android.os.Bundle;
import android.widget.ListView;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTGroup;

import java.util.ArrayList;
import java.util.List;

public class GroupsClassifiedActivity extends BaseActivity {
    protected List<HTGroup> grouplist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_classified);
        setTitle("");

        ListView groupList = findViewById(R.id.lv_group_classified);
        grouplist = HTClient.getInstance().groupManager().getAllGroups();
        GroupsListAdapter adapter = new GroupsListAdapter(this,grouplist);

    }
}
