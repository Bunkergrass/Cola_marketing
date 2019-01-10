package com.htmessage.cola_marketing.activity.homepageFunc.tutor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.utils.DensityUtil;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;

import java.util.ArrayList;
import java.util.List;

public class TutorTagActivity extends BaseActivity {
    Button btn_add;
    RecyclerView rv_tags;
    TutorTagAdapter adapter;
    private List<JSONObject> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tutor_tag);
        setTitle("导师标签");

        rv_tags = findViewById(R.id.rv_tutor_tags);
        btn_add = findViewById(R.id.btn_add_tutor_tag);
        getTagList();
        setView();
    }

    private void setView() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInpuDialog();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new TutorTagAdapter(this,list);
        rv_tags.setAdapter(adapter);
        rv_tags.setLayoutManager(manager);
        adapter.setListener(new TutorTagAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                showDeleteDialog(list.get(position).getInteger("tutor_label_id"));
            }
        });
    }

    private void checkNum() {
        if (adapter.getItemCount()>=3)
            btn_add.setVisibility(View.GONE);
        else
            btn_add.setVisibility(View.VISIBLE);
    }

    private void showInpuDialog() {
        View view = View.inflate(TutorTagActivity.this,R.layout.dialog_input,null);
        ImageView iv_cancel = view.findViewById(R.id.iv_dialog_cancel);
        TextView tv_title = view.findViewById(R.id.tv_dialog_title);
        final EditText et_input = view.findViewById(R.id.et_dialog_input);
        Button btn_send = view.findViewById(R.id.btn_dialog_send);
        et_input.setMaxLines(1);
        et_input.setMaxEms(12);
        tv_title.setText("请输入");

        //禁用换行
        et_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(TutorTagActivity.this);
        builder.setView(view);
        final AlertDialog dialog = builder.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = DensityUtil.dp2px(TutorTagActivity.this,320);
        window.setAttributes(lp);

        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_input.getText().toString().isEmpty()){
                    Toast.makeText(TutorTagActivity.this,R.string.input_text,Toast.LENGTH_SHORT).show();
                }else {
                    addTag(et_input.getText().toString());
                    dialog.dismiss();
                }
            }
        });
    }

    private void showDeleteDialog (final int id) {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_social_main);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = DensityUtil.dp2px(this,280);
        window.setAttributes(lp);

        TextView tv_content1 = window.findViewById(R.id.tv_content1);
        tv_content1.setVisibility(View.GONE);

        TextView tv_content2 = window.findViewById(R.id.tv_content2);
        tv_content2.setText("删除");
        tv_content2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteTag(id);
                dlg.cancel();
            }
        });
    }

    private void getTagList() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",HTApp.getInstance().getUsername()));
        params.add(new Param("page","1"));
        new OkHttpUtils(this).post(params, HTConstant.URL_TUTOR_LABEL_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("getTagList",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        JSONArray data = jsonObject.getJSONArray("data");
                        list.clear();
                        list.addAll( JSONArray.parseArray(data.toJSONString(), JSONObject.class) );
                        adapter.notifyDataSetChanged();
                        checkNum();
                        break;
                    default:
                        Toast.makeText(TutorTagActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(TutorTagActivity.this,R.string.failed_please_retry,Toast.LENGTH_SHORT).show();
                startActivity(getIntent());
                finish();
            }
        });
    }

    private void deleteTag(int id) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",HTApp.getInstance().getUsername()));
        params.add(new Param("tutor_label_id",id+""));
        new OkHttpUtils(this).post(params, HTConstant.URL_TUTOR_LABEL_DEL, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("deleteTag",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        getTagList();
                        break;
                    default:
                        Toast.makeText(TutorTagActivity.this,R.string.failed_please_retry,Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(TutorTagActivity.this,R.string.failed_please_retry,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTag(String name) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid",HTApp.getInstance().getUsername()));
        params.add(new Param("label_name",name));
        new OkHttpUtils(this).post(params, HTConstant.URL_TUTOR_LABEL_ADD, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("addTag",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        getTagList();
                        break;
                    default:
                        Toast.makeText(TutorTagActivity.this,R.string.failed_please_retry,Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(TutorTagActivity.this,R.string.failed_please_retry,Toast.LENGTH_SHORT).show();
            }
        });
    }


}
