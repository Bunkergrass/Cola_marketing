package com.htmessage.cola_marketing.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTConstant;

import java.util.ArrayList;
import java.util.List;

public class SendCodeUtils {
    private String TAG = SendCodeUtils.class.getSimpleName();
    private Context context;

    public SendCodeUtils(Context context) {
        this.context = context;
    }

    public interface SmsCodeListener {
        void onSuccess(String recCode, String recMsg, String smsCode);
        void onFailure(String error);
    }

    /**
     * 通过云片网发送短信
     *
     * @param mobile
     * @param listener
     */
    public void sendCode(String mobile, final SmsCodeListener listener) {
        final int smsCode = (int) (Math.random() * 900000 + 100000);
        List<Param> params = new ArrayList<>();
        params.add(new Param("mobile",  mobile));
        params.add(new Param("code", String.valueOf(smsCode)));
        new OkHttpUtils(context).post(params, HTConstant.SEND_YUNPIAN_CODE, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d(TAG,"----验证码返回："+jsonObject.toJSONString());
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        listener.onSuccess(String.valueOf(code), String.valueOf(code), String.valueOf(smsCode));
                        break;
                    default:
                        listener.onFailure(String.valueOf(code));
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                listener.onFailure(errorMsg);
            }
        });
    }

    public void sendSmsCode(String mobile, String countryCode, final SmsCodeListener listener) {
        final int smsCode = (int) (Math.random() * 900000 + 100000);
        List<Param> params = new ArrayList<>();
        params.add(new Param("mobile",  mobile));
        params.add(new Param("code", String.valueOf(smsCode)));
        params.add(new Param("country", countryCode));
        new OkHttpUtils(context).post(params, HTConstant.SEND_YUNPIAN_CODE, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d(TAG,"----验证码返回："+jsonObject.toJSONString());
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        Log.d(TAG, "验证码:" + smsCode);
                        listener.onSuccess(String.valueOf(code), String.valueOf(code), String.valueOf(smsCode));
                        break;
                    default:
                        listener.onFailure(String.valueOf(code));
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                listener.onFailure(errorMsg);
            }
        });
    }

}
