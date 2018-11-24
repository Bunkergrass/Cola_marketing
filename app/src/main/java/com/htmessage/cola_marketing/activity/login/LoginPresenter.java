package com.htmessage.cola_marketing.activity.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.main.MainActivity;
import com.htmessage.cola_marketing.domain.User;
import com.htmessage.cola_marketing.manager.ContactsManager;
import com.htmessage.cola_marketing.manager.PreferenceManager;
import com.htmessage.cola_marketing.utils.CommonUtils;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.sdk.client.HTClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View loginView;

    public LoginPresenter(LoginContract.View loginView){
        this.loginView=loginView;
        this.loginView.setPresenter(this);
    }

    @Override
    public void requestServer(String username, String password, final boolean isAuth) {
        loginView.showDialog();
        List<Param> params = new ArrayList<>();
        params.add(new Param("usertel", username));
        params.add(new Param("password", password));
        new OkHttpUtils(loginView.getBaseActivity() ).post(params, HTConstant.URL_LOGIN, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getInteger("code");
                switch(code){
                    case 1:
                        JSONObject userJson = jsonObject.getJSONObject("user");
                        loginIm(userJson,isAuth);
                        break;
                    case -1:
                        loginView.cancelDialog();
                        loginView.showToast(R.string.Account_does_not_exist);
                        break;
                    case -2:
                        loginView.cancelDialog();
                        loginView.showToast(R.string.Incorrect_password);
                        break;
                    case -3:
                        loginView.cancelDialog();
                        loginView.showToast(R.string.Account_has_been_disabled);
                        break;
                    default:
                        loginView.cancelDialog();
                        loginView.showToast(R.string.Server_busy);
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                loginView.cancelDialog();
            }
        });
    }

    @Override
    public void chooseCuntry(Context context, TextView tvCountryName, TextView tvCountryCode) {
        //CityCodeAndTimePickUtils.showPup(context,tvCountryName,tvCountryCode);
    }

    private void loginIm(final JSONObject userJson, final boolean isAuth) {
        String userId=userJson.getString(HTConstant.JSON_KEY_HXID);
        String password= userJson.getString(HTConstant.JSON_KEY_PASSWORD);
        if(TextUtils.isEmpty(password)){
            password=userJson.getString("password");
        }

        HTClient.getInstance().login(userId,password, new HTClient.HTCallBack() {
            @Override
            public void onSuccess() {
                 if (userJson == null) {
                    return;
                }
                Log.d("Login in HTClient","is success");
                JSONArray friends = userJson.getJSONArray("friend");
                if (userJson.containsKey("friend")) {
                    userJson.remove("friend");
                }
                HTApp.getInstance().setUserJson(userJson);
                Map<String, User> userlist = new HashMap<String, User>();
                if (friends != null) {
                    for (int i = 0; i < friends.size(); i++) {
                        JSONObject friend = friends.getJSONObject(i);
                        User user = CommonUtils.Json2User(friend);
                        userlist.put(user.getUsername(), user);
                    }
                    List<User> users = new ArrayList<User>(userlist.values());
                    ContactsManager.getInstance().saveContactList(users);
                }
                //上传最近登录时间
                //UpdateLocalLoginTimeUtils.sendLocalTimeToService(loginView.getBaseContext());
                updateLocalLoginTime(loginView.getBaseContext());
                loginView.getBaseActivity(). runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginView.cancelDialog();
                        if (isAuth){
                            loginView.getBaseActivity().setResult(Activity.RESULT_OK);
                            loginView.getBaseActivity().finish();
                        }else{
                            loginView.showToast(R.string.login_success);
                            Intent intent = new Intent(loginView.getBaseContext(), MainActivity.class);
                            loginView.getBaseActivity().startActivity(intent);
                            loginView.getBaseActivity().finish();
                        }
                    }
                });
            }

            @Override
            public void onError() {
                loginView.getBaseActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginView.cancelDialog();
                        loginView.showToast(R.string.Login_failed);
                    }
                });
            }
        });

    }

    @Override
    public void start() {
       CommonUtils.observeSoftKeyboard(loginView.getBaseActivity(), new CommonUtils.OnSoftKeyboardChangeListener() {
           @Override
           public void onSoftKeyBoardChange(int softKeybardHeight, boolean visible, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
               if(visible&&softKeybardHeight>0){
                   PreferenceManager.getInstance().setSoftKeybardHeight(softKeybardHeight);
                   loginView.getBaseActivity().getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
               }
           }
       });
    }

    public void updateLocalLoginTime(Context context) {
        List<Param> params = new ArrayList<>();
        new OkHttpUtils(context).post(params, HTConstant.URL_SEND_LOCAL_LOGIN_TIME, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                switch (code){
                    case 1:
                        Log.d("updateLocalLoginTime","上传本地成功!");
                        break;
                    default:
                        Log.d("updateLocalLoginTime","上传本地失败!");
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.d("updateLocalLoginTime","上传本地失败!"+errorMsg);
            }
        });
    }

}
