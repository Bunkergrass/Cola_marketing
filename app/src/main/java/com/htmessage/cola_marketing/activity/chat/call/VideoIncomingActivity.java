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

import jp.wasabeef.glide.transformations.BlurTransformation;

public class VideoIncomingActivity extends MeetingActivity {
    private Button btn_answer;
    private ImageButton btn_audio, btn_swtich_voice, btn_swtich_camera, btn_speaker;
    private ImageView ivBackground, ivAvatar, ivAvatar1;
    private TextView tvNick, tvNick1;
    private TextView tv_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_incoming);
        intMediaPlayer(R.raw.video_incoming,false);
        initView();
    }

    private void initView() {
        ivAvatar = this.findViewById(R.id.iv_avatar);//头像
        tvNick = this.findViewById(R.id.tv_nick);//昵称
        ivAvatar1 = this.findViewById(R.id.iv_avatar1);//头像
        tvNick1 = this.findViewById(R.id.tv_nick1);//昵称
        ivBackground = this.findViewById(R.id.iv_background);
        tv_note = this.findViewById(R.id.tv_note);
        tv_note.setText(R.string.video_call);

        User user = ContactsManager.getInstance().getContactList().get(mUserId);
        if (user != null) {
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar);
            Glide.with(this).load(user.getAvatar()).apply(options).into(ivAvatar);
            tvNick.setText(user.getNick());
            Glide.with(this).load(user.getAvatar()).apply(options).into(ivAvatar1);
            tvNick1.setText(user.getNick());
            options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().bitmapTransform(new BlurTransformation(this, 25));
            Glide.with(this).load(user.getAvatar()).apply(options)
                    //.bitmapTransform(new BlurTransformation(this, 25), new CenterCrop(this))
                    .into(ivBackground);
        }
        btn_audio = this.findViewById(R.id.btn_audio);
        btn_speaker = this.findViewById(R.id.btn_speaker);
        btn_answer = this.findViewById(R.id.btn_answer);
        btn_swtich_voice = this.findViewById(R.id.btn_swtich_voice);
        btn_swtich_camera = this.findViewById(R.id.btn_swtich_camera);
        btn_audio.setVisibility(View.GONE);
        btn_swtich_camera.setVisibility(View.GONE);
        btn_swtich_voice.setVisibility(View.GONE);
        btn_answer.setVisibility(View.VISIBLE);
        btn_speaker.setVisibility(View.INVISIBLE);
        btn_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChating = true;
                startRTC(false);
                tv_note.setVisibility(View.INVISIBLE);
                ivAvatar.setVisibility(View.GONE);
                ivBackground.setVisibility(View.GONE);
                tvNick.setVisibility(View.GONE);
                btn_answer.setVisibility(View.GONE);
                btn_swtich_voice.setVisibility(View.VISIBLE);
                btn_swtich_camera.setVisibility(View.VISIBLE);
                btn_speaker.setVisibility(View.GONE);
                sendCallMesssage(4003, new HTChatManager.HTMessageCallBack() {
                    @Override
                    public void onProgress() {}

                    @Override
                    public void onSuccess() {
                        soundPool.release();
                    }

                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(VideoIncomingActivity.this, R.string.hung_in_failed, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }
        });

        btn_swtich_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCallMesssage(4004, null);
                videoTovoice();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //    int action = intent.getIntExtra("action", 0);
    }

    @Override
    public void videoTovoice() {
        btn_audio.setVisibility(View.VISIBLE);
        btn_speaker.setVisibility(View.VISIBLE);
        btn_swtich_camera.setVisibility(View.GONE);
        btn_swtich_voice.setVisibility(View.GONE);
        ivAvatar1.setVisibility(View.VISIBLE);
        tvNick1.setVisibility(View.VISIBLE);
        mVideoView.disableCamera();
        findViewById(R.id.rl_rtc_videos).setVisibility(View.INVISIBLE);
    }

    @Override
    public void sendOverMessage() {
        super.sendOverMessage();
        if (isChating) {
            sendCallMesssage(3005, null);
        } else {
            sendCallMesssage(4002, null);
        }

    }

    @Override
    public void OnRtcJoinMeetOK(String strAnyrtcId) {
        super.OnRtcJoinMeetOK(strAnyrtcId);
    }

    @Override
    public void timeOver() {
        super.timeOver();
        sendCallMesssage(4002, null);
    }
}
