package com.htmessage.cola_marketing.activity.addfriends.invitefriend;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;


public class ContactsInviteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.contacts_friends);
        ContactsInviteFragment fragment = (ContactsInviteFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null){
            fragment = new ContactsInviteFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame,fragment);
            transaction.commit();
        }
    }
}
