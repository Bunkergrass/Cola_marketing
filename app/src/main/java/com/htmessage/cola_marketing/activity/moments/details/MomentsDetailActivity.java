package com.htmessage.cola_marketing.activity.moments.details;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;

public class MomentsDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.moments);

        MomentsDetailFragment momentsDetailFragment = (MomentsDetailFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (momentsDetailFragment == null) {
            momentsDetailFragment=new MomentsDetailFragment();
            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.contentFrame,momentsDetailFragment);
            fragmentTransaction.commit();
        }

        momentsDetailFragment.setArguments(getIntent().getExtras());
        MomentsPresenter momentsPresenter=new MomentsPresenter(momentsDetailFragment);
    }
}
