package com.htmessage.cola_marketing.activity.homepageFunc.tutor;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.HintSetView;


import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;

public class TutorEditActivity extends BaseActivity {
    HintSetView hsv_name,hsv_sign,hsv_tag,hsv_desc,hsv_birth,hsv_hobby;
    Long birthTime = 0L;

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

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
    }

    private void setView(JSONObject object) {
        Long birth = object.getLong("birthday");
        String like = object.getString("like");
        String tutor_name = object.getString("tutor_name");
        String tutor_mysign = object.getString("tutor_mysign");
        String sign = object.getString("tutor_sign");

        checkEmpty(hsv_name,tutor_name);
        checkEmpty(hsv_hobby,like);
        checkEmpty(hsv_sign,sign);
        checkEmpty(hsv_desc,tutor_mysign);
        hsv_birth.setEditText(format.format(new Date(birth)));
        birthTime = birth;

        showRightButton("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postTutorInform();
            }
        });
        hsv_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataPickerDialog(hsv_birth);
            }
        });
        hsv_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorEditActivity.this,TutorTagActivity.class));
            }
        });
    }

    private void checkEmpty(HintSetView hsv,String str) {
        if (str.isEmpty())
            hsv.setHintText(getString(R.string.please_input));
        else
            hsv.setEditText(str);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestTutorInform();
    }

    private void showDataPickerDialog(final HintSetView hintSetView) {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String strTime = year + "-" + (month+1) + "-" + dayOfMonth;
                hintSetView.setEditText(strTime);
                try {
                    birthTime = format.parse(strTime).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        },1990,1,1);
        dialog.show();
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
                        finish();
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
        params.add(new Param("tutor_name",hsv_name.getText()));
        params.add(new Param("tutor_mysign",hsv_desc.getText()));
        params.add(new Param("birthday",birthTime+""));
        params.add(new Param("like",hsv_hobby.getText()));
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
