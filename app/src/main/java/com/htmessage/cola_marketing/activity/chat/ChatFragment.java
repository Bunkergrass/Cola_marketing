package com.htmessage.cola_marketing.activity.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.IMAction;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.chat.widget.ChatInputView;
import com.htmessage.cola_marketing.utils.ACache;
import com.htmessage.cola_marketing.utils.CommonUtils;
import com.htmessage.cola_marketing.utils.Emoji.Emojicon;
import com.htmessage.cola_marketing.widget.HTAlertDialog;
import com.htmessage.cola_marketing.widget.VoiceRecorderView;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.utils.MessageUtils;
import com.jrmf360.rplib.JrmfRpClient;
import com.jrmf360.rplib.bean.GrabRpBean;
import com.jrmf360.rplib.bean.TransAccountBean;
import com.jrmf360.rplib.utils.callback.GrabRpCallBack;
import com.jrmf360.rplib.utils.callback.TransAccountCallBack;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements ChatContract.View, SwipeRefreshLayout.OnRefreshListener {
    private ListView lv_chatlist;
    private ChatInputView civ_chatinput;
    private SwipeRefreshLayout sl_refresh_chat;
    private VoiceRecorderView voiceRecorderView;
    private int chatType;
    private String toChatUsername;
    private MyBroadcastReciver myBroadcastReciver;

    private ChatAdapter adapter;
    private ChatContract.Presenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        lv_chatlist = view.findViewById(R.id.lv_chatlist);
        civ_chatinput = view.findViewById(R.id.civ_chatinput);
        sl_refresh_chat = view.findViewById(R.id.sl_refresh_chat);

        voiceRecorderView = view.findViewById(R.id.voice_recorder);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle fragmentArgs = getArguments();
        chatType = fragmentArgs.getInt("chatType", MessageUtils.CHAT_SINGLE);
        toChatUsername = fragmentArgs.getString("userId");

        setView();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMAction.ACTION_MESSAGE_WITHDROW);
        intentFilter.addAction(IMAction.ACTION_MESSAGE_FORWORD);
        intentFilter.addAction(IMAction.ACTION_NEW_MESSAGE);
        intentFilter.addAction(IMAction.ACTION_MESSAGE_EMPTY);
        intentFilter.addAction(IMAction.CMD_DELETE_FRIEND);
        myBroadcastReciver = new MyBroadcastReciver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myBroadcastReciver,intentFilter);
    }

    private static int[] itemNamesSingle = {R.string.attach_take_pic, R.string.attach_picture, R.string.attach_location, R.string.attach_video, R.string.attach_video_call, R.string.attach_file, R.string.attach_red, R.string.attach_transfer};
    private static int[] itemIconsSingle = {R.drawable.chat_takepic_selector, R.drawable.chat_image_selector, R.drawable.chat_location_selector, R.drawable.chat_video_selector, R.drawable.chat_video_call_selector, R.drawable.chat_file_selector, R.drawable.type_redpacket, R.drawable.type_transfer};
    private static int[] itemNamesGroup = {R.string.attach_take_pic, R.string.attach_picture, R.string.attach_location, R.string.attach_video, R.string.attach_video_call, R.string.attach_file, R.string.attach_red };
    private static int[] itemIconsGroup = {R.drawable.chat_takepic_selector, R.drawable.chat_image_selector, R.drawable.chat_location_selector, R.drawable.chat_video_selector, R.drawable.chat_video_call_selector, R.drawable.chat_file_selector, R.drawable.type_redpacket };

    private void setView(){
        sl_refresh_chat.setOnRefreshListener(this);

        if(chatType== MessageUtils.CHAT_SINGLE){
            civ_chatinput.initView(getActivity(), sl_refresh_chat,itemNamesSingle,itemIconsSingle);
        }else {
            civ_chatinput.initView(getActivity(), sl_refresh_chat,itemNamesGroup,itemIconsGroup);
        }
        civ_chatinput.setInputViewLisenter(new MyInputViewLisenter());

        adapter = new ChatAdapter(presenter.getMessageList(), getActivity(), toChatUsername, chatType);
        lv_chatlist.setAdapter(adapter);
        lv_chatlist.setSelection(lv_chatlist.getCount()-1);
        lv_chatlist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            civ_chatinput.hideSoftInput();
            civ_chatinput.interceptBackPress();
            return false;
            }
        });
        lv_chatlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            HTMessage htMessage = adapter.getItem(i);
            if (htMessage != null) {
                if (htMessage.getType() == HTMessage.Type.TEXT) {
                    int action = htMessage.getIntAttribute("action", 0);
                    if (action == 10001 || action == 10002) {
                        return false;
                    }
                }
                showMsgDialog(htMessage, i);
            }
            return true;
            }
        });
        adapter.setOnResendViewClick(new ChatAdapter.OnResendViewClick() {
            @Override
            public void resendMessage(HTMessage htMessage) {
                showReSendDialog(htMessage);
            }

            @Override
            public void onRedMessageClicked(JSONObject jsonObject, String evnId) {
                OpenRedMessage(jsonObject,evnId);
            }

            @Override
            public void onTransferMessageClicked(final JSONObject jsonObject, String transferId) {
                JrmfRpClient.openTransDetail(getActivity(), HTApp.getInstance().getUsername(), HTApp.getInstance().getThirdToken(), transferId, new TransAccountCallBack() {
                    @Override
                    public void transResult(TransAccountBean transAccountBean) {
                        String status = transAccountBean.getTransferStatus();
                        if ("1".equals(status)) {
                            presenter.sendTransferCmdMessage(jsonObject);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        if (!civ_chatinput.interceptBackPress()) {
            getActivity().finish();
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.loadMoreMessages();
            }
        }, 500);
        sl_refresh_chat.setRefreshing(false);
    }

    private void showMsgDialog(final HTMessage message, final int postion) {
        HTAlertDialog HTAlertDialog = new HTAlertDialog(getActivity(), null
                , new String[]{getActivity().getString(R.string.delete), getActivity().getString(R.string.copy), getActivity().getString(R.string.forward)});
        if (message.getDirect() == HTMessage.Direct.SEND) {
            HTAlertDialog = new HTAlertDialog(getActivity(), null, new String[]{getActivity().getString(R.string.delete), getActivity().getString(R.string.copy), getActivity().getString(R.string.forward), getActivity().getString(R.string.reback)});
        }
        HTAlertDialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                if (position == 0) { //删除
                    presenter.deleteMessage(message);
                } else if (position == 1) { //复制
                    presenter.copyMessage(message);
                } else if (position == 2) {//转发
                    presenter.forwordMessage(message);
                } else if (position == 3) {//撤回
                    presenter.withdrowMessage(message, postion);
                }
            }
        });
    }

    /**
     * 重新发送消息
     * @param htMessage
     */
    private void showReSendDialog(final HTMessage htMessage) {
        AlertDialog.Builder buidler = new AlertDialog.Builder(getContext());
        View view = View.inflate(getContext(),R.layout.dialog_alert,null);
        TextView tv_forward_title = view.findViewById(R.id.tv_alert_title);
        TextView tv_forward_content = view.findViewById(R.id.tv_alert_content);
        ImageView iv_foward = view.findViewById(R.id.iv_alert);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);
        iv_foward.setVisibility(View.INVISIBLE);
        tv_forward_title.setText(R.string.prompt);
        tv_forward_content.setText(R.string.resend_text);
        buidler.setView(view);
        final AlertDialog alertDialog = buidler.show();
        tv_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                presenter.resendMessage(htMessage);
            }
        });
        tv_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void showNoMoreMessage() {
        Toast.makeText(getActivity(),R.string.not_more_msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refreshListView() {
        adapter.notifyDataSetChanged();
        if (lv_chatlist.getCount() > 0) {
            lv_chatlist.setSelection(lv_chatlist.getCount() - 1);
        }
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void setPresenter(ChatContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Context getBaseContext() {
        return getContext();
    }

    @Override
    public Activity getBaseActivity() {
        return getActivity();
    }

    private class MyBroadcastReciver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(IMAction.ACTION_MESSAGE_WITHDROW)) {
                String msgId = intent.getStringExtra("msgId");
                presenter.onMessageWithdrow(msgId);
            } else if (intent.getAction().equals(IMAction.ACTION_MESSAGE_FORWORD)) {
                HTMessage message = intent.getParcelableExtra("message");
                presenter.onMeesageForward(message);
            } else if (intent.getAction().equals(IMAction.ACTION_NEW_MESSAGE)) {
                HTMessage message = intent.getParcelableExtra("message");
                presenter.onNewMessage(message);
            } else if (IMAction.ACTION_MESSAGE_EMPTY.equals(intent.getAction())) {
                String id = intent.getStringExtra("id");
                if (toChatUsername.equals(id)) {
                    presenter.onMessageClear();
                }
            } else if (IMAction.CMD_DELETE_FRIEND.equals(intent.getAction())) {
                String userId = intent.getStringExtra(HTConstant.JSON_KEY_HXID);
                if (getActivity() != null) {
                    if (userId.equals(toChatUsername)) {
                        Toast.makeText(getActivity(), getString(R.string.just_delete_friend),Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                }
            }
        }
    }

    private class MyInputViewLisenter implements ChatInputView.InputViewLisenter {
        @Override
        public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
            return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new VoiceRecorderView.EaseVoiceRecorderCallback() {
                @Override
                public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                    presenter.sendVoiceMessage(voiceFilePath, voiceTimeLength);
                }
            });
        }

        @Override
        public void onBigExpressionClicked(Emojicon emojicon) {}

        @Override
        public void onSendButtonClicked(String content) {
            presenter.sendTextMessage(content);
        }

        @Override
        public boolean onEditTextLongClick() {
            String myCopy = ACache.get(getActivity()).getAsString("myCopy");
            if (!TextUtils.isEmpty(myCopy)) {
                JSONObject jsonObject = JSONObject.parseObject(myCopy);
                String msgId = jsonObject.getString("msgId");
                String imagePath = jsonObject.getString("imagePath");
                HTMessage emMessage = presenter.getMessageById(msgId);
                if (emMessage == null) {
                    return true;
                }
                showCopyContent(jsonObject.getString("copyType"), jsonObject.getString("localPath"), emMessage, imagePath);
                return true;
            }
            return false;
        }

        @Override
        public void onEditTextUp() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    lv_chatlist.smoothScrollToPosition(lv_chatlist.getCount() - 1);
                }
            }, 500);
        }

        @Override
        public void onAlbumItemClicked() {
            presenter.selectPicFromLocal();
        }

        @Override
        public void onPhotoItemClicked() {
            presenter.selectPicFromCamera();
        }

        @Override
        public void onLocationItemClicked() {
            presenter.selectLocation();
        }

        @Override
        public void onVideoItemClicked() {
            presenter.selectVideo();
        }

        @Override
        public void onCallItemClicked() {
            presenter.selectCall();
        }

        @Override
        public void onFileItemClicked() {
            presenter.selectFile();
        }

        @Override
        public void onRedPackageItemClicked() {
            //TODO 发红包
            presenter.sendRedPackage();
        }

        @Override
        public void onTransferItemClicked() {
            //TODO 转账
            presenter.sendTransferMessage();
        }
    }

    /**
     * 复制
     *
     * @param copyType
     * @param localPath
     * @param message1
     * @param imagePath
     */
    public void showCopyContent(final String copyType, final String localPath, final HTMessage message1, final String imagePath) {
        AlertDialog.Builder buidler = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.dialog_alert, null);
        TextView tv_forward_title = view.findViewById(R.id.tv_alert_title);
        TextView tv_forward_content = view.findViewById(R.id.tv_alert_content);
        TextView tv_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_cancel = view.findViewById(R.id.tv_dialog_cancel);
        final ImageView iv_foward = view.findViewById(R.id.iv_alert);
        iv_foward.setVisibility(View.GONE);
        tv_forward_title.setText(R.string.copy);
        tv_forward_content.setText(R.string.really_copy_and_send);
        buidler.setView(view);
        if ("image".equals(copyType) && imagePath != null) {
            iv_foward.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(imagePath).into(iv_foward);
        }
        final AlertDialog dialog = buidler.show();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                presenter.sendCopyMessage(copyType, localPath, message1, imagePath);

            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 打开红包
     * @param jsonObject
     * @param envId
     */
    private void OpenRedMessage(final JSONObject jsonObject, String envId){
        if (chatType == MessageUtils.CHAT_GROUP) {
            JrmfRpClient.openGroupRp(getActivity(), HTApp.getInstance().getUsername()
                    , HTApp.getInstance().getThirdToken(), HTApp.getInstance().getUsername()
                    , HTApp.getInstance().getUserAvatar(), envId, new GrabRpCallBack() {
                @Override
                public void grabRpResult(GrabRpBean grabRpBean) {
                    if (grabRpBean.isHadGrabRp()){
                        presenter.sendRedCmdMessage(jsonObject);
                    }
                }
            });
        } else {
            JrmfRpClient.openSingleRp(getActivity(), HTApp.getInstance().getUsername()
                    , HTApp.getInstance().getThirdToken(), HTApp.getInstance().getUsername()
                    , HTApp.getInstance().getUserAvatar(), envId, new GrabRpCallBack() {
                @Override
                public void grabRpResult(GrabRpBean grabRpBean) {
                    if (grabRpBean.isHadGrabRp()){
                        presenter.sendRedCmdMessage(jsonObject);
                    }
                }
            });
        }
    }


}
