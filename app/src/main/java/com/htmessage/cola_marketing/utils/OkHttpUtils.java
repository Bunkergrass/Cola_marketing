package com.htmessage.cola_marketing.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTClientHelper;
import com.htmessage.cola_marketing.HTConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {
    private Context context;
    private OkHttpClient okHttpClient;
    private static final int RESULT_ERROR = 1000;
    private static final int RESULT_SUCESS = 2000;
    private HttpCallBack httpCallBack;

    public interface HttpCallBack {
        void onResponse(JSONObject jsonObject);
        void onFailure(String errorMsg);
    }

    /**
     * 下载不带进度
     */
    public interface DownloadCallBack {
        void onSuccess();
        void onFailure(String message);
    }

    /**
     * 下载的带进度的callback
     */
    public interface ProgressDownloadCallBack {
        void onSuccess();
        void onProgress(int progress);
        void onFailure();
    }

    private OkHandler handler = new OkHandler(this);
    private static class OkHandler extends Handler {
        private WeakReference<OkHttpUtils> reference;

        OkHandler(OkHttpUtils instance) {
            this.reference = new WeakReference<>(instance);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int reusltCode = msg.what;
            if (reference.get() != null) {
                Context context = reference.get().context;
                HttpCallBack httpCallBack = reference.get().httpCallBack;
                switch (reusltCode) {
                    case RESULT_ERROR:
                        Log.e("RESULT_ERROR: "+context.getClass().getSimpleName(),(String) msg.obj);
                        httpCallBack.onFailure((String) msg.obj);
                        break;
                    case RESULT_SUCESS:
                        String result = (String) msg.obj;
                        try {
                            JSONObject jsonObject = JSONObject.parseObject(result);
                            httpCallBack.onResponse(jsonObject);
                            if (jsonObject != null && jsonObject.containsKey("code") && jsonObject.containsKey("message")) {
                                if (jsonObject.getInteger("code") == 0 && jsonObject.getString("message").contains("session")) {
                                    HTClientHelper.getInstance().notifyConflict(context);
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("RESULT_SUCESS: "+context.getClass().getSimpleName(),(String) msg.obj);
                            httpCallBack.onFailure((String) msg.obj);
                        }
                        break;
                }
            }
        }
    }


    public OkHttpUtils(Context context) {
        this.context = context;
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory,sslParams.trustManager)
                .build();
    }

    private void startRequest(final Request request) {
        if (CommonUtils.isNetWorkConnected(context)) {
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message message = handler.obtainMessage();
                    message.what = RESULT_ERROR;
                    message.obj = e.toString();
                    message.sendToTarget();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Message message = handler.obtainMessage();
                    message.what = RESULT_SUCESS;
                    message.obj = response.body().string();
                    message.sendToTarget();
                }
            });
        }
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    //纯粹键值对post请求
    public void post(List<Param> params, String url, HttpCallBack httpCallBack) {
        this.httpCallBack = httpCallBack;
        FormBody.Builder builder = new FormBody.Builder();
        JSONObject userJson = HTApp.getInstance().getUserJson();
        if (userJson != null){
            String session = userJson.getString(HTConstant.JSON_KEY_SESSION);
            if (session != null){
                builder.add("session", session);
            }
        }

        for (Param param : params) {
            builder.add(param.getKey(), param.getValue());
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        startRequest(request);
    }

    //键值对+文件 post请求
    public void post(List<Param> params, List<File> files, String url, HttpCallBack httpCallBack) {
        this.httpCallBack = httpCallBack;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (Param param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.getKey() + "\""),
                    RequestBody.create(MediaType.parse(guessMimeType(param.getKey())), param.getValue()));
            Log.d("param.getKey()----->>", param.getKey());
            Log.d("param.getValue()----->>", param.getValue());
        }
        for (File file : files) {
            if (file != null && file.exists()) {
                //TODO-本项目固化文件的键名为“file”
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + "file" + "\"; filename=\"" + file.getName()+ "\""),
                        RequestBody.create(MediaType.parse(guessMimeType(file.getName())), file));
                Log.d("file.getName()----->>", file.getName());
            }
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        startRequest(request);

    }

    public void loadFile(String url, final String savePath, final DownloadCallBack  callBack){
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure(e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int len;
                byte[] buf = new byte[2048];
                InputStream inputStream = response.body().byteStream();
                //可以在这里自定义路径
                File file1 = new File(savePath);
                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                callBack.onSuccess();
            }
        });
    }

    /**
     * 下载文件带进度
     *
     * @param url      下载地址
     * @param savePath 保存地址
     * @param callBack ProgressDownloadCallBack
     */
    public void loadFileHasProgress(String url, final String savePath, final ProgressDownloadCallBack callBack) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int len;
                byte[] buf = new byte[2048];
                InputStream inputStream = response.body().byteStream();
                long requestLength = response.body().contentLength();
                long total = 0;
                //可以在这里自定义路径
                File file1 = new File(savePath);
                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                while ((len = inputStream.read(buf)) != -1) {
                    total += len;
                    // publishing the progress....
                    if (requestLength > 0) // only if total length is known
                        callBack.onProgress((int) (total * 100 / requestLength));
                    fileOutputStream.write(buf, 0, len);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                callBack.onSuccess();
            }
        });
    }

}
