package com.htmessage.cola_marketing.activity.homepageFunc.squad;

import android.support.v7.app.AppCompatActivity;
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
import com.htmessage.sdk.client.HTClient;

import java.util.ArrayList;
import java.util.List;

public class NewSquadActivity extends BaseActivity {
    HintSetView hsv_name,hsv_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tutor);
        setTitle("新建战略小组");
        //视觉效果类似，不再重复创建布局
        hsv_name = findViewById(R.id.hsv_new_tutor_name);
        hsv_money = findViewById(R.id.hsv_new_tutor_money);

        hsv_money.setVisibility(View.GONE);

        showRightButton("完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! hsv_name.getText().isEmpty())
                    postData();
                else
                    Toast.makeText(NewSquadActivity.this,R.string.inform_incomplete,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postData() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",HTApp.getInstance().getUsername()));
        params.add(new Param("type","1"));
        params.add(new Param("group_name",hsv_name.getText()));
        new OkHttpUtils(this).post(params, HTConstant.URL_CREATE_GROUP, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("createGroup",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        Toast.makeText(NewSquadActivity.this,"成功",Toast.LENGTH_SHORT).show();
                        HTClient.getInstance().groupManager().refreshGroupList();
                        finish();
                        break;
                    default:
                        Toast.makeText(NewSquadActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(NewSquadActivity.this,R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
