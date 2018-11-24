package com.htmessage.cola_marketing.activity.main.qrcode;

import android.graphics.Bitmap;

import com.htmessage.cola_marketing.activity.BaseView;


public interface QrCodeView extends BaseView<QrCodePrester> {
    void showQrCode(Bitmap bitmap);
    void showError(String error);
}
