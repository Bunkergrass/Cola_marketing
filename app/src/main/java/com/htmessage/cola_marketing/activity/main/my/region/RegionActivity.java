package com.htmessage.cola_marketing.activity.main.my.region;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;

public class RegionActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		setTitle(R.string.region);
		RegionFragment fragment = (RegionFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
		if (fragment == null){
			fragment = new RegionFragment();
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.contentFrame,fragment);
			transaction.commit();
		}
		new RegionPresenter(fragment);
	}
}
