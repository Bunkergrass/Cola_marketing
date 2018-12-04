package com.htmessage.cola_marketing.activity.chat.prevideocall;

import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.domain.User;
import com.htmessage.cola_marketing.utils.ACache;
import com.htmessage.cola_marketing.utils.DensityUtil;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;

import java.util.ArrayList;
import java.util.List;


public class PreVideoCallPrestener implements PreVideoCallBasePresenter {
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<String> userIds = new ArrayList<>();
    private ArrayList<User> exitsUsers = new ArrayList<>();
    private PreVideoVideoCallView videoVideoCallView;

    public PreVideoCallPrestener(PreVideoVideoCallView videoVideoCallView) {
        this.videoVideoCallView = videoVideoCallView;
        this.videoVideoCallView.setPresenter(this);
        JSONObject userJson = HTApp.getInstance().getUserJson();
        User user = new User(HTApp.getInstance().getUsername());
        user.setNick(userJson.getString(HTConstant.JSON_KEY_NICK));
        user.setAvatar(userJson.getString(HTConstant.JSON_KEY_AVATAR));
        user.setUsername(HTApp.getInstance().getUsername());
        user.setUserInfo(userJson.toJSONString());
        users.add(user);
        userIds.add(HTApp.getInstance().getUsername());
    }

    @Override
    public void getGroupMembers(final String groupId) {
        if (TextUtils.isEmpty(groupId)){
            videoVideoCallView.getBaseActivity().finish();
            return;
        }
        List<Param> params = new ArrayList<>();
        params.add(new Param("gid", groupId));
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        new OkHttpUtils(videoVideoCallView.getBaseContext()).post(params, HTConstant.URL_GROUP_MEMBERS, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.containsKey("code")) {
                    int code = Integer.parseInt(jsonObject.getString("code"));
                    if (code == 1000) {
                        if (jsonObject.containsKey("data") && jsonObject.get("data") instanceof JSONArray) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (jsonArray != null && jsonArray.size() != 0) {
                                ACache.get(videoVideoCallView.getBaseContext()).put(HTApp.getInstance().getUsername() + groupId, jsonArray);
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    User user = new User(jsonObject1.getString(HTConstant.JSON_KEY_HXID));
                                    user.setUsername(jsonObject1.getString(HTConstant.JSON_KEY_HXID));
                                    user.setUserInfo(jsonObject1.toJSONString());
                                    user.setNick(jsonObject1.getString(HTConstant.JSON_KEY_NICK));
                                    user.setAvatar(jsonObject1.getString(HTConstant.JSON_KEY_AVATAR));
                                    exitsUsers.add(user);
                                }
                            }
                        }
                        videoVideoCallView.reFreshView(exitsUsers,getUserIds(),getUsers());
                    }
                }
            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });
    }

    @Override
    public ArrayList<String> getUserIds() {
        return userIds;
    }

    @Override
    public ArrayList<User> getUsers() {
        return users;
    }

    @Override
    public ArrayList<User> getExitUsers() {
        return exitsUsers;
    }

    @Override
    public void showCheckedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(videoVideoCallView.getBaseContext());
        View dialogView = View.inflate(videoVideoCallView.getBaseContext(), R.layout.dialog_alert, null);
        TextView tv_delete_people = dialogView.findViewById(R.id.tv_alert_content);
        TextView tv_delete_title = dialogView.findViewById(R.id.tv_alert_title);
        View view_line_dialog = dialogView.findViewById(R.id.view_line_dialog);
        TextView tv_cancle = dialogView.findViewById(R.id.tv_dialog_cancel);

        tv_cancle.setVisibility(View.GONE);
        view_line_dialog.setVisibility(View.GONE);
        TextView tv_ok = dialogView.findViewById(R.id.tv_dialog_ok);
        tv_delete_people.setText(R.string.Support_for_up_to_9_people_at_the_same_time_voice_chat);
        tv_delete_title.setText(R.string.prompt);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DensityUtil.dp2px(videoVideoCallView.getBaseContext(),320);
        window.setAttributes(params);

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public String getCallId() {
        String callId = null;
        if (users.size() != 1) {
            if (users.size() !=1 || users.size()>1){
                for (int i = 1; i < users.size()-1; i++) {
                    callId += users.get(i).getUsername()+"_";
                }
                callId = users.get(0).getUsername() +"_"+callId+users.get(users.size()-1).getUsername();
            }else{
                callId = users.get(0).getUsername();
            }
        } else {
            callId =null;
            Toast.makeText(videoVideoCallView.getBaseContext(), R.string.please_check_people, Toast.LENGTH_SHORT).show();
        }
        return callId;
    }

    @Override
    public void onDestory() {
        videoVideoCallView = null;
    }

    @Override
    public void start() {

    }

    public View setViewParams(final User user) {
        View view = getView(user);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(160, 160,1);
        params.setMargins(5,5,5,5);
        view.setLayoutParams(params);
        return view;
    }

    public View getView(final User user) {
        View view = View.inflate(videoVideoCallView.getBaseContext(), R.layout.item_pre_videocall_gridview, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(user.getNick());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 180,1);
        params.setMargins(5,5,5,5);
        view.setLayoutParams(params);
        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar);
        Glide.with(videoVideoCallView.getBaseContext()).load(user.getAvatar()).apply(options).into(imageView);
        return view;
    }
}
