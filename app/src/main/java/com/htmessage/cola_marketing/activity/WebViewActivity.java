package com.htmessage.cola_marketing.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.widget.MyWebViewClient;

public class WebViewActivity extends BaseActivity {
    WebView webView;
    private static final String APP_CACAHE_DIRNAME = "/webcache";

    private static final int FILE_SELECT_CODE = 0;

    private ValueCallback<Uri> uploadMessage;//回调图片选择，4.4以下
    private ValueCallback<Uri[]> uploadCallbackAboveL;//回调图片选择，5.0以上

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = findViewById(R.id.webView);
        initDate();
    }

    private void initDate() {
        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");
        setTitle(title);

        WebSettings settings = webView.getSettings();
        //设置 缓存模式
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);
        //开启 database storage API 功能
        settings.setDatabaseEnabled(true);
        //支持javascript
        settings.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        settings.setSupportZoom(true);
        // 设置是否使用内置缩放工具
        settings.setBuiltInZoomControls(false);
        //设置使用viewport
        settings.setUseWideViewPort(true);
        //自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //使用预览模式
        settings.setLoadWithOverviewMode(true);
        //缓存路径
        String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
        //设置数据库缓存路径
        //settings.setDatabasePath(cacheDirPath); //deprecated in API 19  now it's default
        //设置  Application Caches 缓存目录
        settings.setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        settings.setAppCacheEnabled(true);
        //支持视频播放
        settings.setSupportMultipleWindows(true);
        //Flash player
        //settings.setPluginState(WebSettings.PluginState.ON); //deprecated in API 18

        webView.setWebChromeClient(new WebChromeClient(){
            @Nullable
            @Override
            public View getVideoLoadingProgressView() {
                Toast.makeText(WebViewActivity.this,"Loading video",Toast.LENGTH_SHORT).show();
                return super.getVideoLoadingProgressView();
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                uploadCallbackAboveL = filePathCallback;
                Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILE_SELECT_CODE);
                return true;
            }
        });

        MyWebViewClient myWebViewClient = new MyWebViewClient(this);
        myWebViewClient.setMessage(getString(R.string.loading));
        myWebViewClient.setWebView(webView, url);
        webView.setWebViewClient(myWebViewClient);
        //mWebView.addJavascriptInterface(new JavaScriptUtils(getApplicationContext()),"android");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) { return; }

        switch (requestCode){
            case FILE_SELECT_CODE:
                Uri uri = data.getData();
                Uri[] uris = new Uri[]{uri};
                ClipData clipData = data.getClipData();  //选择多张
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri1 = item.getUri();
                        uris[i]=uri1;
                    }
                }
                uploadCallbackAboveL.onReceiveValue(uris);//回调给js
                break;
        }
    }


}
