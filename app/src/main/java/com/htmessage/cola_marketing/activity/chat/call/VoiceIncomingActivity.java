package com.htmessage.cola_marketing.activity.chat.call;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import org.anyrtc.meet_kit.RTMeetKit;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class VoiceIncomingActivity extends MeetingActivity {
    private ImageButton btn_audio;
    private ImageButton btn_speaker;
    private Button btn_answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_incoming);

        intMediaPlayer(R.raw.video_incoming, false);
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int action = intent.getIntExtra("action", 0);

    }

    private void initView() {

        ImageView ivAvatar = this.findViewById(R.id.iv_avatar);//头像
        TextView tvNick = this.findViewById(R.id.tv_nick);//昵称
        ImageView ivBackground = this.findViewById(R.id.iv_background);
        User user = ContactsManager.getInstance().getContactList().get(mUserId);
        if (user != null) {
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar);
            Glide.with(this).load(user.getAvatar()).apply(options).into(ivAvatar);
            tvNick.setText(user.getNick());
            options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().bitmapTransform(new BlurTransformation(this, 25));
            Glide.with(this).load(user.getAvatar()).apply(options).into(ivBackground);
        } else {
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar);
            Glide.with(this).load(R.drawable.default_avatar).apply(options).into(ivAvatar);
            tvNick.setText("");
            options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().bitmapTransform(new BlurTransformation(this, 25));
            Glide.with(this).load(R.color.black).apply(options).into(ivBackground);
        }
        btn_audio = this.findViewById(R.id.btn_audio);
        btn_speaker = this.findViewById(R.id.btn_speaker);
        btn_answer = this.findViewById(R.id.btn_answer);
        btn_audio.setVisibility(View.GONE);
        btn_answer.setVisibility(View.VISIBLE);
        btn_speaker.setVisibility(View.INVISIBLE);
        btn_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChating = true;
                if (isGroup) {
                    Intent intent = getIntent();
                    intent.setClass(VoiceIncomingActivity.this, GroupRtcActivity.class);
                    intent.putExtra("mode", RTMeetKit.RTCVideoLayout.RTC_V_3X3_auto.ordinal());
                    intent.putExtra("isOutgoing", false);
                    startActivity(intent);
                    finish();
                } else {
                    startRTC(true);
                    btn_answer.setVisibility(View.GONE);
                    btn_audio.setVisibility(View.VISIBLE);
                    btn_speaker.setVisibility(View.VISIBLE);
                    sendCallMesssage(3003, new HTChatManager.HTMessageCallBack() {
                        @Override
                        public void onProgress() {}

                        @Override
                        public void onSuccess() {
                            soundPool.release();
//                            releaseHandler.removeCallbacks(runnable);
                        }

                        @Override
                        public void onFailure() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(VoiceIncomingActivity.this, R.string.hung_in_failed,Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public void sendOverMessage() {
        super.sendOverMessage();
        if (!isGroup) {
            if (isChating) {
                sendCallMesssage(3005, null);
            } else {
                sendCallMesssage(3002, null);
            }
        }
    }

    @Override
    public void OnRtcJoinMeetOK(String strAnyrtcId) {
        super.OnRtcJoinMeetOK(strAnyrtcId);
        if (!isGroup) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv_note = (TextView) findViewById(R.id.tv_note);
                    tv_note.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    public void timeOver() {
        super.timeOver();
        if (!isGroup) {
            sendCallMesssage(3002, null);
        }
    }

}
