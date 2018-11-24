package com.htmessage.cola_marketing.activity.main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.contacts.ContactsFragment;
import com.htmessage.cola_marketing.activity.login.LoginActivity;
import com.htmessage.cola_marketing.activity.main.conversation.ConversationFragment;
import com.htmessage.cola_marketing.activity.main.discover.DiscoverFragment;
import com.htmessage.cola_marketing.activity.main.homepage.HomepageFragment;
import com.htmessage.cola_marketing.activity.main.my.MyAccountFragment;

public class MainActivity extends BaseActivity {
    BottomNavigationView main_navigation;
    Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            finish();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);

        fragments = new Fragment[]{new HomepageFragment(),new ConversationFragment(),new ContactsFragment(),new DiscoverFragment(),new MyAccountFragment()};
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void initView() {
        hideBackView();
        main_navigation = findViewById(R.id.main_navigation);
        setView();
    }

    private void setView() {
        main_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        main_navigation.setSelectedItemId(main_navigation.getMenu().getItem(0).getItemId());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.navigation_homepage:
                            replaceFragment(fragments[0]);
                            return true;
                        case R.id.navigation_weike:
                            replaceFragment(fragments[1]);
                            return true;
                        case R.id.navigation_service:
                            replaceFragment(fragments[2]);
                            return true;
                        case R.id.navigation_discover:
                            replaceFragment(fragments[3]);
                            return true;
                        case R.id.navigation_my:
                            replaceFragment(fragments[4]);
                            return true;
                    }
                    return false;
                }
            };

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_main_fragment, fragment);
        transaction.commit();
    }
}
