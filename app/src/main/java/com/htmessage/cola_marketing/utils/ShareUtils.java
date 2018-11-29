package com.htmessage.cola_marketing.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShareUtils {
    private static final String PACKAGE_QQ = "com.tencent.mobileqq";
    private static final String PACKAGE_WEIBO = "com.sina.weibo";
    private static final String PACKAGE_WECHAT = "com.tencent.mm";
    private static final String PACKAGE_WBLOG = "com.tencent.wblog";
    private static final String PACKAGE_MMS = "com.android.mms";


    /**
     *
     * */
    public static void shareImage(Context context,Uri uri,String title) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, title));
    }

    /**
     *
     */
    public static void shareText(Context context,String extraText,String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
//        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resInfo != null) {
            Map<String,Intent> intentsMap = new HashMap<>();
            for (ResolveInfo info : resInfo) {
                String packageName = info.activityInfo.packageName;
                if (packageName.equals(PACKAGE_QQ)) {
                    Intent targeted = new Intent(Intent.ACTION_SEND);
                    targeted.setType("text/plain");
//                    targeted.setType("image/*");
                    targeted.putExtra(Intent.EXTRA_SUBJECT, title);
                    targeted.putExtra(Intent.EXTRA_TEXT, extraText);
                    targeted.setPackage(PACKAGE_QQ);
                    if (!intentsMap.containsKey(PACKAGE_QQ))
                        intentsMap.put(PACKAGE_QQ,targeted);
                } else if (packageName.equals(PACKAGE_WECHAT)) {
                    Intent targeted = new Intent(Intent.ACTION_SEND);
                    targeted.setType("text/plain");
//                    targeted.setType("image/*");
                    targeted.putExtra(Intent.EXTRA_SUBJECT, title);
                    targeted.putExtra(Intent.EXTRA_TEXT, extraText);
                    targeted.setPackage(PACKAGE_WECHAT);
                    if (!intentsMap.containsKey(PACKAGE_WECHAT))
                        intentsMap.put(PACKAGE_WECHAT,targeted);
                } else if (packageName.equals(PACKAGE_WEIBO)) {
                    Intent targeted = new Intent(Intent.ACTION_SEND);
                    targeted.setType("text/plain");
//                    targeted.setType("image/*");
                    targeted.putExtra(Intent.EXTRA_SUBJECT, title);
                    targeted.putExtra(Intent.EXTRA_TEXT, extraText);
                    targeted.setPackage(PACKAGE_WEIBO);
                    if (!intentsMap.containsKey(PACKAGE_WEIBO))
                        intentsMap.put(PACKAGE_WEIBO,targeted);
                }
            }
            List<Intent> targetedShareIntents = new ArrayList<>(intentsMap.values());
            Intent chooserIntent = Intent.createChooser(intent.setPackage(PACKAGE_MMS), "选择分享");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
            context.startActivity(chooserIntent);
        }
    }

    /**
     *
     */
    public static void shareImageAndText(Context context,Uri uri,String extraText,String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain;image/*");
//        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resInfo != null) {
            Map<String,Intent> intentsMap = new HashMap<>();
            for (ResolveInfo info : resInfo) {
                String packageName = info.activityInfo.packageName;
                if (packageName.equals(PACKAGE_QQ)) {
                    Intent targeted = new Intent(Intent.ACTION_SEND);
                    targeted.setType("text/plain;image/*");
                    targeted.putExtra(Intent.EXTRA_SUBJECT, title);
                    targeted.putExtra(Intent.EXTRA_TEXT, extraText);
                    targeted.putExtra(Intent.EXTRA_STREAM, uri);
                    targeted.setPackage(PACKAGE_QQ);
                    if (!intentsMap.containsKey(PACKAGE_QQ))
                        intentsMap.put(PACKAGE_QQ,targeted);
                } else if (packageName.equals(PACKAGE_WECHAT)) {
                    Intent targeted = new Intent(Intent.ACTION_SEND);
                    targeted.setType("text/plain;image/*");
                    targeted.putExtra(Intent.EXTRA_SUBJECT, title);
                    targeted.putExtra(Intent.EXTRA_TEXT, extraText);
                    targeted.putExtra(Intent.EXTRA_STREAM, uri);
                    targeted.setPackage(PACKAGE_WECHAT);
                    if (!intentsMap.containsKey(PACKAGE_WECHAT))
                        intentsMap.put(PACKAGE_WECHAT,targeted);
                } else if (packageName.equals(PACKAGE_WEIBO)) {
                    Intent targeted = new Intent(Intent.ACTION_SEND);
                    targeted.setType("text/plain;image/*");
                    targeted.putExtra(Intent.EXTRA_SUBJECT, title);
                    targeted.putExtra(Intent.EXTRA_TEXT, extraText);
                    targeted.putExtra(Intent.EXTRA_STREAM, uri);
                    targeted.setPackage(PACKAGE_WEIBO);
                    if (!intentsMap.containsKey(PACKAGE_WEIBO))
                        intentsMap.put(PACKAGE_WEIBO,targeted);
                }
            }
            List<Intent> targetedShareIntents = new ArrayList<>(intentsMap.values());
            Intent chooserIntent = Intent.createChooser(intent.setPackage(PACKAGE_MMS), "选择分享");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
            context.startActivity(chooserIntent);
        }
    }

    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        boolean isSaved = false;
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
            isSaved = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
        return isSaved;
    }

}
