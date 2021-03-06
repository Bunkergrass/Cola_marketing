package com.htmessage.cola_marketing.activity.login.register;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.utils.ACache;
import com.htmessage.cola_marketing.utils.SetTelCountTimer;
import com.htmessage.cola_marketing.widget.HTAlertDialog;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class RegisterActivity extends BaseActivity implements RegisterContract.View, View.OnClickListener {
    private EditText et_usernick, et_usertel, et_password;
    private Button btn_register, btn_code;
    private ImageView iv_hide, iv_show, iv_photo;
    private TextView tv_xieyi, tv_country, tv_country_code;
    private RelativeLayout rl_country;
    private ACache aCache;
    private SetTelCountTimer countTimer;
    private RegisterContract.Presenter mPresenter;
    //选取的原始图片
    private String imagePathOrigin = null;
    private Dialog dialog;
    private EditText et_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle(R.string.register);

        dialog = HTApp.getInstance().createLoadingDialog(this, getString(R.string.Is_the_registered));
        aCache = ACache.get(this);
        mPresenter = new RegisterPresenter(this);

        initView();
        initData();
        setLisenter();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initData() {
        String xieyi = "<font color=" + "\"" + "#AAAAAA" + "\">" + getString(R.string.press_top)
                + "&nbsp;" + "\"" + getString(R.string.register) + "\"" + "&nbsp;" + getString(R.string.btn_means_agree) + "</font>" + "<u>"
                + "<font color=" + "\"" + "#576B95" + "\">" + getString(R.string.Secret_agreement)
                + "</font>" + "</u>";
        tv_xieyi.setText(Html.fromHtml(xieyi));
        countTimer = new SetTelCountTimer(btn_code);
    }

    private void initView() {
        et_usernick = findViewById(R.id.et_usernick);
        et_usertel = findViewById(R.id.et_usertel);
        et_password = findViewById(R.id.et_password);
        //获取国家code
        tv_country = findViewById(R.id.tv_country);
        tv_country_code = findViewById(R.id.tv_country_code);
        rl_country = findViewById(R.id.rl_country);
        btn_register = findViewById(R.id.btn_register);
        tv_xieyi = findViewById(R.id.tv_xieyi);
        iv_hide = findViewById(R.id.iv_hide);
        iv_show = findViewById(R.id.iv_show);
        iv_photo = findViewById(R.id.iv_photo);
        btn_code = findViewById(R.id.btn_code);
        et_code = findViewById(R.id.et_code);
    }

    private void setLisenter() {
        // 监听多个输入框
        TextChange textChange = new TextChange();
        et_usernick.addTextChangedListener(textChange);
        et_usertel.addTextChangedListener(textChange);
        et_password.addTextChangedListener(textChange);

        tv_country.setOnClickListener(this);
        tv_country_code.setOnClickListener(this);
        rl_country.setOnClickListener(this);
        iv_hide.setOnClickListener(this);
        iv_show.setOnClickListener(this);
        iv_photo.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_code.setOnClickListener(this);
    }

    @Override
    public void showAvatar(String imagePath) {
        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.default_image);
        Glide.with(this).load(imagePath).apply(options).into(iv_photo);
    }

    @Override
    public void showDialog() {
        if (dialog != null)
            dialog.show();
    }

    @Override
    public void cancelDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void showPassword() {
        iv_show.setVisibility(View.GONE);
        iv_hide.setVisibility(View.VISIBLE);
        et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @Override
    public void hidePassword() {
        iv_hide.setVisibility(View.GONE);
        iv_show.setVisibility(View.VISIBLE);
        et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    @Override
    public void enableButton() {
        btn_register.setEnabled(true);
    }

    @Override
    public void disableButton() {
        btn_register.setEnabled(false);
    }

    @Override
    public void showToast(int msgRes) {
        Toast.makeText(this,msgRes,Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getOriginImagePath() {
        return imagePathOrigin;
    }

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Activity getBaseActivity() {
        return this;
    }

    @Override
    public void showSmsCode(String code) {
        aCache.put("registerCode", code);
        showToast(R.string.code_is_send);
    }

    @Override
    public void clearCacheCode() {
        aCache.put("registerCode", "");
    }

    @Override
    public void smsCodeError() {
        showToast(R.string.sending_failed);
    }

    @Override
    public void startTimeDown() {
        if (countTimer != null) {
            countTimer.start();
        }
    }

    @Override
    public void finishTimeDown() {
        if (countTimer != null) {
            countTimer.onFinish();
        }
    }

    // EditText监听器
    class TextChange implements TextWatcher {
        @Override
        public void afterTextChanged(Editable arg0) {}

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void onTextChanged(CharSequence cs, int start, int before, int count) {
            boolean sign1 = et_usernick.getText().length() > 0;
            boolean sign2 = et_usertel.getText().length() > 0;
            boolean sign3 = et_password.getText().length() > 0;
            if (sign1 & sign2 & sign3) {
                enableButton();
            } else {
                disableButton();
            }
        }
    }

    // 切换后将密码EditText光标置于末尾
    private void editTextEnd() {
        CharSequence charSequence = et_password.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_hide:
                hidePassword();
                editTextEnd();
                break;
            case R.id.iv_show:
                showPassword();
                editTextEnd();
                break;
            case R.id.btn_register:
                String usernick = et_usernick.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                String usertel = et_usertel.getText().toString().trim();
                String country = tv_country.getText().toString().trim();
                String countryCode = tv_country_code.getText().toString().trim();
                String code = et_code.getText().toString().trim();
                String registerCode = aCache.getAsString("registerCode");

                if (TextUtils.isEmpty(usertel)) {
                    showToast(R.string.mobile_not_be_null);
                    return;
                }
//                if (country.equals(getString(R.string.china)) && countryCode.equals(getString(R.string.country_code))) {
//                    if (!Validator.isMobile(usertel)) {
//                        showToast(R.string.please_input_true_mobile);
//                        return;
//                    }
//                }
                if (TextUtils.isEmpty(registerCode) || TextUtils.isEmpty(code)){
                    showToast(R.string.please_input_code);
                    return;
                }
                if (!code.equals(registerCode)){
                    showToast(R.string.code_is_wrong);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    showToast(R.string.pwd_is_not_allow_null);
                    return;
                }
                mPresenter.registerInServer(usernick, usertel, password);
                break;
            case R.id.rl_country:
                //CityCodeAndTimePickUtils.showPup(getContext(), tv_country, tv_country_code);
                break;
            case R.id.iv_photo:
                showCamera();
                break;
            case R.id.btn_code:
                String usertel2 = et_usertel.getText().toString().trim();
                String country2 = tv_country.getText().toString().trim();
                String countryCode2 = tv_country_code.getText().toString().trim();
                if (TextUtils.isEmpty(usertel2)) {
                    showToast(R.string.mobile_not_be_null);
                    return;
                }
//                if (country2.equals(getString(R.string.china)) && countryCode2.equals(getString(R.string.country_code))) {
//                    if (!Validator.isMobile86(usertel2)) {
//                        showToast(R.string.please_input_true_mobile);
//                        return;
//                    }
//                }
                mPresenter.sendSmsCode(usertel2,usertel2.length(), countryCode2);
                break;
        }
    }

    private String getAvatarName() {
        return HTApp.getInstance().getDirFilePath() + "org_" + System.currentTimeMillis() + ".png";
    }

    // 拍照部分
    private void showCamera() {
        HTAlertDialog HTAlertDialog = new HTAlertDialog(this, null, new String[]{getString(R.string.attach_take_pic), getString(R.string.image_manager)});
        HTAlertDialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                imagePathOrigin = getAvatarName();
                switch (position) {
                    case 0:
                        if (!checkPermission(Manifest.permission.CAMERA)) {
                            return;
                        }
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 指定调用相机拍照后照片的储存路径
                        Uri uri = FileProvider.getUriForFile(RegisterActivity.this, getPackageName()+".provider",new File(imagePathOrigin));
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(intent, RegisterContract.PHOTO_REQUEST_TAKEPHOTO);
                        break;
                    case 1:
                        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            return;
                        }
                        Crop.pickImage(RegisterActivity.this, RegisterContract.PHOTO_REQUEST_GALLERY);
                        break;
                }
            }
        });
    }

    private static final int REQUEST_CODE = 100;
    private boolean checkPermission(String permissionName) {
        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(permissionName, getPackageName()));
        if (permission) {
            return true;
        } else {
            showToast(R.string.no_permission_camera);
            requestPermissions(new String[]{permissionName}, REQUEST_CODE);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.CAMERA) || permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                        showToast(R.string.permission_ok);
                    } else {
                        showToast(R.string.permission_error);
                    }
                }
            }
        }
    }


}
