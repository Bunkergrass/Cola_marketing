package com.htmessage.cola_marketing.activity.homepageFunc.AptitudeExam;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamActivity extends BaseActivity {
    RadioGroup rg_exam;
    TextView tv_check_text,tv_time;
    LinearLayout ll_pre,ll_next,ll_exam;

    LinearLayout ll_exam_result;
    TextView tv_exam_result;
    ImageView iv_exam_result;
    Button btn_exam_result;

    private List<Check> checkList = new ArrayList<>();
    private String[] answerList;
    private String[] idList;
    private String check_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        setTitle("导师考核");

        requestCheckList();
        initView();
    }

    private void initView() {
        rg_exam = findViewById(R.id.rg_exam);
        tv_check_text = findViewById(R.id.tv_check_text);
        tv_time = findViewById(R.id.tv_exam_time);
        ll_exam = findViewById(R.id.ll_exam);
        ll_next = findViewById(R.id.ll_exam_next);
        ll_pre = findViewById(R.id.ll_exam_pre);

        ll_exam_result = findViewById(R.id.ll_exam_result);
        tv_exam_result = findViewById(R.id.tv_exam_result);
        iv_exam_result = findViewById(R.id.iv_exam_result);
        btn_exam_result = findViewById(R.id.btn_exam_result);
    }

    private void showView(final int i) {
        rg_exam.removeAllViews();
        rg_exam.clearCheck();
        if (i < checkList.size()) {
            Check check = checkList.get(i);

            String num = (i+1) + "/" + checkList.size() + "  ";
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.DarkGray));
            SpannableString spannableString = new SpannableString(num + check.question);
            spannableString.setSpan(colorSpan,0,num.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tv_check_text.setText(spannableString);

            char c = 'A';
            for (String option : check.getOptions()) {
                RadioButton radioButton = new RadioButton(ExamActivity.this);
                radioButton.setText(c+ ". " + option);
                radioButton.setId(c);
                radioButton.setTextSize(16);
                radioButton.setPadding(20,40,20,40);
                rg_exam.addView(radioButton);
                c++;
            }
            if (answerList[i] != null)
                rg_exam.check(answerList[i].charAt(0));
//            rg_exam.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(RadioGroup group, int checkedId) {
//                    char checked = (char) checkedId;
//                    answerList[i] = String.valueOf(checked);
//                    showView(i+1);
//                }
//            });
            ll_pre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (i>0)
                        showView(i-1);
                }
            });
            ll_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    char checked = (char) rg_exam.getCheckedRadioButtonId();
                    answerList[i] = String.valueOf(checked);
                    showView(i+1);
                }
            });
        } else {
            ll_exam.setVisibility(View.GONE);
            ll_exam_result.setVisibility(View.VISIBLE);
            iv_exam_result.setImageResource(R.drawable.loadingicon);
            tv_exam_result.setText("等待结果");
            btn_exam_result.setVisibility(View.INVISIBLE);
            postAnswer();
        }
    }

    private void showFinishView(boolean isSuccess) {
        ll_exam.setVisibility(View.GONE);
        ll_exam_result.setVisibility(View.VISIBLE);
        btn_exam_result.setVisibility(View.VISIBLE);
        if (isSuccess) {
            iv_exam_result.setImageResource(R.drawable.img_test_pass);
            tv_exam_result.setText("考核通过！");
            btn_exam_result.setText("返回首页");
            btn_exam_result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            iv_exam_result.setImageResource(R.drawable.img_test_fail);
            tv_exam_result.setText("考核未通过");
            btn_exam_result.setText("再来一次");
            btn_exam_result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
        }
    }

    private void requestCheckList() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("type", "1"));
        new OkHttpUtils(this).post(params, HTConstant.URL_CHECK_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("requestCheckList",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray array = data.getJSONArray("check_con");

                        idList = new String[array.size()];
                        check_id = data.getString("check_id");
                        for (int i = 0 ; i<array.size() ; i++) {
                            checkList.add(new Check(array.getJSONObject(i)));
                            idList[i] = array.getJSONObject(i).getString("check_con_id");
                        }
                        answerList = new String[checkList.size()];
                        showView(0);
                        break;
                    default:
                        Toast.makeText(ExamActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(ExamActivity.this,R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postAnswer() {
        String check_con_ids = Arrays.toString(idList);
        String check_result_selects = Arrays.toString(answerList);

        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("check_id", check_id));
        params.add(new Param("check_con_ids", check_con_ids.substring(1,check_con_ids.length()-1)));
        params.add(new Param("check_result_selects", check_result_selects.substring(1,check_result_selects.length()-1)));
        new OkHttpUtils(this).post(params, HTConstant.URL_CHECK_RESULT, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("postAnswer",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        showFinishView(true);
                        break;
                    case 2:
                        showFinishView(false);
                        break;
                    default:
                        Toast.makeText(ExamActivity.this,R.string.update_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(ExamActivity.this,R.string.update_failed,Toast.LENGTH_SHORT).show();
            }
        });
    }



    static class Check {
        String question;
        String[] options;

        Check(JSONObject object) {
            question = object.getString("check_text");
            JSONArray array = object.getJSONArray("check_select");
            options = new String[array.size()];
            for (int i=0 ; i<array.size() ; i++) {
                options[i] = array.getJSONObject(i).getString("check_select_text");
            }
        }

        public String getQuestion() {
            return question;
        }

        public String[] getOptions() {
            return options;
        }
    }

}
