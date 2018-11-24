package com.htmessage.cola_marketing.activity.chat.call;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.domain.User;
import com.htmessage.cola_marketing.manager.ContactsManager;
import com.htmessage.sdk.manager.HTChatManager;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class VoiceOutgoingActivity extends MeetingActivity {
    private ImageButton btn_audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_outgoing);

        intMediaPlayer(R.raw.video_request,true);
        initView();
        startRTC(true);
    }

    private void initView() {
        ImageView ivAvatar = this.findViewById(R.id.iv_avatar);//头像
        TextView tvNick = this.findViewById(R.id.tv_nick);//昵称
        ImageView ivBackground = this.findViewById(R.id.iv_background);

        btn_audio = this.findViewById(R.id.btn_audio);
        btn_audio.setEnabled(false);
        User user = ContactsManager.getInstance().getContactList().get(mUserId);
        if (user != null) {
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar);
            Glide.with(this).load(user.getAvatar()).apply(options).into(ivAvatar);
            tvNick.setText(user.getNick());
            options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().bitmapTransform(new BlurTransformation(this,25));
            Glide.with(this).load(user.getAvatar()).apply(options).into(ivBackground);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int action = intent.getIntExtra("action", 0);
        if (action == 3003) {
            releaseHandler.removeCallbacks(runnable);
            btn_audio.setEnabled(true);
        }
        TextView tv_note= this.findViewById(R.id.tv_note);
        tv_note.setVisibility(View.INVISIBLE);
    }

    @Override
    public void OnRtcJoinMeetOK(String strAnyrtcId) {
        super.OnRtcJoinMeetOK(strAnyrtcId);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendCallMesssage(3000, new HTChatManager.HTMessageCallBack() {
                    @Override
                    public void onProgress() {}

                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                VoiceOutgoingActivity.super.setRes("失败");
                                Toast.makeText(VoiceOutgoingActivity.this,R.string.call_failed,Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void sendOverMessage() {
        super.sendOverMessage();
        if(isChating){
            sendCallMesssage(3005,null);
        }else{
            sendCallMesssage(3001,null);
        }
    }

    @Override
    public void timeOver() {
        super.timeOver();
        sendCallMesssage(3002,null);
    }

}
