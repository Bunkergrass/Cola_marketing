package com.htmessage.cola_marketing.activity.main.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htmessage.cola_marketing.R;

public class SettingsView extends LinearLayout {
    private String text;
    private int img;
    private boolean show_img;

    public SettingsView(Context context){
        super(context);
    }

    public SettingsView(Context context, AttributeSet attr) {
        super(context, attr);

        LayoutInflater.from(context).inflate(R.layout.item_settings_view, this, true);

        TypedArray array = context.obtainStyledAttributes(attr, R.styleable.SettingsView);
        text = array.getString(R.styleable.SettingsView_setting_text);
        img = array.getResourceId(R.styleable.SettingsView_setting_img, R.drawable.icon_discover);
        show_img = array.getBoolean(R.styleable.SettingsView_show_image,true);

        array.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        TextView settings_text = findViewById(R.id.settings_text);
        ImageView settings_img = findViewById(R.id.settings_img);


        if (show_img) {
            settings_img.setImageResource(img);
            settings_text.setText(text);
        }
        else {
            settings_img.setVisibility(GONE);
            settings_text.setText("    "+text);
        }
    }
}
