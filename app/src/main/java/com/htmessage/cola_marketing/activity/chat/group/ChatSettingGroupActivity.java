package com.htmessage.cola_marketing.activity.chat.group;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.IMAction;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.chat.GroupSetingsGridApdater;
import com.htmessage.cola_marketing.domain.User;
import com.htmessage.cola_marketing.manager.ContactsManager;
import com.htmessage.cola_marketing.utils.ACache;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.ExpandGridView;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.GroupManager;
import com.htmessage.sdk.model.HTGroup;

import org.jivesoftware.smack.chat.Chat;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class ChatSettingGroupActivity extends BaseActivity {
    // 成员总数
    private TextView tv_m_total;
    // 成员列表
    private ExpandGridView userGridview;
    // 修改群名称、置顶、、、、
    private TextView tv_groupname;
    private TextView tv_groupDesc;

    private RelativeLayout rl_switch_chattotop;
    private RelativeLayout rl_switch_block_groupmsg;
    private RelativeLayout re_clear;

    // 状态变化
    private ImageView iv_switch_chattotop;
    private ImageView iv_switch_unchattotop;
    private ImageView iv_switch_block_groupmsg;
    private ImageView iv_switch_unblock_groupmsg;
    // 删除并退出

    private Button exitBtn;

    public String groupId;

    private GroupSetingsGridApdater adapter;
    public static ChatSettingGroupActivity instance;
    private static final int REQUEST_CODE_ADD_USER = 0;
    private static final int REQUEST_GROUP_NAME = 100;
    private static final int REQUEST_GROUP_DESE = 200;

    private String userId;
    private List<JSONObject> membersJSONArray = new ArrayList<>();
    private HTGroup htGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_setting_group);
        instance = this;
        userId = HTApp.getInstance().getUsername();
        initView();
        initData();
        //  updateGroup();
    }


    private void initView() {
        setTitle(R.string.chat_msg);
        tv_groupname = findViewById(R.id.tv_groupname);
        tv_groupDesc = findViewById(R.id.tv_groupDesc);
        rl_switch_chattotop = findViewById(R.id.rl_switch_chattotop);
        rl_switch_block_groupmsg = findViewById(R.id.rl_switch_block_groupmsg);
        re_clear = findViewById(R.id.re_clear);
        iv_switch_chattotop = findViewById(R.id.iv_switch_chattotop);
        iv_switch_unchattotop = findViewById(R.id.iv_switch_unchattotop);
        iv_switch_block_groupmsg = findViewById(R.id.iv_switch_block_groupmsg);
        iv_switch_unblock_groupmsg = findViewById(R.id.iv_switch_unblock_groupmsg);
        userGridview = findViewById(R.id.gridview);
        exitBtn = findViewById(R.id.btn_exit_grp);
        exitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                exitGroup();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initData() {
        // 获取传过来的groupid
        groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {
            finish();
            return;
        }
        htGroup = HTClient.getInstance().groupManager().getGroup(groupId);
        if (htGroup == null) {
            finish();
            return;
        }
        if (htGroup.getOwner().equals(HTApp.getInstance().getUsername())) {
            exitBtn.setText(R.string.delete_group);
        } else {
            exitBtn.setText(R.string.exit_group);
        }
        tv_groupname.setText(htGroup.getGroupName());
        tv_groupDesc.setText(htGroup.getGroupDesc());
        //JSONArray jsonArrayCache = ACache.get(getApplicationContext()).getAsJSONArray(userId + groupId);
        JSONArray jsonArrayCache = ACache.get(ChatSettingGroupActivity.this).getAsJSONArray(userId + groupId);
        arrayToList(jsonArrayCache, membersJSONArray);
        setTitle(R.string.chat_msg + "(" + String.valueOf(membersJSONArray.size()) + ")");
        adapter = new GroupSetingsGridApdater(this, membersJSONArray, htGroup.getOwner().equals(userId));
        userGridview.setAdapter(adapter);
        // 设置OnTouchListener
        userGridview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (adapter.isInDeleteMode) {
                            adapter.isInDeleteMode = false;
                            adapter.notifyDataSetChanged();
                            return true;
                        }
                        break;
                    default:
                        return false;
                }
                return false;
            }
        });
        this.findViewById(R.id.re_change_groupname).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ChatSettingGroupActivity.this, UpdateGroupActivity.class)
                        .putExtra("groupId", groupId)
                        .putExtra("type", UpdateGroupActivity.TYPE_GROUP_NAME), REQUEST_GROUP_NAME);
            }
        });
        this.findViewById(R.id.re_change_groupDesc).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ChatSettingGroupActivity.this, UpdateGroupActivity.class)
                        .putExtra("groupId", groupId)
                        .putExtra("type", UpdateGroupActivity.TYPE_GROUP_DESC), REQUEST_GROUP_DESE);
            }
        });
        this.findViewById(R.id.re_change_groupImgUrl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatSettingGroupActivity.this, UpdateGroupImgUrlActivity.class)
                        .putExtra("groupId", groupId)
                        .putExtra("groupName", htGroup.getGroupName()));
            }
        });
        refreshGroupMembersInserver();
        setTitle(R.string.chat_msg + "(" + String.valueOf(membersJSONArray.size()) + ")");
        re_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearGroupHistory();
            }
        });

    }


    /**
     * 清空群聊天记录
     */
    public void clearGroupHistory() {
        final Dialog progressDialog = HTApp.getInstance().createLoadingDialog(this, "正在清空聊天记录...");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {

            public void run() {
                HTClient.getInstance().conversationManager().deleteConversationAndMessage(groupId);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_EMPTY).putExtra("id", groupId));
                progressDialog.dismiss();
            }

        }, 2000);
    }

    /**
     * 删除群成员
     * 当删除的是自己，则就是退群或者解散群
     */
    protected void exitGroup() {
        if (userId.equals(htGroup.getOwner())) {
            //自己是群主，解散群
            deleteGroup();
        } else {
            leaveGroup();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_USER:// 添加群成员
                    refreshMembers();
                    break;
                case REQUEST_GROUP_NAME:
                    if (data != null) {
                        String value = data.getStringExtra("value");
                        if (value != null) {
                            tv_groupname.setText(value);
                        }
                    }
                    break;
                case REQUEST_GROUP_DESE:
                    if (data != null) {
                        String value = data.getStringExtra("value");
                        if (value != null) {
                            tv_groupDesc.setText(value);
                        }
                    }
                    break;
            }
        }
    }

    public void refreshMembers() {
        refreshGroupMembersInserver();
    }

    public void deleteMembers(String memberUserId) {
        final ProgressDialog progressDialog = new ProgressDialog(ChatSettingGroupActivity.this);
        progressDialog.setMessage(getString(R.string.deleting));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        String memberUserNick = memberUserId;
        User user = ContactsManager.getInstance().getContactList().get(memberUserId);
        if (user != null) {
            memberUserNick = user.getNick();
        }
        HTClient.getInstance().groupManager().deleteMember(groupId, memberUserId, memberUserNick, new GroupManager.CallBack() {
            @Override
            public void onSuccess(String data) {
                progressDialog.dismiss();
                refreshMembers();
                Toast.makeText(getApplicationContext(), R.string.delete_sucess, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.delete_failed, Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void refreshGroupMembersInserver() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("gid", groupId));
        params.add(new Param("uid", userId));
        new OkHttpUtils(this).post(params, HTConstant.URL_GROUP_MEMBERS, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.containsKey("code")) {
                    int code = Integer.parseInt(jsonObject.getString("code"));
                    if (code == 1000) {
                        if (jsonObject.containsKey("data") && jsonObject.get("data") instanceof JSONArray) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (jsonArray != null && jsonArray.size() != 0) {
                                ACache.get(ChatSettingGroupActivity.this).put(HTApp.getInstance().getUsername() + groupId, jsonArray);
                                arrayToList(jsonArray, membersJSONArray);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(String errorMsg) {}
        });
    }

    public void startAddMembers() {
        // 进入选人页面
        startActivityForResult((new Intent(ChatSettingGroupActivity.this, GroupAddMembersActivity.class)
                        .putExtra("groupId", groupId)), REQUEST_CODE_ADD_USER);
    }

    private void arrayToList(JSONArray jsonArray, List<JSONObject> jsonObjects) {
        jsonObjects.clear();
        if (jsonArray == null) {
            return;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObjectTemp = jsonArray.getJSONObject(i);
            if (jsonObjectTemp.getString(HTConstant.JSON_KEY_HXID).equals(htGroup.getOwner())) {
                jsonObjects.add(0, jsonObjectTemp);
            } else {
                jsonObjects.add(jsonObjectTemp);
            }
        }
    }

    private void deleteGroup() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.deleting_group));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        HTClient.getInstance().groupManager().deleteGroup(groupId, new GroupManager.CallBack() {
            @Override
            public void onSuccess(String data) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.delete_sucess, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.delete_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void leaveGroup() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.exting_group));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        HTClient.getInstance().groupManager().leaveGroup(groupId, HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_NICK), new GroupManager.CallBack() {
            @Override
            public void onSuccess(String data) {
                setResult(RESULT_OK);
                finish();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.exting_group_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.exting_group_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stickGroup(HTGroup htGroup,boolean isStick) {
        String topGroups = HTApp.getInstance().getUsername()+"topGroups";
        ArrayList<HTGroup> htGroups;
        htGroups = (ArrayList<HTGroup>) ACache.get(ChatSettingGroupActivity.this).getAsObject(topGroups);
        if (htGroups == null) {
            htGroups = new ArrayList<>();
            htGroups.add(htGroup);
            ACache.get(ChatSettingGroupActivity.this).put(topGroups,htGroups);
            return;
        }
        if (isStick){
            htGroups.add(htGroup);
            ACache.get(ChatSettingGroupActivity.this).put(topGroups,htGroups);
        } else {
            htGroups.remove(htGroup);
            ACache.get(ChatSettingGroupActivity.this).put(topGroups,htGroups);
        }
    }

}
