package com.htmessage.cola_marketing.activity.chat.forward;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.IMAction;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.domain.User;
import com.htmessage.cola_marketing.manager.ContactsManager;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageFileBody;
import com.htmessage.sdk.model.HTMessageImageBody;
import com.htmessage.sdk.model.HTMessageLocationBody;
import com.htmessage.sdk.model.HTMessageVideoBody;
import com.htmessage.sdk.model.HTMessageVoiceBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ForwardSingleActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<User> friends = new ArrayList<>();
    private TextView tv_group_check;
    private ListView lv_forward_list;
    private ForwardSingleAdapter adAdapter;
    private String forwordType;
    private String localUrl,obj,exobj;
    private HTMessage message1;
    private JSONObject object,extJSON;
    private String imagePath,msgId,toChatUsername;
    private List<HTMessage> msgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_single);
        getData();
        initView();
        initData();
        setListener();
    }

    private void getData() {
        obj = getIntent().getStringExtra("obj");
        object = JSONObject.parseObject(obj);
        imagePath = object.getString("imagePath");
        forwordType = object.getString("forwordType");
        localUrl = object.getString("localPath");
        msgId = object.getString("msgId");
        toChatUsername =object.getString("toChatUsername");
        exobj =object.getString("exobj");
        getContacts();
    }

    private void getContacts() {
        friends.clear();
        Map<String, User> users = ContactsManager.getInstance().getContactList();
        Iterator<Map.Entry<String, User>> iterator = users.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, User> entry = iterator.next();
            friends.add(entry.getValue());
        }
        // 对list进行排序
        Collections.sort(friends, new PinyinComparator() {});
    }

//    public void getContactsInServer() {
//        if (!HTClient.getInstance().isLogined()) {
//            return;
//        }
//        List<Param> params = new ArrayList<Param>();
//        new OkHttpUtils(this).post(params, HTConstant.URL_FriendList, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                int code = jsonObject.getIntValue("code");
//                switch (code) {
//                    case 1:
//                        JSONArray friends = jsonObject.getJSONArray("user");
//                        if (friends != null || friends.size() != 0) {
//                            List<User> users = new ArrayList<User>();
//                            for (int i = 0; i < friends.size(); i++) {
//                                JSONObject friend = friends.getJSONObject(i);
//                                User user = CommonUtils.Json2User(friend);
//                                users.add(user);
//                            }
//                            ContactsManager.getInstance().saveContactList(users);
//                        }
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//
//            }
//        });
//    }

    private void initView() {
        tv_group_check = findViewById(R.id.tv_group_check);
        lv_forward_list = findViewById(R.id.lv_forward_list);
        showRightButton(this);
    }

    private void initData() {
        msgList = HTClient.getInstance().messageManager().getMessageList(toChatUsername);
        extJSON = JSONObject.parseObject(exobj);
        message1 = getCopyMessage(msgId);
        getContacts();
        adAdapter = new ForwardSingleAdapter(ForwardSingleActivity.this, friends);
        lv_forward_list.setAdapter(adAdapter);
    }

    private void setListener() {
        tv_group_check.setOnClickListener(this);
        lv_forward_list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckBox checkBox = view.findViewById(R.id.cb_friends_list);
        checkBox.toggle();
        ForwardSingleAdapter.getIsSelected().put(position, checkBox.isChecked());//将CheckBox的选中状况记录下来
        // 调整选定条目
        if (checkBox.isChecked() == true) {
            users.add(adAdapter.getItem(position));
        } else {
            users.remove(adAdapter.getItem(position));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_group_check:
                //startActivityForResult(new Intent(ForwardSingleActivity.this, ForwardGroupActivity.class).putExtra("obj",object.toJSONString()), 3002);
                break;
            case R.id.btn_right:
                if (users.size() == 0 || users == null) {
                    Toast.makeText(ForwardSingleActivity.this, R.string.please_check_contant, Toast.LENGTH_SHORT).show();
                    return;
                }
                showMessageFarWordDialog(users, forwordType, localUrl);
                break;
        }
    }

    private void showMessageFarWordDialog(final ArrayList<User> users, final String forwordType, final String localUrl) {
        AlertDialog.Builder buidler = new AlertDialog.Builder(this);
        View view = View.inflate(ForwardSingleActivity.this, R.layout.dialog_alert,null);
        TextView tv_forward_title = view.findViewById(R.id.tv_alert_title);
        TextView tv_forward_content = view.findViewById(R.id.tv_alert_content);
        ImageView iv_foward = view.findViewById(R.id.iv_alert);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);

        tv_forward_content.setText(R.string.forword_always);
        tv_forward_title.setText(getString(R.string.forword_people).replace("1", String.valueOf(users.size())));
        if ("image".equals(forwordType) && localUrl != null) {
            iv_foward.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(this).load(imagePath).apply(options).into(iv_foward);
        }

        buidler.setView(view);
        final AlertDialog dialog = buidler.show();
        tv_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                for (int i = 0; i < users.size(); i++) {
                    User easeUser = users.get(i);
                    switch (forwordType) {
                        case "file":
                            HTMessageFileBody fileBody = (HTMessageFileBody) message1.getBody();
                            HTMessage emMessage = HTMessage.createFileSendMessage(easeUser.getUsername(), localUrl, fileBody.getSize());
                            sendMessage(emMessage);
                            break;
                        case "text":
                            HTMessage textMessage= HTMessage.createTextSendMessage(easeUser.getUsername(),localUrl);
                            sendMessage(textMessage);
                            break;
                        case "video":
                            HTMessageVideoBody videoBody = (HTMessageVideoBody) message1.getBody();
                            HTMessage emvideoMessage = HTMessage.createVideoSendMessage(easeUser.getUsername(), localUrl, videoBody.getLocalPathThumbnail(), videoBody.getVideoDuration());
                            sendMessage(emvideoMessage);
                            break;
                        case "voice":
                            HTMessageVoiceBody voiceBody = (HTMessageVoiceBody) message1.getBody();
                            HTMessage voiceMSg = HTMessage.createVoiceSendMessage(easeUser.getUsername(), localUrl, voiceBody.getAudioDuration());
                            sendMessage(voiceMSg);
                            break;
                        case "image":
                            HTMessageImageBody imageBody = (HTMessageImageBody) message1.getBody();
                            HTMessage message = HTMessage.createImageSendMessage(easeUser.getUsername(), localUrl, imageBody.getSize());
                            sendMessage(message);
                            break;
                        case "type_location":
                            HTMessageLocationBody locationBody = (HTMessageLocationBody) message1.getBody();
                            HTMessage locationSendMessage = HTMessage.createLocationSendMessage(easeUser.getUsername(), locationBody.getLatitude(), locationBody.getLongitude(), locationBody.getAddress(), localUrl);
                            sendMessage(locationSendMessage);
                            break;
                    }
                }
            }
        });
        tv_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void sendMessage(final HTMessage htMessage) {
        htMessage.setAttributes(extJSON.toJSONString());
        htMessage.setChatType(ChatType.singleChat);
        HTClient.getInstance().chatManager().sendMessage(htMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {
            }

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        htMessage.setStatus(HTMessage.Status.SUCCESS);
                        HTClient.getInstance().messageManager().saveMessage(htMessage,false);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_FORWORD).putExtra("message",htMessage));
                    }
                });
            }

            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        htMessage.setStatus(HTMessage.Status.FAIL);
                        HTClient.getInstance().messageManager().saveMessage(htMessage,false);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_FORWORD).putExtra("message",htMessage));
                    }
                });
            }
        });
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 3002:
                if (resultCode == RESULT_OK) {
                    ForwardSingleActivity.this.finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        friends.clear();
        getContacts();
        if (adAdapter!=null){
            adAdapter.notifyDataSetChanged();
        }
    }

    private HTMessage getCopyMessage(String msgId) {
        for (HTMessage htMessage : msgList) {
            if (htMessage.getMsgId().equals(msgId)) {
                return htMessage;
            }
        }
        return null;
    }

    public class PinyinComparator implements Comparator<User> {

        @SuppressLint("DefaultLocale")
        @Override
        public int compare(User o1, User o2) {
            String py1 = o1.getInitialLetter();
            String py2 = o2.getInitialLetter();
            if (py1.equals(py2)) {
                return o1.getNick().compareTo(o2.getNick());
            } else {
                if ("#".equals(py1)) {
                    return 1;
                } else if ("#".equals(py2)) {
                    return -1;
                }
                return py1.compareTo(py2);
            }
//            String py1 = o1.getInitialLetter();
//            String py2 = o2.getInitialLetter();
//            // 判断是否为空""
//            if (isEmpty(py1) && isEmpty(py2))
//                return 0;
//            if (isEmpty(py1))
//                return -1;
//            if (isEmpty(py2))
//                return 1;
//            String str1 = "";
//            String str2 = "";
//            try {
//                str1 = ((o1.getInitialLetter()).toUpperCase()).substring(0, 1);
//                str2 = ((o2.getInitialLetter()).toUpperCase()).substring(0, 1);
//            } catch (Exception e) {
//                System.out.println("某个str为\" \" 空");
//            }
//            return str1.compareTo(str2);
        }

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
    }

}
