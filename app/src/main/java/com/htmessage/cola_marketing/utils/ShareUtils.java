package com.htmessage.cola_marketing.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

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
//        List<Intent> targetedShareIntents = new ArrayList<Intent>();
//        for (int i = 0; i < 3; i++) {
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("text/plain");
//            intent.setType("image/jpeg");
//            intent.putExtra(Intent.EXTRA_SUBJECT, title);
//            intent.putExtra(Intent.EXTRA_TEXT, extraText);//extraText为文本的内容
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//新任务栈
//
//            if (i == 0) {
//                intent.setPackage("com.tencent.mobileqq");
//            } else if (i == 1) {
//                intent.setPackage("com.tencent.mm");
//            } else if (i == 2) {
//                intent.setPackage("com.sina.weibo");
//            }
//            targetedShareIntents.add(intent);
//        }
//
//        Intent chooserIntent = Intent.createChooser(targetedShareIntents.get(0), title);
//        if (chooserIntent == null) {
//            return;
//        }
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
//
//        // A Parcelable[] of Intent or LabeledIntent objects as set with
//        // putExtra(String, Parcelable[]) of additional activities to place
//        // a the front of the list of choices, when shown to the user with a
//        // ACTION_CHOOSER.
//        try {
//            context.startActivity(chooserIntent);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(context, "Can't find share component to share", Toast.LENGTH_SHORT).show();
//        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setType("image/*");
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
                    targeted.setType("image/*");
                    targeted.putExtra(Intent.EXTRA_SUBJECT, title);
                    targeted.putExtra(Intent.EXTRA_TEXT, extraText);
                    targeted.setPackage(PACKAGE_QQ);
                    if (!intentsMap.containsKey(PACKAGE_QQ))
                        intentsMap.put(PACKAGE_QQ,targeted);
                } else if (packageName.equals(PACKAGE_WECHAT)) {
                    Intent targeted = new Intent(Intent.ACTION_SEND);
                    targeted.setType("text/plain");
                    targeted.setType("image/*");
                    targeted.putExtra(Intent.EXTRA_SUBJECT, title);
                    targeted.putExtra(Intent.EXTRA_TEXT, extraText);
                    targeted.setPackage(PACKAGE_WECHAT);
                    if (!intentsMap.containsKey(PACKAGE_WECHAT))
                        intentsMap.put(PACKAGE_WECHAT,targeted);
                } else if (packageName.equals(PACKAGE_WEIBO)) {
                    Intent targeted = new Intent(Intent.ACTION_SEND);
                    targeted.setType("text/plain");
                    targeted.setType("image/*");
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

    }
//        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//        if (resInfo != null) {
//            List<Intent> targetedShareIntents = new ArrayList<>();
//            for (ResolveInfo info : resInfo) {
//                String packageName = info.activityInfo.packageName;
//                if (packageName.equals("com.tencent.mobileqq")) {
//                    Intent targeted = new Intent(Intent.ACTION_SEND);
//                    targeted.setType("text/plain");
//                    targeted.putExtra(Intent.EXTRA_SUBJECT, title);
//                    targeted.putExtra(Intent.EXTRA_TEXT, extraText);
//                    targeted.setClassName("com.tencent.mobileqq","com.tencent.mobileqq.activity.JumpActivity");
//                    targetedShareIntents.add(targeted);
//                } else if (packageName.equals("com.tencent.mm")) {
//                    Intent targeted = new Intent(Intent.ACTION_SEND);
//                    targeted.setType("text/plain");
//                    targeted.putExtra(Intent.EXTRA_SUBJECT, title);
//                    targeted.putExtra(Intent.EXTRA_TEXT, extraText);
//                    targeted.setPackage("com.tencent.mm");
//                    targetedShareIntents.add(targeted);
//                } else if (packageName.equals("com.sina.weibo")) {
//                    Intent targeted = new Intent(Intent.ACTION_SEND);
//                    targeted.setType("text/plain");
//                    targeted.putExtra(Intent.EXTRA_SUBJECT, title);
//                    targeted.putExtra(Intent.EXTRA_TEXT, extraText);
//                    targeted.setPackage("com.sina.weibo");
//                    targetedShareIntents.add(targeted);
//                }
//            }
//            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "选择分享");
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
//            context.startActivity(chooserIntent);
//        }
}
