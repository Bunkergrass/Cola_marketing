package com.htmessage.cola_marketing.activity.login.password;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;

public class PasswordResetActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        getData();
        initView();
    }

    private void initView() {
        PasswordResetFragment fragment = (PasswordResetFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null){
            fragment = new PasswordResetFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, fragment);
            transaction.commit();
        }
        PasswordPrester prester = new PasswordPrester(fragment);
    }

    private void getData() {
        boolean isReset = getIntent().getBooleanExtra("isReset", false);
        if (isReset){
            setTitle(R.string.resetPassword);
        }else{
            setTitle(R.string.find_pwd);
        }
    }
}
