package com.htmessage.cola_marketing.activity.main.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.SettingsActivity;
import com.htmessage.cola_marketing.activity.main.my.profile.ProfileActivity;
import com.htmessage.cola_marketing.activity.main.widget.HomepageFuncView;
import com.htmessage.cola_marketing.activity.main.widget.SettingsView;
import com.htmessage.cola_marketing.activity.moments.MomentsFriendActivity;
import com.htmessage.cola_marketing.activity.myLists.myApplies.MyAppliesActivity;
import com.htmessage.cola_marketing.activity.myLists.myMarkets.MyMarketsActivity;
import com.htmessage.cola_marketing.activity.myLists.myProject.MyProjectActivity;
import com.jrmf360.walletlib.JrmfWalletClient;


public class MyAccountFragment extends Fragment implements View.OnClickListener {
    HomepageFuncView hfv_personal,hfv_setting,hfv_wallet,hfv_album;
    SettingsView sv_project,sv_apply,sv_market,sv_tutor;
    ImageView iv_avatar;
    TextView tv_nick;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_account, container, false);
        initView(root);
        return root;
    }

    private void initView(View root) {
        hfv_personal = root.findViewById(R.id.hfv_personal);
        hfv_wallet = root.findViewById(R.id.hfv_wallet);
        hfv_setting = root.findViewById(R.id.hfv_setting);
        hfv_album = root.findViewById(R.id.hfv_album);
        sv_apply = root.findViewById(R.id.sv_my_applys);
        sv_market = root.findViewById(R.id.sv_my_markets);
        sv_project = root.findViewById(R.id.sv_projects_list);
        sv_tutor = root.findViewById(R.id.sv_my_tutor);
        iv_avatar = root.findViewById(R.id.iv_my_avatar);
        tv_nick = root.findViewById(R.id.tv_my_nick);

        hfv_setting.setOnClickListener(this);
        hfv_wallet.setOnClickListener(this);
        hfv_personal.setOnClickListener(this);
        hfv_album.setOnClickListener(this);
        sv_project.setOnClickListener(this);
        sv_market.setOnClickListener(this);
        sv_apply.setOnClickListener(this);
        sv_tutor.setOnClickListener(this);

        RequestOptions options = new RequestOptions().optionalCircleCrop().placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar);
        Glide.with(this).load(HTApp.getInstance().getUserAvatar()).apply(options).into(iv_avatar);
        tv_nick.setText(HTApp.getInstance().getUserNick());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hfv_personal:
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                break;
            case R.id.hfv_wallet:
                JrmfWalletClient.intentWallet(getActivity(),
                        HTApp.getInstance().getUsername(), HTApp.getInstance().getThirdToken(),
                        HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_NICK),
                        HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_AVATAR));
                break;
            case R.id.hfv_setting:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            case R.id.hfv_album:
                startActivity(new Intent(getActivity(), MomentsFriendActivity.class).putExtra("userId", HTApp.getInstance().getUsername()));
                break;
            case R.id.sv_my_applys:
                startActivity(new Intent(getActivity(), MyAppliesActivity.class));
                break;
            case R.id.sv_my_markets:
                startActivity(new Intent(getActivity(), MyMarketsActivity.class));
                break;
            case R.id.sv_projects_list:
                startActivity(new Intent(getActivity(), MyProjectActivity.class));
                break;
            case R.id.sv_my_tutor:
                break;
        }
    }
}
