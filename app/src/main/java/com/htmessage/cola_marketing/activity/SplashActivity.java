package com.htmessage.cola_marketing.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.login.LoginActivity;
import com.htmessage.cola_marketing.activity.main.MainActivity;
import com.htmessage.cola_marketing.manager.LocalUserManager;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.sdk.client.HTClient;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView splash_root = findViewById(R.id.splash_root);
        AlphaAnimation animation = new AlphaAnimation(0.5f, 1.0f);
        animation.setDuration(1500);
        splash_root.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (HTClient.getInstance().isLogined()) {
                    //UpdateLocalLoginTimeUtils.sendLocalTimeToService(SplashActivity.this);
                    sendLocalTimeToService(SplashActivity.this);
                    LocalUserManager.getInstance().saveVersionDialog(false);
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    void sendLocalTimeToService(Context context){
        List<Param> params = new ArrayList<>();
        new OkHttpUtils(context).post(params, HTConstant.URL_SEND_LOCAL_LOGIN_TIME, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                switch (code){
                    case 1:
                        Log.d("sendLocalTimeToService","上传本地成功!");
                        break;
                    default:
                        Log.d("sendLocalTimeToService","上传本地失败!");
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.d("sendLocalTimeToService","上传本地失败!"+errorMsg);
            }
        });
    }
}
