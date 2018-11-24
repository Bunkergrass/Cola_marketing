package com.htmessage.cola_marketing.activity.main.discover;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.main.widget.SettingsView;
import com.htmessage.cola_marketing.activity.moments.MomentsActivity;
import com.htmessage.cola_marketing.zxing.activity.CaptureActivity;


public class DiscoverFragment extends Fragment implements View.OnClickListener {
    SettingsView sv_friends,sv_scan;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_discover, container, false);
        initView(root);
        return root;
    }

    private void initView(View root) {
        sv_friends = root.findViewById(R.id.sv_dis_friendship);
        sv_scan = root.findViewById(R.id.sv_dis_scan);

        sv_friends.setOnClickListener(this);
        sv_scan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sv_dis_friendship:
                startActivity(new Intent(getActivity(), MomentsActivity.class));
                break;
            case R.id.sv_dis_scan:
                startActivity(new Intent(getActivity(), CaptureActivity.class));
                break;
        }
    }
}
