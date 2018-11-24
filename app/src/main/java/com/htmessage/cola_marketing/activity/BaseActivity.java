package com.htmessage.cola_marketing.activity;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.R;

public class BaseActivity extends AppCompatActivity {
    public String TAG = BaseActivity.this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HTApp.getInstance().saveActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Log.d("onCreate-------->",BaseActivity.this.getClass().getName());
    }

    public void back(View view) {
        finish();
    }

    public void setTitle(int title) {
        TextView textView = this.findViewById(R.id.tv_title);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.addRule();
//        imageView.setLayoutParams(layoutParams);
        if (textView != null) {
            textView.setText(title);
        }
    }

    public void setTitle(String title) {
        TextView textView = this.findViewById(R.id.tv_title);
        if (textView != null) {
            textView.setText(title);
        }
    }

    public void hideBackView() {
        ImageView iv_back = this.findViewById(R.id.iv_back);
        TextView tv_back = this.findViewById(R.id.tv_back);
        if (iv_back != null && tv_back != null) {
            iv_back.setVisibility(View.GONE);
            tv_back.setVisibility(View.GONE);
        }
    }

    public void changeBackView(int icon,View.OnClickListener onClickListener) {
        ImageView iv_back = this.findViewById(R.id.iv_back);
        TextView tv_back = this.findViewById(R.id.tv_back);
        if (iv_back != null && tv_back != null) {
            iv_back.setVisibility(View.VISIBLE);
            tv_back.setVisibility(View.VISIBLE);
            iv_back.setImageResource(icon);
        }
        if (onClickListener != null) {
            iv_back.setOnClickListener(onClickListener);
        }
    }

    public void changeBackView(int icon, String text, View.OnClickListener onClickListener) {
        ImageView iv_back = this.findViewById(R.id.iv_back);
        TextView tv_back = this.findViewById(R.id.tv_back);
        if (iv_back != null && tv_back != null) {
            iv_back.setVisibility(View.VISIBLE);
            tv_back.setVisibility(View.VISIBLE);
            iv_back.setImageResource(icon);
            tv_back.setText(text);
        }
        if (onClickListener != null) {
            iv_back.setOnClickListener(onClickListener);
        }
    }

    public void hideRightView(){
        ImageView ivRight = this.findViewById(R.id.iv_right);
        Button btn_Right = this.findViewById(R.id.btn_right);
        TextView tv_right = this.findViewById(R.id.tv_right);
        ivRight.setVisibility(View.GONE);
        btn_Right.setVisibility(View.GONE);
        tv_right.setVisibility(View.GONE);
    }

    public void showRightView(int res, View.OnClickListener onClickListener) {
        ImageView ivRight = this.findViewById(R.id.iv_right);
        if (ivRight != null) {
            ivRight.setImageResource(res);
            ivRight.setVisibility(View.VISIBLE);
            if (onClickListener != null) {
                ivRight.setOnClickListener(onClickListener);
            }
        }
    }

    public void showRightButton(View.OnClickListener onClickListener) {
        Button btn_Right = this.findViewById(R.id.btn_right);
        if (btn_Right != null) {
            btn_Right.setVisibility(View.VISIBLE);
            if (onClickListener != null) {
                btn_Right.setOnClickListener(onClickListener);
            }
        }
    }

    public void showRightButton(int res, View.OnClickListener onClickListener) {
        Button btn_Right = this.findViewById(R.id.btn_right);
        if (btn_Right != null) {
            btn_Right.setText(res);
            btn_Right.setVisibility(View.VISIBLE);
            if (onClickListener != null) {
                btn_Right.setOnClickListener(onClickListener);
            }
        }
    }

    public void showRightButton(String res, View.OnClickListener onClickListener) {
        Button btn_Right = this.findViewById(R.id.btn_right);
        if (btn_Right != null) {
            btn_Right.setText(res);
            btn_Right.setVisibility(View.VISIBLE);
            if (onClickListener != null) {
                btn_Right.setOnClickListener(onClickListener);
            }
        }
    }

    public void showRightTextView(int res, View.OnClickListener onClickListener) {
        TextView tv_right = this.findViewById(R.id.tv_right);
        if (tv_right != null) {
            tv_right.setText(res);
            tv_right.setVisibility(View.VISIBLE);
            if (onClickListener != null) {
                tv_right.setOnClickListener(onClickListener);
            }
        }
    }

    public void showRightTextView(String res, View.OnClickListener onClickListener) {
        TextView tv_right = this.findViewById(R.id.tv_right);
        if (tv_right != null) {
            tv_right.setText(res);
            tv_right.setVisibility(View.VISIBLE);
            if (onClickListener != null) {
                tv_right.setOnClickListener(onClickListener);
            }
        }
    }

    protected boolean isCompatible(int apiLevel) {
        return Build.VERSION.SDK_INT >= apiLevel;
    }
}
