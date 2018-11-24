package com.htmessage.cola_marketing.activity.main.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htmessage.cola_marketing.R;

public class HomepageFuncView extends LinearLayout {
    private String text;
    private int img;

    public HomepageFuncView(Context context){
        super(context);
    }

    public HomepageFuncView(Context context, AttributeSet attr) {
        super(context, attr);

        LayoutInflater.from(context).inflate(R.layout.widget_home_func_view, this, true);

        TypedArray array = context.obtainStyledAttributes(attr, R.styleable.SettingsView);
        text = array.getString(R.styleable.SettingsView_setting_text);
        img = array.getResourceId(R.styleable.SettingsView_setting_img, R.drawable.icon_discover);

        array.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        TextView settings_text = findViewById(R.id.tv_homefunc);
        ImageView settings_img = findViewById(R.id.iv_homefunc);

        settings_text.setText(text);
        settings_img.setImageResource(img);
    }
}
