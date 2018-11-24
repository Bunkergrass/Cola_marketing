package com.htmessage.cola_marketing.activity.chat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.chat.group.ChatSettingGroupActivity;
import com.htmessage.cola_marketing.activity.main.MainActivity;
import com.htmessage.cola_marketing.domain.User;
import com.htmessage.cola_marketing.manager.ContactsManager;
import com.htmessage.cola_marketing.manager.NotifierManager;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.utils.MessageUtils;

import java.util.List;

public class ChatActivity extends BaseActivity {
    public static ChatActivity activityInstance;
    private ChatFragment chatFragment;
    public String toChatUsername;
    public int chatType;
    private ChatPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        activityInstance = this;
        toChatUsername = getIntent().getExtras().getString("userId");
        chatType = getIntent().getExtras().getInt("chatType", MessageUtils.CHAT_SINGLE);

        if (chatType == MessageUtils.CHAT_SINGLE){
            User user = ContactsManager.getInstance().getContactList().get(toChatUsername);
            String userNick = user.getNick();
            if (TextUtils.isEmpty(userNick)) {
                userNick = toChatUsername;
            }
            setTitle(userNick);
            showRightView(R.drawable.personal_imform, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ChatActivity.this, ChatSettingSingleActivity.class).putExtra("userId", toChatUsername));
                }
            });
        }else if(chatType == MessageUtils.CHAT_GROUP){
            HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(toChatUsername);
            String groupName = htGroup.getGroupName();
            if (TextUtils.isEmpty(groupName)) {
                groupName = toChatUsername;
            }
            setTitle(groupName);
            showRightView(R.drawable.personal_imform, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ChatActivity.this, ChatSettingGroupActivity.class).putExtra("groupId", toChatUsername));
                }
            });
        }

        chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (chatFragment == null) {
            chatFragment = new ChatFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, chatFragment).commit();
        }
        chatFragment.setArguments(getIntent().getExtras());

        presenter = new ChatPresenter(chatFragment,toChatUsername,chatType);
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }
    }

    private void toMainActivity() {
        if (isSingleActivity(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public boolean isSingleActivity(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningTasks(1);
        return ((ActivityManager.RunningTaskInfo) list.get(0)).numRunning == 1;
    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityInstance = this;
        NotifierManager.getInstance().cancel(Integer.parseInt(toChatUsername));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        chatFragment.onActivityResult(requestCode, resultCode, data);
    }
}