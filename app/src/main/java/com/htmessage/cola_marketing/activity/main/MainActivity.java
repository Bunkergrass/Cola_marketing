package com.htmessage.cola_marketing.activity.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.IMAction;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;
import com.htmessage.cola_marketing.activity.contacts.ContactsFragment;
import com.htmessage.cola_marketing.activity.login.LoginActivity;
import com.htmessage.cola_marketing.activity.main.conversation.ConversationFragment;
import com.htmessage.cola_marketing.activity.main.discover.DiscoverFragment;
import com.htmessage.cola_marketing.activity.main.homepage.HomepageFragment;
import com.htmessage.cola_marketing.activity.main.my.MyAccountFragment;
import com.htmessage.cola_marketing.manager.LocalUserManager;
import com.htmessage.cola_marketing.runtimepermissions.PermissionsManager;
import com.htmessage.cola_marketing.runtimepermissions.PermissionsResultAction;
import com.htmessage.cola_marketing.utils.CommonUtils;
import com.htmessage.sdk.client.HTClient;
import com.jrmf360.tools.manager.CusActivityManager;

public class MainActivity extends BaseActivity implements MainView {
    private BottomNavigationView main_navigation;
    private Fragment[] fragments;

    public boolean isConflict = false;
    private boolean isConflictDialogShow;
    private boolean ischeckdialogshow = false;
    private AlertDialog.Builder exceptionBuilder;
    private MainPrestener mainPrestener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            finish();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
        mainPrestener = new MainPrestener(this);

        fragments = new Fragment[]{new HomepageFragment(),new ConversationFragment(),new ContactsFragment(),new DiscoverFragment(),new MyAccountFragment()};
        initView();

        if (getIntent().getBooleanExtra(IMAction.ACTION_CONFLICT, false) && !isConflictDialogShow) {
            showConflicDialog();
        }
        requestPermissions();
        updateRed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(IMAction.ACTION_CONFLICT, false) && !isConflictDialogShow) {
            showConflicDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainPrestener.checkVersion();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainPrestener.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exceptionBuilder != null) {
            exceptionBuilder.create().dismiss();
            exceptionBuilder = null;
            isConflictDialogShow = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        hideBackView();
        main_navigation = findViewById(R.id.main_navigation);
        setView();
    }

    private void setView() {
        main_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        main_navigation.setSelectedItemId(main_navigation.getMenu().getItem(0).getItemId());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.navigation_homepage:
                            replaceFragment(fragments[0]);
                            return true;
                        case R.id.navigation_weike:
                            replaceFragment(fragments[1]);
                            return true;
                        case R.id.navigation_service:
                            replaceFragment(fragments[2]);
                            return true;
                        case R.id.navigation_discover:
                            replaceFragment(fragments[3]);
                            return true;
                        case R.id.navigation_my:
                            replaceFragment(fragments[4]);
                            return true;
                    }
                    return false;
                }
            };

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_main_fragment, fragment);
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
//                Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRed() {
        CommonUtils.upDateRedAvatarUrl(MainActivity.this, HTApp.getInstance().getUserNick(), HTApp.getInstance().getUserAvatar());
    }

    @Override
    public void showConflicDialog() {
        isConflictDialogShow = true;
        //HTClientHelper.getInstance().logout(null);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (exceptionBuilder == null)
                    exceptionBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                exceptionBuilder.setTitle(getResources().getString(R.string.Logoff_notification));
                exceptionBuilder.setMessage(R.string.connect_conflict);
                exceptionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        exceptionBuilder = null;
                        isConflictDialogShow = false;
                        HTApp.getInstance().setUserJson(null);
                        LocalUserManager.getInstance().saveVersionDialog(false);
                        finish();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                });
                exceptionBuilder.setCancelable(false);
                exceptionBuilder.show();
                isConflict = true;
            } catch (Exception e) {
                Log.e(TAG, "---------color conflictBuilder error" + e.getMessage());
            }
        }
    }

    @Override
    public void showUpdateDialog(String message, final String url, final boolean isForce) {
        if (ischeckdialogshow)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_alert, null);

        TextView tv_delete_people = dialogView.findViewById(R.id.tv_alert_content);
        View view_line_dialog = dialogView.findViewById(R.id.view_line_dialog);
        TextView tv_delete_title = dialogView.findViewById(R.id.tv_alert_title);
        TextView tv_cancle = dialogView.findViewById(R.id.tv_dialog_cancel);
        TextView tv_ok = dialogView.findViewById(R.id.tv_dialog_ok);

        tv_delete_title.setText(getString(R.string.has_update));
        tv_delete_people.setText(message);
        tv_cancle.setText(R.string.update_later);
        tv_ok.setText(R.string.update_now);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
        ischeckdialogshow = true;
        if (isForce) {
            view_line_dialog.setVisibility(View.GONE);
            tv_cancle.setVisibility(View.GONE);
            tv_ok.setText(R.string.update_has);
            dialog.setCancelable(false);//点击屏幕外不取消  返回键也没用
            dialog.setCanceledOnTouchOutside(false); //点击屏幕外取消,返回键有用
        } else {
            dialog.setCancelable(true);//点击屏幕外取消  返回键也没用
            dialog.setCanceledOnTouchOutside(true); //点击屏幕外取消,返回键有用
        }
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ischeckdialogshow = false;
                LocalUserManager.getInstance().saveVersionDialog(true);
                if (isForce)
                    logout();
            }
        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ischeckdialogshow = false;
                if (!isForce)
                    dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void logout() {
        HTClient.getInstance().logout(new HTClient.HTCallBack() {
            @Override
            public void onSuccess() {
                HTApp.getInstance().setUserJson(null);
                HTApp.getInstance().finishActivities();
                LocalUserManager.getInstance().saveVersionDialog(false);
                CusActivityManager.getInstance().finishAllActivity();
                HTApp.getInstance().clearThirdToken();
                finish();
            }

            @Override
            public void onError() {
                HTApp.getInstance().setUserJson(null);
                HTApp.getInstance().finishActivities();
                LocalUserManager.getInstance().saveVersionDialog(false);
                CusActivityManager.getInstance().finishAllActivity();
                HTApp.getInstance().clearThirdToken();
                finish();
            }
        });
    }

    @Override
    public void setPresenter(MainPrestener presenter) {
        mainPrestener = presenter;
    }

    @Override
    public Activity getBaseActivity() {
        return this;
    }

}
