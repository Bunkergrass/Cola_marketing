package com.htmessage.cola_marketing.activity.contacts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.IMAction;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.addfriends.newfriend.NewFriendsActivity;
import com.htmessage.cola_marketing.activity.chat.group.GroupListActivity;
import com.htmessage.cola_marketing.activity.contacts.details.UserDetailsActivity;
import com.htmessage.cola_marketing.domain.User;
import com.htmessage.cola_marketing.widget.HTAlertDialog;


/**
 * deprecated, has been converted to converted to ContactsFragment
 */
public class ContactsActivity extends BaseActivity implements View.OnClickListener, ContactsView {
    private ContactsAdapter adapter;
    private ListView listView;
    private Sidebar sidebar;
    private TextView tv_unread;
    private TextView tv_total;
    private ContactsPresenter contactsPresenter;

    public interface ContactsListener {
        void showInvitionCount(int count);
    }

    private ContactsListener contactsListener = new ContactsListener() {
        @Override
        public void showInvitionCount(int count) {
            //
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactsPresenter = new ContactsPresenter(this);

        initView();
        viewSetter();
        contactsPresenter.refreshContactsInServer();
        registerBroadReciever();
    }

    @Override
    public void onDestroy() {
        try {
            LocalBroadcastManager.getInstance(ContactsActivity.this).unregisterReceiver(myBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            showInvitionCount(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        listView = findViewById(R.id.list);
        sidebar = findViewById(R.id.sidebar);

        View footerView = LayoutInflater.from(ContactsActivity.this).inflate(R.layout.item_contact_list_footer, null);
        View headView = LayoutInflater.from(ContactsActivity.this).inflate(R.layout.item_contact_list_header, null);
        listView.addHeaderView(headView);
        listView.addFooterView(footerView);

        tv_unread = headView.findViewById(R.id.tv_unread);
        tv_total = footerView.findViewById(R.id.tv_total);
        headView.findViewById(R.id.re_newfriends).setOnClickListener(this);
        headView.findViewById(R.id.re_chatroom).setOnClickListener(this);
    }

    private void viewSetter() {
        showSiderBar();
        //showRightView(R.drawable.ic_add, );

        adapter = new ContactsAdapter(ContactsActivity.this, contactsPresenter.getContactsListInDb());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position != adapter.getCount() + 1) {
                    User user = adapter.getItem(position - 1);
                    startActivity(new Intent(ContactsActivity.this, UserDetailsActivity.class).putExtra(HTConstant.KEY_USER_INFO, user.getUserInfo()));
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position != adapter.getCount() + 1) {
                    User user = adapter.getItem(position - 1);
                    showItemDialog(user);
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.re_newfriends:
                startActivityForResult(new Intent(ContactsActivity.this, NewFriendsActivity.class), 10086);
                contactsPresenter.clearInvitionCount();
                break;
            case R.id.re_chatroom:
                startActivity(new Intent(ContactsActivity.this, GroupListActivity.class));
                break;
        }
    }

    @Override
    public void setPresenter(ContactsPresenter presenter) {}

    @Override
    public Activity getBaseActivity() {
        return this;
    }

    @Override
    public void showItemDialog(final User user) {
        final HTAlertDialog HTAlertDialog = new HTAlertDialog(ContactsActivity.this, null, new String[]{getResources().getString(R.string.delete)});
        HTAlertDialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        contactsPresenter.deleteContacts(user.getUsername());
                        break;
                    case 1:
                        contactsPresenter.moveUserToBlack(user.getUsername());
                        break;
                }
                refresh();
            }
        });
    }

    @Override
    public void showSiderBar() {
        sidebar.setVisibility(View.VISIBLE);
        sidebar.setListView(listView);
    }

    @Override
    public void showInvitionCount(int count) {
        if (count != 0) {
            tv_unread.setVisibility(View.VISIBLE);
//            tv_unread.setText(count+"");
        } else {
            tv_unread.setVisibility(View.GONE);
        }
        contactsListener.showInvitionCount(count);
    }

    @Override
    public void showContactsCount(int count) {
        tv_total.setText(count + getString(R.string.more_people));
    }

    @Override
    public void refresh() {
        adapter.notifyDataSetChanged();
        showContactsCount(contactsPresenter.getContactsCount());
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (IMAction.ACTION_CONTACT_CHANAGED.equals(action)) {
                contactsPresenter.refreshContactsInLocal();
            } else if (IMAction.ACTION_INVITE_MESSAGE.equals(action)) {
                showInvitionCount(1);
            } else if (IMAction.CMD_DELETE_FRIEND.equals(action)) {
                String userId = intent.getStringExtra(HTConstant.JSON_KEY_HXID);
                contactsPresenter.deleteContactOnBroast(userId);
                refresh();
            }
        }
    }

    private MyBroadcastReceiver myBroadcastReceiver;

    private void registerBroadReciever() {
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMAction.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(IMAction.ACTION_INVITE_MESSAGE);
        intentFilter.addAction(IMAction.CMD_DELETE_FRIEND);
        LocalBroadcastManager.getInstance(ContactsActivity.this).registerReceiver(myBroadcastReceiver, intentFilter);
    }

}
