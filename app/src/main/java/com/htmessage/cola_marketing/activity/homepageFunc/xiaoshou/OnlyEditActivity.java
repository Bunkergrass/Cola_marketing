package com.htmessage.cola_marketing.activity.homepageFunc.xiaoshou;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.utils.DensityUtil;
import com.htmessage.cola_marketing.utils.OkHttpUtils;
import com.htmessage.cola_marketing.utils.Param;
import com.htmessage.cola_marketing.widget.HintSetView;

import java.util.ArrayList;
import java.util.List;

public class OnlyEditActivity extends BaseActivity {
    private HintSetView hsv_name,hsv_price,hsv_stock,hsv_main,hsv_imgs,hsv_desc;
    private Button btn_confirm;
    private LinearLayout ll_main;
    private FrameLayout fl_fragment;

    private String onlyId,goods_id,price,name,stock;
    private final ChooseImageFragment chooseFragment1 = new ChooseImageFragment();
    private final ChooseImageFragment chooseFragment9 = new ChooseImageFragment();

    String main_pic,pics,desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_edit);
        setTitle("编辑");

        onlyId = getIntent().getStringExtra("onlyId");
        goods_id = getIntent().getStringExtra("goods_id");
        price = getIntent().getStringExtra("price");
        name = getIntent().getStringExtra("name");
        stock = getIntent().getStringExtra("stock");
        initView();
        requestGoodsImgs();
    }

    private void initView() {
        ll_main = findViewById(R.id.ll_edit_main);
        fl_fragment = findViewById(R.id.fl_choose_fragment);
        hsv_desc = findViewById(R.id.hsv_only_desc);
        hsv_imgs = findViewById(R.id.hsv_only_imgs);
        hsv_main = findViewById(R.id.hsv_only_main);
        hsv_name = findViewById(R.id.hsv_only_name);
        hsv_price = findViewById(R.id.hsv_only_price);
        hsv_stock = findViewById(R.id.hsv_only_stock);
        btn_confirm = findViewById(R.id.btn_only_confirm);

        hsv_name.setHintText(name);
        hsv_stock.setHintText(stock);
        hsv_price.setHintText(price+"元");
    }

    private void setViewListener(final String[] strings) {
        hsv_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });
        hsv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_main.setVisibility(View.GONE);
                replaceFragment(chooseFragment1, strings,false);
                fl_fragment.setVisibility(View.VISIBLE);
            }
        });
        hsv_imgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_main.setVisibility(View.GONE);
                replaceFragment(chooseFragment9,strings,true);
                fl_fragment.setVisibility(View.VISIBLE);
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nullCheck()){
                    if (goods_id != null)
                        addGoods();
                    else
                        updateGoods();
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment,String[] data,boolean isPics){
        Bundle bundle = new Bundle();
        bundle.putStringArray("data",data);
        bundle.putBoolean("isPics",isPics);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_choose_fragment, fragment);
        transaction.commit();
    }

    @Override
    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (fl_fragment.getVisibility() == View.VISIBLE) {
            fl_fragment.setVisibility(View.GONE);
            ll_main.setVisibility(View.VISIBLE);

            hideRightView();
        } else
            super.onBackPressed();
    }

    private void showInputDialog() {
        View view = View.inflate(this,R.layout.dialog_input,null);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog dialog = builder.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = DensityUtil.dp2px(this,280);
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
                    Toast.makeText(OnlyEditActivity.this,R.string.input_text,Toast.LENGTH_SHORT).show();
                }else {
                    hsv_desc.setHintText(et_input.getText().toString());
                    desc = et_input.getText().toString();
                    dialog.dismiss();
                }
            }
        });
    }

    private void requestGoodsImgs() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("only_id", onlyId));
        new OkHttpUtils(OnlyEditActivity.this).post(params, HTConstant.URL_GOODS_IMAGES, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("requestGoodsImgs",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        JSONArray datas = jsonObject.getJSONArray("data");
                        String str = datas.toString();
                        String[] strings = str.substring(1,str.length()-1).replace("\"","").split(",");
                        setViewListener(strings);
                        break;
                    default:
                        Toast.makeText(OnlyEditActivity.this,R.string.just_nothing,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(OnlyEditActivity.this,R.string.request_failed_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean nullCheck() {
        if (main_pic == null || pics == null || desc == null) {
            Toast.makeText(OnlyEditActivity.this,R.string.inform_incomplete,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateGoods() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("only_id", onlyId));
        params.add(new Param("onlygood_text", desc));
        params.add(new Param("onlygood_pic", main_pic));
        params.add(new Param("onlygood_pics", pics));
        new OkHttpUtils(OnlyEditActivity.this).post(params, HTConstant.URL_GOODS_UP, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("updateGoods",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        Toast.makeText(OnlyEditActivity.this,R.string.update_success,Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    default:
                        Toast.makeText(OnlyEditActivity.this,R.string.update_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(OnlyEditActivity.this,R.string.update_failed,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addGoods() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        params.add(new Param("only_id", onlyId));
        params.add(new Param("goods_id", goods_id));
        params.add(new Param("onlygood_name", name));
        params.add(new Param("onlygood_text", desc));
        params.add(new Param("onlygood_pic", main_pic));
        params.add(new Param("onlygood_pics", pics));
        new OkHttpUtils(OnlyEditActivity.this).post(params, HTConstant.URL_GOODS_ADD, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("addGoods",jsonObject.toString());
                switch (jsonObject.getInteger("code")) {
                    case 1:
                        Toast.makeText(OnlyEditActivity.this,R.string.update_success,Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case 2:
                        Toast.makeText(OnlyEditActivity.this,"已存在，可以前往【我的销售】修改",Toast.LENGTH_SHORT).show();
                        finish();
                    default:
                        Toast.makeText(OnlyEditActivity.this,R.string.update_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(OnlyEditActivity.this,R.string.update_failed,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
