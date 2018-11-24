package com.htmessage.cola_marketing.activity.addfriends.add.next;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;

public class AddFriendsNextActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        findViewById(R.id.title).setVisibility(View.GONE);
        AddFriendNextFragment fragment = (AddFriendNextFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null){
            fragment = new AddFriendNextFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame,fragment);
            transaction.commit();
        }
        new AddFriendNextPrestener(fragment);
    }
}