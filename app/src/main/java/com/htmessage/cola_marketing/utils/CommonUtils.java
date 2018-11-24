package com.htmessage.cola_marketing.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.github.promeg.pinyinhelper.Pinyin;
import com.htmessage.cola_marketing.HTConstant;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.addfriends.invitefriend.ContactInfo;
import com.htmessage.cola_marketing.domain.User;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class CommonUtils {
    private static final String TAG = "CommonUtils";
    private static Toast toast;
    private static ProgressDialog dialog;
    private static String   app_id = "shops";
    private static String   app_secret = "LXnNLLPeFMF839IGzbBRVYMmFvUuB5Q1";//应用秘钥
    private static String   encoding_AESKey = "aIOtFulgOxx2eKKQkhQFbZAqaHjHe8N1FAvYiWA5kpa";
    private static String   token = "MYj0NQn4kBGNc545nc4yCk5Z9j44Y75G";

    /**
     * check if network avalable
     * @param context
     * @return boolean
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * check if sdcard exist
     * @return boolean
     */
    public static boolean isSdcardExist() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * change the resId int to string
     * @param resId
     */
    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    /**
     * get top activity from task
     * @param context
     * @return
     */
    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }

    /**
     * get message duration
     * @param context,string start,string now
     * @return string
     */
    public static String getDuration(Context context, long d1, long d2) {
        String backStr = "";
        // 毫秒ms
        long diff = d2 - d1;
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays != 0) {
            if (diffDays < 80) {
                if (1 < diffDays && diffDays < 2) {
                    backStr = context.getString(R.string.yesterday);
                } else if (1 < diffDays && diffDays < 2) {
                    backStr = context.getString(R.string.The_day_before_yesterday);
                } else {
                    backStr = String.valueOf(diffDays) + context.getString(R.string.Days_ago);
                }
            } else {
                backStr = context.getString(R.string.long_long_ago);
            }

        } else if (diffHours != 0) {
            backStr = String.valueOf(diffHours) + context.getString(R.string.An_hour_ago);
        } else if (diffMinutes != 0) {
            backStr = String.valueOf(diffMinutes) + context.getString(R.string.minutes_ago);
        } else if (diffSeconds != 0) {
            backStr = String.valueOf(diffSeconds) + context.getString(R.string.seconds_ago);
        } else {
            backStr = context.getString(R.string.just);
        }
        return backStr;
    }


    /**
    * get message duration
     * @param context,string start,string now
     * @return string
    */
    public static String getDuration(Context context, String rel_time, String now_time) {

        if (TextUtils.isEmpty(now_time)) {
            if (!TextUtils.isEmpty(rel_time)) {
                String showTime = rel_time.substring(0, rel_time.lastIndexOf(":"));
                return showTime;
            }
            return "时间错误";
        }

        String backStr = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(rel_time);
            d2 = format.parse(now_time);

            // 毫秒ms
            long diff = d2.getTime() - d1.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays != 0) {
                if (diffDays < 80) {
                    if (1 < diffDays && diffDays < 2) {
                        backStr = context.getString(R.string.yesterday);
                    } else if (1 < diffDays && diffDays < 2) {
                        backStr = context.getString(R.string.The_day_before_yesterday);
                    } else {
                        backStr = String.valueOf(diffDays) + context.getString(R.string.Days_ago);
                    }
                } else {
                    backStr = context.getString(R.string.long_long_ago);
                }

            } else if (diffHours != 0) {
                backStr = String.valueOf(diffHours) + context.getString(R.string.An_hour_ago);
            } else if (diffMinutes != 0) {
                backStr = String.valueOf(diffMinutes) + context.getString(R.string.minutes_ago);
            } else if (diffSeconds != 0) {
                backStr = String.valueOf(diffSeconds) + context.getString(R.string.seconds_ago);
            } else {
                backStr = context.getString(R.string.just);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return backStr;
    }

    /**
     * get message duration
     * @param activity
     * @return int
     */
    public static int getSupportSoftInputHeight(Activity activity) {
        Rect r = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight(activity);
            Log.d("observeSoftKeyboard---9", String.valueOf(getSoftButtonsBarHeight(activity)));
        }
        if (softInputHeight < 0) {
            Log.w("EmotionInputDetector", "Warning: value of softInputHeight is below zero!");
        }
        return softInputHeight;
    }

    /**
     * get message duration
     * @param activity
     * @return int
     */
    private static int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        }
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * softKeyboard display listener
     * @param activity
     */
    public static void observeSoftKeyboard(final Activity activity, final OnSoftKeyboardChangeListener listener) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            int previousKeyboardHeight = -1;
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHeight = rect.bottom - rect.top;
                int height = decorView.getHeight();
                int keyboardHeight = height - rect.bottom;

                if (Build.VERSION.SDK_INT >= 20) {
                    // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
                    keyboardHeight = keyboardHeight - getSoftButtonsBarHeight(activity);

                }

                if (previousKeyboardHeight != keyboardHeight) {
                    boolean hide = (double) displayHeight / height > 0.8;
                    listener.onSoftKeyBoardChange(keyboardHeight, !hide, this);
                }

                previousKeyboardHeight = height;

            }
        });
    }
    public interface OnSoftKeyboardChangeListener {
        void onSoftKeyBoardChange(int softKeybardHeight, boolean visible, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener);
    }

    /**
     * check if it is Chinese
     */
    public static boolean isChinese(String str) {
        char[] chars = str.toCharArray();
        boolean isGB2312 = false;
        for (int i = 0; i < chars.length; i++) {
            byte[] bytes = ("" + chars[i]).getBytes();
            if (bytes.length == 2) {
                int[] ints = new int[2];
                ints[0] = bytes[0] & 0xff;
                ints[1] = bytes[1] & 0xff;
                if (ints[0] >= 0x81 && ints[0] <= 0xFE && ints[1] >= 0x40 && ints[1] <= 0xFE) {
                    isGB2312 = true;
                    break;
                }
            }
        }
        return isGB2312;
    }

    /**
     * check if it is English
     */
    public static boolean isEnglish(String s) {
        char c = s.charAt(0);
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * check if it is numeric
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static String getMD5String(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("not such method");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
    public static String getMD5String() {
        return  getMD5String(token + app_secret + app_id + encoding_AESKey);
    }

    /**
     * the following part uses class User from domain
     */

    /**
     * set initial letter of according user's nickname( username if no nickname)
     * @param
     * @param user
     */
    public static void setUserInitialLetter(User user) {
        final String DefaultLetter = "#";
        String letter = DefaultLetter;
        if (!TextUtils.isEmpty(user.getNick())) {
            letter = Pinyin.toPinyin(user.getNick().toCharArray()[0]);
            user.setInitialLetter(letter.toUpperCase().substring(0, 1));
            if (isNumeric(user.getInitialLetter()) || !isEnglish(user.getInitialLetter())) {
                user.setInitialLetter("#");
            }
            return;
        }
        if (letter == DefaultLetter && !TextUtils.isEmpty(user.getUsername())) {
            letter = Pinyin.toPinyin(user.getUsername().toCharArray()[0]);
        }
        user.setInitialLetter(letter.substring(0, 1));
        if (isNumeric(user.getInitialLetter()) || !isEnglish(user.getInitialLetter())) {
            user.setInitialLetter("#");
        }
    }

    /**
     * set initial letter of according user's nickname( username if no nickname)
     *
     * @param
     * @param user
     */
    public static void setContactsInfoInitialLetter(ContactInfo user) {
        final String DefaultLetter = "#";
        String letter = DefaultLetter;
        if (!TextUtils.isEmpty(user.getName())) {
            letter = Pinyin.toPinyin(user.getName().toCharArray()[0]);
            user.setLetter(letter.toUpperCase().substring(0, 1));
            if (isNumeric(user.getLetter()) || !isEnglish(user.getLetter())) {
                user.setLetter("#");
            }
            return;
        }
        if (letter == DefaultLetter && !TextUtils.isEmpty(user.getName())) {
            letter = Pinyin.toPinyin(user.getName().toCharArray()[0]);
        }
        user.setLetter(letter.substring(0, 1));
        if (isNumeric(user.getLetter()) || !isEnglish(user.getLetter())) {
            user.setLetter("#");
        }
    }

    /**
     * set user's team letter
     * @param
     * @param user
     */
    public static void setUserTeamLetter(User user) {
        final String DefaultLetter = "0";
        String letter = DefaultLetter;
        String info = user.getUserInfo();
        JSONObject userJson = JSONObject.parseObject(info);
        String teamId = userJson.getString("teamId");
        if (!TextUtils.isEmpty(teamId)) {
            letter = teamId;
            user.setInitialLetter(letter);
            return;
        }
    }

    /**
     * transform User class and JSON
     */
    public static User Json2User(JSONObject userJson) {
        User user = new User(userJson.getString(HTConstant.JSON_KEY_HXID));
        user.setNick(userJson.getString(HTConstant.JSON_KEY_NICK));
        user.setAvatar(userJson.getString(HTConstant.JSON_KEY_AVATAR));
        user.setUserInfo(userJson.toJSONString());
        CommonUtils.setUserInitialLetter(user);
        return user;
    }
    public static JSONObject User2Json(User user) {
        JSONObject jsonObject = new JSONObject();
        String userInfo = user.getUserInfo();
        try {
            if (userInfo != null) {
                jsonObject = JSONObject.parseObject(userInfo);
            }
        } catch (JSONException e) {
            Log.d("JSONUtil----->>", "User2Json error");
        }
        return jsonObject;
    }

    interface AlertDialogCallback {
        void onPositive();
        void onNegative();
    }

    public static void showAlertDialog(Context context, String title, String content, final AlertDialogCallback callback) {
        View view = View.inflate(context, R.layout.dialog_alert, null);
        TextView tv_forward_title = view.findViewById(R.id.tv_alert_title);
        TextView tv_forward_content = view.findViewById(R.id.tv_alert_content);
        ImageView iv_foward = view.findViewById(R.id.iv_alert);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);
        tv_forward_title.setText(title);
        tv_forward_content.setText(content);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog dialog = builder.show();
        tv_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onPositive();
            }
        });
        tv_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onNegative();
            }
        });
    }

    public static void showAlertDialog(Context context, String title, String content, @Nullable Integer imageResId, final AlertDialogCallback callback) {
        View view = View.inflate(context, R.layout.dialog_alert, null);
        TextView tv_forward_title = view.findViewById(R.id.tv_alert_title);
        TextView tv_forward_content = view.findViewById(R.id.tv_alert_content);
        ImageView iv_foward = view.findViewById(R.id.iv_alert);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);
        tv_forward_title.setText(title);
        tv_forward_content.setText(content);
        iv_foward.setImageResource(imageResId);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog dialog = builder.show();
        tv_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onPositive();
            }
        });
        tv_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onNegative();
            }
        });
    }

    /**
     * 金融魔方Utils
     */
    public static void upDateRedAvatarUrl(Activity activity,String nick, String avatar) {
//        if (TextUtils.isEmpty(nick)) {
//            nick = HTApp.getInstance().getUsername();
//        }
//        if (TextUtils.isEmpty(avatar)) {
//            avatar = HTApp.getInstance().getUserAvatar();
//        }
//        JrmfRpClient.updateUserInfo(activity,HTApp.getInstance().getUsername(), HTApp.getInstance().getThirdToken(), nick, avatar, new OkHttpModelCallBack<BaseModel>() {
//            @Override
//            public void onSuccess(BaseModel baseModel) {
//                boolean success = baseModel.isSuccess();
//                if (success) {
//                    Log.d(TAG, "----更新魔方红包信息成功");
//                } else {
//                    Log.d(TAG, "----更新魔方红包信息失败");
//                }
//            }
//
//            @Override
//            public void onFail(String s) {
//                Log.d(TAG, "----更新魔方红包信息失败");
//            }
//        });
    }

}
