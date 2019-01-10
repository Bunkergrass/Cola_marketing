package com.htmessage.cola_marketing.activity.homepageFunc.tutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.chat.ChatActivity;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.HintSetView;
import com.htmessage.sdk.utils.MessageUtils;
import com.jrmf360.walletpaylib.JrmfWalletPayClient;

import java.util.ArrayList;
import java.util.List;

public class TutorDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_PAY = 101;

    HintSetView hsv_num,hsv_more;
    LinearLayout ll_tag;
    TextView tv_tag,tv_desc,tv_name,tv_id;
    Button btn_pay;

    private JSONObject object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_detail);
        setTitle("导师信息");

        initView();
        object = JSONObject.parseObject(getIntent().getStringExtra("tutor_json"));
        setView();
    }

    private void initView() {
        ll_tag = findViewById(R.id.ll_tutor_detail_tag);
        tv_desc = findViewById(R.id.tv_tutor_detail_desc);
        tv_tag = findViewById(R.id.tv_tutor_detail_tag);
        tv_name = findViewById(R.id.tv_tutor_detail_name);
        tv_id = findViewById(R.id.tv_tutor_detail_id);
        hsv_num = findViewById(R.id.hsv_tutor_detail_num);
        hsv_more = findViewById(R.id.hsv_tutor_detail_more);
        btn_pay = findViewById(R.id.btn_tutor_detail_pay);

        hsv_more.setOnClickListener(this);
        hsv_num.setOnClickListener(this);
        btn_pay.setOnClickListener(this);
        ll_tag.setOnClickListener(this);
    }

    private void setView() {
        String creator = object.getString("creator");
        String tutor_name = object.getString("tutor_name");
        String group_num = object.getString("group_num");
        String descri = object.getString("descri");
        JSONArray label = object.getJSONArray("label");
        String group_money = object.getString("group_money");

        tv_name.setText(tutor_name);
        tv_id.setText("群ID：" + group_num);
        tv_desc.setText("群介绍：" + descri);
        btn_pay.setText("支付"+group_money+"元入群");
        if (!label.isEmpty())
            tv_tag.setText(label.getJSONObject(0).getString("label_name"));
//        if (creator.equals(HTApp.getInstance().getUsername()))
//            btn_pay.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAY && resultCode == RESULT_OK) {
            String orderNum = data.getStringExtra(JrmfWalletPayClient.ORDER_NUM);
            String orderStateCode = data.getStringExtra(JrmfWalletPayClient.ORDER_STATE_CODE);
            if (orderStateCode.equals("0000")) {
                payGroup();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hsv_tutor_detail_num:
                break;
            case R.id.hsv_tutor_detail_more:
                break;
            case  R.id.ll_tutor_detail_tag:
                break;
            case R.id.btn_tutor_detail_pay:
                JrmfWalletPayClient.walletPay(this,0
                        ,HTApp.getInstance().getUsername() //用户id
                        ,object.getString("creator") //接受者id
                        ,object.getFloat("group_money").intValue() *100 //支付金额 单位：分
                        ,"" //订单号
                        ,"导师组进群费" //商品名称
                        ,HTApp.getInstance().getThirdToken() //第三方签名,由用户传递过来
                        ,REQUEST_CODE_PAY); //请求码
                break;
        }
    }

    private void payGroup() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",HTApp.getInstance().getUsername()));
        params.add(new Param("gid",object.getString("gid")));
        new OkHttpUtils(this).post(params, HTConstant.URL_PAY_GROUP, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        startActivity(new Intent(TutorDetailActivity.this,ChatActivity.class)
                                .putExtra("userId",object.getString("gid"))
                                .putExtra("chatType", MessageUtils.CHAT_GROUP));
                        finish();
                        break;
                    default:
                        Toast.makeText(TutorDetailActivity.this,R.string.failed_please_retry,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(TutorDetailActivity.this,R.string.failed_please_retry,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
