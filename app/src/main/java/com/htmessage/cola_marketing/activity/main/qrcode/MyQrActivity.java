package com.htmessage.cola_marketing.activity.main.qrcode;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;


public class MyQrActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setTitle(R.string.me_qrcode);
        QrCodeFragment fragment = (QrCodeFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null){
            fragment = new QrCodeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame,fragment);
            transaction.commit();
        }
        new QrCodePrester(fragment);
    }
}