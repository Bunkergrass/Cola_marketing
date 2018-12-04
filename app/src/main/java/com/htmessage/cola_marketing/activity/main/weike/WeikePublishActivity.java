package com.htmessage.cola_marketing.activity.main.weike;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.common.BigImageActivity;
import com.htmessage.cola_marketing.utils.CommonUtils;
import com.htmessage.cola_marketing.utils.DensityUtil;
import com.htmessage.cola_marketing.utils.HTPathUtils;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.sdk.utils.UploadFileUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class WeikePublishActivity extends BaseActivity {
    private EditText et_weike_title;
    private EditText et_weike_content;
    GridView gv_weike_imgs;
    private ProgressDialog dialog;

    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_LOCAL = 1;
    private File cameraFile;
    private List<String> pathList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private ImageAdapter adapter;
    private int temp = 0;//次数

    private static class MyHandler extends Handler {
        WeakReference<WeikePublishActivity> reference;

        public MyHandler(WeikePublishActivity activity) {
            reference = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WeikePublishActivity activity = reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        activity.temp++;
                        if (activity.temp == activity.pathList.size()) {
                            String title = activity.et_weike_title.getText().toString().trim();
                            String content = activity.et_weike_content.getText().toString().trim();
                            activity.sendSocial(title,content);
                        }
                        break;
                    case 1:
                        activity.temp = 0;
                        activity.dialog.dismiss();
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
        setContentView(R.layout.activity_weike_publish);

        //ArrayList<String> imagePath = this.getIntent().getStringArrayListExtra("pathList");
        initView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists()) {
                    pathList.add(cameraFile.getAbsolutePath());
                    adapter.notifyDataSetChanged();

                }
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (data != null) {
                    ArrayList<String> list = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    if (list != null) {
                        pathList.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        setTitle("");
        //pathList.addAll(images);
        gv_weike_imgs = findViewById(R.id.gv_weike_imgs);

        adapter = new ImageAdapter(WeikePublishActivity.this, pathList);
        gv_weike_imgs.setAdapter(adapter);
        gv_weike_imgs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (pathList.size() < 9 && position == pathList.size()) {
                    showPhotoDialog();
                } else {
                    checkDialog(position);
                }
            }

        });

        et_weike_title = findViewById(R.id.et_weike_title);
        et_weike_content = findViewById(R.id.et_weike_content);

        showRightButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_weike_title.getText().toString().trim();
                String content = et_weike_content.getText().toString().trim();
                if (TextUtils.isEmpty(content) || TextUtils.isEmpty(title)) {
                    Toast.makeText(getApplicationContext(), R.string.input_text,Toast.LENGTH_SHORT).show();
                    return;
                }
                send(content);
            }
        });
    }

    private void send(String content) {
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.publishing));
        dialog.show();
        if (pathList.isEmpty()){
            String title = et_weike_title.getText().toString().trim();
            String text = et_weike_content.getText().toString().trim();
            sendSocial(title,text);
        }else {
            compressMore(pathList);
        }
    }

    private void uploadImages(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        nameList.add(fileName);
        new UploadFileUtils(WeikePublishActivity.this, fileName, filePath).asyncUploadFile(new UploadFileUtils.a() {
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
        final LinkedList<Runnable> taskList = new LinkedList<>();
        final ArrayList<String> newList = new ArrayList<>();//压缩后的图片路径
        final Handler handler = new Handler();
        for (String path : pathList){
            Luban.with(WeikePublishActivity.this)
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

    /**
     * 异步发送
     *
     * @param content 文字内容
     */
    private void sendSocial(final String title, final String content) {
        List<Param> params = new ArrayList<>();
        JSONObject userJson = HTApp.getInstance().getUserJson();
        String userId = userJson.getString(HTConstant.JSON_KEY_HXID);
        params.add(new Param("uid", userId));
        params.add(new Param("title", title));
        params.add(new Param("text", content));
        String imagePath = "";
        if (!nameList.isEmpty()){
            if (nameList.size() != 1 || nameList.size() > 1) {
                for (int i = 1; i < nameList.size() - 1; i++) {
                    imagePath += nameList.get(i) + ",";
                }
                imagePath = nameList.get(0) + "," + imagePath + nameList.get(nameList.size() - 1);
            } else {
                imagePath = nameList.get(0);
            }
            Log.e("sendSocial", imagePath);
        }
        params.add(new Param("url", imagePath));
        new OkHttpUtils(this).post(params, HTConstant.URL_WEIKE_SEND_POST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                dialog.dismiss();
                int code = jsonObject.getInteger("code");
                switch (code) {
                    case 1:
                        Toast.makeText(WeikePublishActivity.this, R.string.sending_success,Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    default:
                        Toast.makeText(WeikePublishActivity.this, getString(R.string.server_wrong) + code,Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                dialog.dismiss();
                Log.e("WeikePublishActivity",errorMsg);
                Toast.makeText(WeikePublishActivity.this,errorMsg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPhotoDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_social_main);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = DensityUtil.dp2px(WeikePublishActivity.this,320);
        window.setAttributes(lp);

        TextView tv_paizhao = window.findViewById(R.id.tv_content1);
        tv_paizhao.setText(R.string.attach_take_pic);
        tv_paizhao.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                if (!CommonUtils.isSdcardExist()) {
                    Toast.makeText(WeikePublishActivity.this, R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
                    return;
                }
                cameraFile = new File(new HTPathUtils(null, WeikePublishActivity.this).getImagePath() + "/" + HTApp.getInstance().getUsername()
                        + System.currentTimeMillis() + ".png");
                Uri uri = FileProvider.getUriForFile(WeikePublishActivity.this,getPackageName()+".provider",cameraFile);
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri), REQUEST_CODE_CAMERA);

                dlg.cancel();
            }
        });
        TextView tv_xiangce = window.findViewById(R.id.tv_content2);
        tv_xiangce.setText(R.string.image_manager);
        tv_xiangce.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(9 - pathList.size())
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(false)
                        .start(WeikePublishActivity.this, REQUEST_CODE_LOCAL);
                dlg.cancel();
            }
        });
    }

    private void checkDialog(final int position) {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_social_main);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = DensityUtil.dp2px(WeikePublishActivity.this,280);
        window.setAttributes(lp);

        TextView tv_paizhao = window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("看大图");
        tv_paizhao.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(WeikePublishActivity.this, BigImageActivity.class);
                intent.putExtra("images", pathList.toArray(new String[pathList.size()]));
                intent.putExtra("page", position);
                intent.putExtra("isNetUrl", false);//表示非网络图片,而是本地图片
                startActivity(intent);
                dlg.cancel();
            }
        });
        TextView tv_xiangce = window.findViewById(R.id.tv_content2);
        tv_xiangce.setText(R.string.delete);
        tv_xiangce.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pathList.remove(position);
                adapter.notifyDataSetChanged();
                dlg.cancel();
            }
        });
    }

    class ImageAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private Context context;
        private List<String> list;

        public ImageAdapter(Context context, List<String> list) {
            this.context = context;
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            int total = list.size();
            if (total < 9)
                total++;
            return total;
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.item_moment_gv_img, null);
            ImageView iv_grid_moment = convertView.findViewById(R.id.iv_grid_moment);
            if (position == list.size() && list.size() < 9){
                Glide.with(WeikePublishActivity.this).load(R.drawable.icon_add).into(iv_grid_moment);
            } else {
                RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.default_image);
                Glide.with(WeikePublishActivity.this).load(getItem(position)).apply(options).into(iv_grid_moment);
            }
            return convertView;
        }

    }
}
