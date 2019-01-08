package com.htmessage.cola_marketing.activity.homepageFunc.tiyan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.main.my.region.RegionActivity;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.HintSetView;

import java.util.ArrayList;
import java.util.List;

public class AwardActivity extends BaseActivity {
    private static final int UPDATE_REGION = 7;

    private HintSetView hsv_name,hsv_city,hsv_addr;
    private Button btn_award;
    private EditText et_tel;
    private ImageView iv_awards;
    private TextView tv_awards,tv_desc;

    private String addr;
    private String taskId,only_good_name,only_good_text,only_good_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award);
        setTitle("填写信息");

        taskId = getIntent().getStringExtra("taskId");
        only_good_name = getIntent().getStringExtra("only_good_name");
        only_good_text = getIntent().getStringExtra("only_good_text");
        only_good_pic = getIntent().getStringExtra("only_good_pic");

        initView();
    }

    private void initView() {
        hsv_city = findViewById(R.id.hsv_award_city);
        hsv_addr = findViewById(R.id.hsv_award_addr);
        hsv_name = findViewById(R.id.hsv_award_name);
        et_tel = findViewById(R.id.et_award_tel);
//        hsv_phone = findViewById(R.id.hsv_award_phone);
        btn_award = findViewById(R.id.btn_award);
        iv_awards = findViewById(R.id.iv_awards);
        tv_awards = findViewById(R.id.tv_awards);
        tv_desc = findViewById(R.id.tv_awards_desc);

        tv_awards.setText(only_good_name);
        tv_desc.setText(only_good_text);
        RequestOptions options = new RequestOptions().placeholder(R.drawable.default_image2).error(R.drawable.default_image2);
        Glide.with(this).load(HTConstant.baseGoodsUrl + only_good_pic).apply(options).into(iv_awards);

        hsv_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AwardActivity.this, RegionActivity.class),UPDATE_REGION);
            }
        });
        btn_award.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hsv_name.getText().isEmpty() || et_tel.getText().toString().isEmpty()
                        || hsv_addr.getText().isEmpty() || addr.isEmpty())
                    Toast.makeText(AwardActivity.this,"请填写完整信息",Toast.LENGTH_SHORT).show();
                else
                    postAddress();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (intent != null && requestCode == UPDATE_REGION) {
            String province = intent.getStringExtra("province");
            String city = intent.getStringExtra("city");
            addr = province+city;
            hsv_city.setHintText(addr);
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void postAddress() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("task_id",taskId));
        params.add(new Param("prize_name",hsv_name.getText()));
        params.add(new Param("prize_tel",et_tel.getText().toString()));
        params.add(new Param("prize_address",addr+hsv_addr.getText()));
        new OkHttpUtils(this).post(params, HTConstant.URL_ADD_PRICE_ADDRESS, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("postAddress",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        Toast.makeText(AwardActivity.this,R.string.sending_success,Toast.LENGTH_SHORT).show();
                        finish();
                    default:
                        Toast.makeText(AwardActivity.this,R.string.update_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(AwardActivity.this,R.string.update_failed,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
