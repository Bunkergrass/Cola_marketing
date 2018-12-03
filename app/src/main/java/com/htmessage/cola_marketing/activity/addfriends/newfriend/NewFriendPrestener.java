package com.htmessage.cola_marketing.activity.addfriends.newfriend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.htmessage.cola_marketing.IMAction;
import com.htmessage.cola_marketing.domain.InviteMessage;
import com.htmessage.cola_marketing.domain.InviteMessgeDao;

import java.util.List;

/**
 * 类描述：NewFriendPrestener 描述:
 */
public class NewFriendPrestener implements NewFriendBasePresenter {
    private NewFriendView newFriendView;
    private InviteMessgeDao dao;
    private NewFriendRecivier friendRecivier;
    private List<InviteMessage> allInviteMessage;

    public NewFriendPrestener(NewFriendView newFriendView) {
        this.newFriendView = newFriendView;
        this.newFriendView.setPresenter(this);
        dao = new InviteMessgeDao(newFriendView.getBaseContext());
        allInviteMessage = dao.getMessagesList();
    }

    @Override
    public List<InviteMessage> getAllInviteMessage() {
        return allInviteMessage;
    }

    @Override
    public void registerRecivier() {
        friendRecivier = new NewFriendRecivier();
        IntentFilter intent = new IntentFilter(IMAction.ACTION_INVITE_MESSAGE);
        LocalBroadcastManager.getInstance(newFriendView.getBaseContext()).registerReceiver(friendRecivier, intent);
    }

    @Override
    public void startActivity(Context context, Class clazz) {
        newFriendView.getBaseActivity().startActivity(new Intent(context, clazz));
    }

    private void unRegisterRecivier() {
        if (friendRecivier != null) {
            LocalBroadcastManager.getInstance(newFriendView.getBaseContext()).unregisterReceiver(friendRecivier);
        }
    }

    @Override
    public void saveUnreadMessageCount(int count) {
        dao.saveUnreadMessageCount(count);
    }

    @Override
    public void refresh() {
        allInviteMessage.clear();
        if (dao == null){
            dao = new InviteMessgeDao(newFriendView.getBaseContext());
        }
        allInviteMessage.addAll(dao.getMessagesList());
    }

    @Override
    public void onDestory() {
        unRegisterRecivier();
        newFriendView = null;
    }

    @Override
    public void start() {}

    private class NewFriendRecivier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (IMAction.ACTION_INVITE_MESSAGE.equals(intent.getAction())) {
                newFriendView.refresh();
            }
        }
    }
}
