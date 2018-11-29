package com.htmessage.cola_marketing.activity.homepageFunc.xiaoshou;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.WebViewActivity;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.utils.ShareUtils;

import java.util.ArrayList;
import java.util.List;

public class XiaoShouActivity extends BaseActivity {
    private ImageView iv_xiaoshou;
    private TextView tv_price,tv_stock,tv_name;
    private LinearLayout ll_share;
    private WebView wb_detail;
    private Button btn_shop;

    private String onlyId,onlyGoodId;
    private boolean state,isAdd;
    private final Bitmap[] mainPic = new Bitmap[1];
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiaoshou);
        setTitle("商品预览");

        onlyId = getIntent().getStringExtra("onlyId");
        state = getIntent().getBooleanExtra("state",false);
        isAdd = getIntent().getBooleanExtra("isAdd",false);
        onlyGoodId = getIntent().getStringExtra("onlyGoodId");

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestOnlyDetail();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        iv_xiaoshou = findViewById(R.id.iv_xiaoshou);
        wb_detail = findViewById(R.id.wb_xiaoshou_detail);
        tv_name = findViewById(R.id.tv_xiaoshou_name);
        tv_price = findViewById(R.id.tv_xiaoshou_price);
        tv_stock = findViewById(R.id.tv_xiaoshou_stock);
        ll_share = findViewById(R.id.ll_xiaoshou_share);
        btn_shop = findViewById(R.id.btn_xiaoshou_shop);

        WebSettings webSettings = wb_detail.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
    }

    private void showView(JSONObject jsonObject) {
        final String goods_id = jsonObject.getString("goods_id");
        final String market_price = jsonObject.getString("market_price");
        final String name = jsonObject.getString("only_good_name");
        final String stock = jsonObject.getString("stock");
        final String imgUrl = jsonObject.getString("only_good_pic");
        String description = jsonObject.getString("description");
        final String shareUrl = HTConstant.baseGoodsUrl + jsonObject.getString("only_url");

        tv_name.setText(name);
        tv_stock.setText("库存量  " + stock);

        SpannableString string = new SpannableString("￥" + market_price);
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.6f);
        string.setSpan(sizeSpan,0,1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_price.setText(string);

        wb_detail.loadDataWithBaseURL(HTConstant.baseGoodsUrl,description,"text/html", "utf-8",null);

        RequestOptions options = new RequestOptions().placeholder(R.drawable.default_image2).error(R.drawable.default_image2);
        Glide.with(this).load(HTConstant.baseGoodsUrl+imgUrl).apply(options).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                BitmapDrawable bd = (BitmapDrawable) resource;
                mainPic[0] = bd.getBitmap();
                return false;
            }
        }).into(iv_xiaoshou);

        if (state) {
            final Intent intent = new Intent(XiaoShouActivity.this,OnlyEditActivity.class)
                    .putExtra("onlyId",onlyId)
                    .putExtra("price",market_price)
                    .putExtra("name",name)
                    .putExtra("stock",stock);
            if (isAdd)
                intent.putExtra("goods_id",goods_id);
            showRightTextView("编辑", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intent);
                }
            });
        }
        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mainPic[0] != null && uri == null) {
//                    uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), mainPic[0], name,null));
//                    Toast.makeText(XiaoShouActivity.this,"图片已保存",Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                    intent.setData(uri);
//                    sendBroadcast(intent);//通知相册更新
//                }
                if (mainPic[0] != null) {
                    Toast.makeText(XiaoShouActivity.this,"图片已保存",Toast.LENGTH_SHORT).show();
                    ShareUtils.saveImageToGallery(getApplicationContext(),mainPic[0]);
                }
                requestEncryptUrl();
            }
        });
        btn_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(XiaoShouActivity.this, WebViewActivity.class)
                        .putExtra("url",shareUrl)
                        .putExtra("title","和商城"));
            }
        });
    }

    private void requestOnlyDetail() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("only_id",onlyId));
        new OkHttpUtils(this).post(params, HTConstant.URL_TRADE_ONLY_DETAIL, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("requestOnlyDetail",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        showView(jsonObject.getJSONObject("data"));
                        break;
                    default:
                        Toast.makeText(XiaoShouActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(XiaoShouActivity.this,R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestEncryptUrl() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("only_id",onlyId));
        if (onlyGoodId == null) {
            params.add(new Param("type","0"));
        } else {
            params.add(new Param("type","0"));
            params.add(new Param("onlygood_id",onlyGoodId));
        }
        new OkHttpUtils(this).post(params, HTConstant.URL_ENCRYPT_URL, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("requestEncryptUrl",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
//                        if (uri != null) {
//                            ShareUtils.shareImageAndText(XiaoShouActivity.this,uri
//                                    ,HTConstant.baseGoodsUrl.substring(0,HTConstant.baseGoodsUrl.length()-1) + jsonObject.getString("data")
//                                    ,"分享商品动态链接");
//                        } else
                        ShareUtils.shareText(XiaoShouActivity.this
                                ,HTConstant.baseGoodsUrl.substring(0,HTConstant.baseGoodsUrl.length()-1) + jsonObject.getString("data")
                                ,"分享商品动态链接");
                        break;
                    default:
                        Toast.makeText(XiaoShouActivity.this,"获取动态链接失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(XiaoShouActivity.this,"获取动态链接失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
