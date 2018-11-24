package com.htmessage.cola_marketing.activity.main.service;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.main.widget.SettingsView;


public class ServiceFragment extends Fragment implements View.OnClickListener {
    SettingsView sv_service_yiduiyi;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_service, container, false);
        initView(root);
        setListener();
        return root;
    }

    private void initView(View root) {
        sv_service_yiduiyi = root.findViewById(R.id.sv_service_yiduiyi);
    }

    private void setListener() {
        sv_service_yiduiyi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sv_service_yiduiyi:
                break;
        }
    }
}
