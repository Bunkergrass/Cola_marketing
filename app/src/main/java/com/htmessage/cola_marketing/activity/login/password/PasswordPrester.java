package com.htmessage.cola_marketing.activity.login.password;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.login.LoginActivity;
import com.htmessage.cola_marketing.manager.LocalUserManager;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.utils.SendCodeUtils;
import com.htmessage.sdk.client.HTClient;

import java.util.ArrayList;
import java.util.List;

public class PasswordPrester implements PasswordBasePrester {
    private String TAG = PasswordPrester.class.getSimpleName();
    private PasswordView passwordView;

    private ProgressBar bar;

    public PasswordPrester(PasswordView passwordView) {
        this.passwordView = passwordView;
        this.passwordView.setPresenter(this);
    }

    @Override
    public void sendSMSCode(String mobile, String countryName, String countryCode) {
        if (TextUtils.isEmpty(mobile)) {
            Toast.makeText(passwordView.getBaseContext(), R.string.mobile_not_be_null,Toast.LENGTH_SHORT).show();
            return;
        }
//        if (countryName.equals(passwordView.getBaseContext().getString(R.string.china)) && countryCode.equals(passwordView.getBaseContext().getString(R.string.country_code))) {
//            if (!Validator.isMobile86(mobile)) {
//                CommonUtils.showToastShort(passwordView.getBaseContext(), R.string.please_input_true_mobile);
//                return;
//            }
//        }
        bar = new ProgressBar(passwordView.getBaseActivity());
        bar.setVisibility(View.VISIBLE);
        passwordView.getBaseActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//        CommonUtils.showDialogNumal(passwordView.getBaseActivity(), passwordView.getBaseActivity().getString(R.string.Is_sending_a_request));
        passwordView.startTimeDown();
//        if (mobile.length() == 11 || mobile.length() == 13 && passwordView.getBaseActivity().getString(R.string.country_code).equals(countryCode)) {
//            sendCode(mobile);
//        } else {
            sendCodeByClund(mobile,countryCode);
//        }
    }
    private void sendCode(String mobile) {
        new SendCodeUtils(passwordView.getBaseActivity()).sendCode(mobile, new SendCodeUtils.SmsCodeListener() {
            @Override
            public void onSuccess(String recCode, String recMsg, final String smsCode) {
                passwordView.onSendSMSCodeSuccess(smsCode);
                bar.setVisibility(View.GONE);
                passwordView.getBaseActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onFailure(String error) {
                bar.setVisibility(View.GONE);
                passwordView.getBaseActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                passwordView.finishTimeDown();
            }
        });
    }

    private void sendCodeByClund(String mobile, String countryCode) {
        new SendCodeUtils(passwordView.getBaseActivity()).sendCode(mobile,new SendCodeUtils.SmsCodeListener() {
            @Override
            public void onSuccess(String recCode, String recMsg, String smsCode) {
                passwordView.onSendSMSCodeSuccess(smsCode);
                bar.setVisibility(View.GONE);
                passwordView.getBaseActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onFailure(String error) {
                bar.setVisibility(View.GONE);
                passwordView.getBaseActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                passwordView.finishTimeDown();
            }
        });
    }
    @Override
    public void resetPassword(String cacheCode, String smsCode, String password, String confimPwd, String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            Toast.makeText(passwordView.getBaseContext(), R.string.mobile_not_be_null,Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(smsCode) || TextUtils.isEmpty(cacheCode)) {
            Toast.makeText(passwordView.getBaseContext(), R.string.please_input_code,Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confimPwd)) {
            Toast.makeText(passwordView.getBaseContext(), R.string.new_password_cannot_be_empty,Toast.LENGTH_SHORT).show();
            return;
        }
        if (!cacheCode.equals(smsCode)) {
            Toast.makeText(passwordView.getBaseContext(), R.string.code_is_wrong,Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confimPwd)) {
            Toast.makeText(passwordView.getBaseContext(), R.string.Two_input_password,Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(passwordView.getBaseContext());
        progressDialog.setMessage(passwordView.getBaseContext().getString(R.string.are_reset_password));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        List<Param> params = new ArrayList<Param>();
        params.add(new Param("newPassword", password));
        params.add(new Param("tel", mobile));
        new OkHttpUtils(passwordView.getBaseContext()).post(params, HTConstant.URL_RESETPASSWORD, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        passwordView.clearCacheCode();
                        passwordView.onResetSuccess(passwordView.getBaseContext().getString(R.string.password_reset_success));
                        logOut(passwordView.getIsReset());
                        break;
                    default:
                        passwordView.onResetFailed(passwordView.getBaseContext().getString(R.string.password_reset_failed));
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                progressDialog.dismiss();
                passwordView.onResetFailed(passwordView.getBaseContext().getString(R.string.password_reset_failed));
            }
        });
    }

    private void logOut(boolean isReset) {
        if (isReset) {
            HTClient.getInstance().logout(new HTClient.HTCallBack() {
                @Override
                public void onSuccess() {
                    passwordView.getBaseActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            // show login scree
                            HTApp.getInstance().setUserJson(null);
                            HTApp.getInstance().finishActivities();
//                            HTApp.getInstance().clearThirdToken();
                            LocalUserManager.getInstance().saveVersionDialog(false);
//                            CusActivityManager.getInstance().finishAllActivity();
                            passwordView.getBaseActivity().startActivity(new Intent(passwordView.getBaseActivity(), LoginActivity.class));
                            passwordView.getBaseActivity().finish();
                        }
                    });
                }

                @Override
                public void onError() {
                    passwordView.getBaseActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            passwordView.onLogOutFailed(passwordView.getBaseContext().getString(R.string.logout_failed));
                        }
                    });
                }
            });
        } else {
            passwordView.getBaseActivity().finish();
        }
    }

    @Override
    public void start() {

    }
}
