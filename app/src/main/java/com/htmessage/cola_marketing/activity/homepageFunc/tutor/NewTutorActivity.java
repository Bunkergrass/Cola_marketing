package com.htmessage.cola_marketing.activity.homepageFunc.tutor;

import android.os.Bundle;
import android.text.InputType;
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
import com.htmessage.sdk.client.HTClient;

import java.util.ArrayList;
import java.util.List;

public class NewTutorActivity extends BaseActivity {
    HintSetView hsv_name,hsv_money,hsv_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tutor);
        setTitle("新建导师组");

        hsv_name = findViewById(R.id.hsv_new_tutor_name);
        hsv_money = findViewById(R.id.hsv_new_tutor_money);
        hsv_desc = findViewById(R.id.hsv_new_tutor_desc);
        hsv_money.et_hint_text.setInputType(InputType.TYPE_CLASS_NUMBER);

        showRightButton("完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmpty())
                    postData();
                else
                    Toast.makeText(NewTutorActivity.this,R.string.inform_incomplete,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkEmpty() {
        return !(hsv_money.getText().isEmpty()
                && hsv_name.getText().isEmpty());
    }

    private void postData() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",HTApp.getInstance().getUsername()));
        params.add(new Param("type","2"));
        params.add(new Param("group_name",hsv_name.getText()));
        params.add(new Param("group_money",hsv_money.getText()));
        new OkHttpUtils(this).post(params, HTConstant.URL_CREATE_GROUP, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("createGroup",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        Toast.makeText(NewTutorActivity.this,"成功",Toast.LENGTH_SHORT).show();
                        HTClient.getInstance().groupManager().refreshGroupList();
                        finish();
                        break;
                    default:
                        Toast.makeText(NewTutorActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(NewTutorActivity.this,R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
