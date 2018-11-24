package com.htmessage.cola_marketing.activity.main.my.update;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;


public class ProfileUpdateActivity extends BaseActivity {
    public static final int TYPE_NICK = 0;
    public static final int TYPE_FXID = 1;
    public static final int TYPE_SIGN = 2;
    private UpdateProfilePrestener prestener;
    private String title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        int type = getIntent().getIntExtra("type", 0);
        switch (type){
            case 0:
                title = getString(R.string.change_nick);
                break;
            case 1:
                title = getString(R.string.change_mixin);
                break;
            case 2:
                title = getString(R.string.change_personalized_signature);
                break;
        }
        setTitle(title);
        ProfileUpdateFragment fragment = (ProfileUpdateFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null){
            fragment = new ProfileUpdateFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame,fragment);
            transaction.commit();
        }
        prestener = new UpdateProfilePrestener(fragment);
        showRightTextView(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prestener.update();
            }
        });
    }
}
