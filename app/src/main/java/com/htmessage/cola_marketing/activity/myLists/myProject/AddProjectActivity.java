package com.htmessage.cola_marketing.activity.myLists.myProject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.domain.Project;
import com.htmessage.cola_marketing.utils.DensityUtil;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.HintSetView;
import com.htmessage.sdk.utils.UploadFileUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class AddProjectActivity extends BaseActivity implements View.OnClickListener {
    private HintSetView hsv_name,hsv_team,hsv_desc,hsv_scene,hsv_support;
    private FrameLayout fl_content;
    private LinearLayout ll_activity;

    private JSONObject data;
    public Project project;
    private ProjectDetailFragment detailFragment = new ProjectDetailFragment();
    private ProjectTeamFragment teamFragment = new ProjectTeamFragment();
    boolean isAdd;

    public List<String> pathList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();

    private static class MyHandler extends Handler {
        WeakReference<AddProjectActivity> reference;

        public MyHandler(AddProjectActivity activity) {
            reference = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AddProjectActivity activity = reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        if (activity.nullCheck())
                            activity.post();
                        break;
                    case 1:
                        Toast.makeText(activity, R.string.update_failed, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };
    MyHandler handler = new MyHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        setTitle("项目添加");
        initView();

        data = JSONObject.parseObject(getIntent().getStringExtra("project"));
        if (data != null) {
            project = new Project(data);
            isAdd = false;
            setView();
        } else {
            project = new Project();
            isAdd = true;
        }
    }

    private void initView() {
        fl_content = findViewById(R.id.fl_content);
        ll_activity = findViewById(R.id.ll_add_project);
        hsv_desc = findViewById(R.id.hsv_project_desc);
        hsv_name = findViewById(R.id.hsv_project_name);
        hsv_scene = findViewById(R.id.hsv_project_scene);
        hsv_support = findViewById(R.id.hsv_project_support);
        hsv_team = findViewById(R.id.hsv_project_team);
        Button btn_confirm = findViewById(R.id.btn_add_project);

        hsv_team.setOnClickListener(this);
        hsv_support.setOnClickListener(this);
        hsv_scene.setOnClickListener(this);
        hsv_name.setOnClickListener(this);
        hsv_desc.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hsv_project_desc:
                ll_activity.setVisibility(View.GONE);
                replaceFragment(detailFragment);
                fl_content.setVisibility(View.VISIBLE);
                break;
            case R.id.hsv_project_name:
                ll_activity.setVisibility(View.GONE);
//                replaceFragment(detailFragment);
                addFragment(detailFragment);
                fl_content.setVisibility(View.VISIBLE);
                break;
            case R.id.hsv_project_scene:
                showInputDialog(true);
                break;
            case R.id.hsv_project_support:
                showInputDialog(false);
                break;
            case R.id.hsv_project_team:
                ll_activity.setVisibility(View.GONE);
//                replaceFragment(teamFragment);
                addFragment(teamFragment);
                fl_content.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_add_project:
                compressMore(pathList);
                break;
        }
    }

    public void setView() {
        hsv_name.setHintText(project.getExplain_name());
        hsv_scene.setHintText(project.getExplain_scene());
        hsv_support.setHintText(project.getSupport());
    }

    @Override
    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (fl_content.getVisibility() == View.VISIBLE) {
            fl_content.setVisibility(View.GONE);
            ll_activity.setVisibility(View.VISIBLE);
            hideRightView();
        } else
            super.onBackPressed();
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, fragment);
        transaction.commit();
    }

    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragmentManager.findFragmentById(R.id.fl_content) != null)
            transaction.hide(fragmentManager.findFragmentById(R.id.fl_content));
        if (fragment.isAdded())
            transaction.show(fragment);
        else
            transaction.add(R.id.fl_content, fragment);
        transaction.commit();
    }

    private void showInputDialog(final boolean flag) {
        View view = View.inflate(AddProjectActivity.this,R.layout.dialog_input,null);
        ImageView iv_cancel = view.findViewById(R.id.iv_dialog_cancel);
        TextView tv_title = view.findViewById(R.id.tv_dialog_title);
        final EditText et_input = view.findViewById(R.id.et_dialog_input);
        Button btn_send = view.findViewById(R.id.btn_dialog_send);

        tv_title.setText("请输入");
        et_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(AddProjectActivity.this);
        builder.setView(view);
        final AlertDialog dialog = builder.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = DensityUtil.dp2px(AddProjectActivity.this,280);
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
                    Toast.makeText(AddProjectActivity.this,R.string.input_text,Toast.LENGTH_SHORT).show();
                }else {
                    if (flag) {
                        project.setExplain_scene(et_input.getText().toString());
                        hsv_scene.setHintText(project.getExplain_scene());
                    } else {
                        project.setSupport(et_input.getText().toString());
                        hsv_support.setHintText(project.getSupport());
                    }
                    dialog.dismiss();
                }
            }
        });
    }

    private boolean nullCheck() {
        if (project.getTeam_name() == null || project.getTeam() == null || project.getExplain_desc() == null
                || project.getExplain_end() == null || project.getExplain_name() == null || project.getExplain_scene() == null
                || project.getExplain_start() == null || project.getSupport() == null) {
            Toast.makeText(AddProjectActivity.this,R.string.inform_incomplete,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void post() {
        List<Param> params = new ArrayList<>();
        String url;
        if (isAdd){
            url = HTConstant.URL_PROJECT_ADD_EXPLAIN;
        } else {
            url = HTConstant.URL_PROJECT_UP_EXPLAIN;
            params.add(new Param("explain_id",project.getId()+""));
        }
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("team_name",project.getTeam_name()));
        params.add(new Param("team",project.getTeam()));
        String imgs = nameList.toString();
        params.add(new Param("team_pic",imgs.substring(1,imgs.length()-1)));
        params.add(new Param("explain_name",project.getExplain_name()));
        params.add(new Param("explain_start",project.getExplain_start().getTime() + ""));
        params.add(new Param("explain_end",project.getExplain_end().getTime() + ""));
        params.add(new Param("explain_describe",project.getExplain_desc()));
        params.add(new Param("explain_scene",project.getExplain_scene()));
        params.add(new Param("support",project.getSupport()));
        new OkHttpUtils(AddProjectActivity.this).post(params, url, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("AddProjectActivity.post",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        Toast.makeText(AddProjectActivity.this,R.string.sending_success,Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    default:
                        Toast.makeText(AddProjectActivity.this,R.string.update_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
            }
        });
    }

    private void uploadImages(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        nameList.add(fileName);
        new UploadFileUtils(AddProjectActivity.this, fileName, filePath).asyncUploadFile(new UploadFileUtils.a() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                int body = result.getStatusCode();
                if (body == 200) {
                    handler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                handler.sendEmptyMessage(1);
                Log.d("uploadImages---->", "链接错误:" + clientExcepion.getMessage() + "====服务错误:" + serviceException.getMessage());
            }
        });
    }

    /**
     * 压缩多图
     *
     * @param pathList 传入的为图片原始路径
     */
    private void compressMore(final List<String> pathList) {
        for (String path : pathList){
            Luban.with(AddProjectActivity.this)
                    .load(new File(path))
                    .ignoreBy(100) //不压缩的阈值，单位为K
                    .filter(new CompressionPredicate() {
                        @Override
                        public boolean apply(String path) {
                            //官方写法，似乎不能压缩gif
                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                            //return false;
                        }
                    })
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {}

                        @Override
                        public void onSuccess(File file) {
                            uploadImages(file.getAbsolutePath());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Luban error",e.toString());
                        }
                    }).launch();
        }
    }
}
