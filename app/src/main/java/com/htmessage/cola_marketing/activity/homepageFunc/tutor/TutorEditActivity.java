package com.htmessage.cola_marketing.activity.homepageFunc.tutor;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.HintSetView;


import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;

public class TutorEditActivity extends BaseActivity {
    HintSetView hsv_name,hsv_sign,hsv_tag,hsv_desc,hsv_birth,hsv_hobby;
    private static final String dateFormat = "yyyy-MM-dd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_edit);
        setTitle("编辑导师资料");

        hsv_name = findViewById(R.id.hsv_tutor_edit_name);
        hsv_sign = findViewById(R.id.hsv_tutor_edit_sign);
        hsv_tag = findViewById(R.id.hsv_tutor_edit_tag);
        hsv_desc = findViewById(R.id.hsv_tutor_edit_desc);
        hsv_birth = findViewById(R.id.hsv_tutor_edit_birth);
        hsv_hobby = findViewById(R.id.hsv_tutor_edit_hobby);

        showRightButton("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postTutorInform();
            }
        });
    }

    private void setView(JSONObject object) {
        Long birth = object.getLong("birthday");
        String like = object.getString("like");
        String tutor_name = object.getString("tutor_name");
        String tutor_mysign = object.getString("tutor_mysign");
        String sign = object.getString("tutor_sign");

        hsv_name.setHintText(tutor_name);
        hsv_hobby.setHintText(like);
        hsv_sign.setHintText(sign);
        hsv_desc.setHintText(tutor_mysign);
        hsv_birth.setHintText(new SimpleDateFormat(dateFormat).format(new Date(birth)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestTutorInform();
    }

    private void requestTutorInform() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",HTApp.getInstance().getUsername()));
        new OkHttpUtils(this).post(params, HTConstant.URL_TUTOR_DETAIL, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("requestTutorInform",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        setView(jsonObject.getJSONObject("data"));
                        break;
                    default:
                        Toast.makeText(TutorEditActivity.this,"您尚未成为导师",Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                requestTutorInform();
            }
        });
    }

    private void postTutorInform() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",HTApp.getInstance().getUsername()));
        params.add(new Param("tutor_name",""));
        params.add(new Param("tutor_mysign",""));
        params.add(new Param("birthday",""));
        params.add(new Param("like",""));
        new OkHttpUtils(this).post(params, HTConstant.URL_UP_TUTOR, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        Toast.makeText(TutorEditActivity.this,R.string.update_success,Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    default:
                        Toast.makeText(TutorEditActivity.this,R.string.failed_please_retry,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(TutorEditActivity.this,R.string.failed_please_retry,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
