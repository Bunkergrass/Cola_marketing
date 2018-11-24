package com.htmessage.cola_marketing.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htmessage.cola_marketing.R;

public class HintSetView extends LinearLayout {
    private String key_text;
    private String hint_text;
    private boolean editable;

//    private TextView tv_hint_text;
    private EditText et_hint_text;
    private ImageView iv_hint_go;

    public HintSetView(Context context){
        super(context);
    }

    public HintSetView(Context context, AttributeSet attr) {
        super(context, attr);

        LayoutInflater.from(context).inflate(R.layout.item_hint_set, this, true);

        TypedArray array = context.obtainStyledAttributes(attr, R.styleable.HintSetView);
        key_text = array.getString(R.styleable.HintSetView_key_text);
        hint_text = array.getString(R.styleable.HintSetView_hint_text);
        editable = array.getBoolean(R.styleable.HintSetView_editable,false);

        array.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        TextView tv_key_text = findViewById(R.id.tv_key_text);
//        tv_hint_text = findViewById(R.id.tv_hint_text);
        iv_hint_go = findViewById(R.id.iv_hint_go);
        et_hint_text = findViewById(R.id.et_hint_text);

        tv_key_text.setText(key_text);
        if (hint_text.isEmpty()) {
            iv_hint_go.setVisibility(VISIBLE);
        } else {
            iv_hint_go.setVisibility(GONE);
//            if (editable){
//                et_hint_text.setHint(hint_text);
//                et_hint_text.setVisibility(VISIBLE);
//                tv_hint_text.setVisibility(GONE);
//            } else {
//                tv_hint_text.setText(hint_text);
//                et_hint_text.setVisibility(GONE);
//                tv_hint_text.setVisibility(VISIBLE);
//            }
            et_hint_text.setEnabled(editable);
            et_hint_text.setHint(hint_text);
        }
    }

    public void setHintText(String hint) {
        this.hint_text = hint;
        if (hint_text.isEmpty()) {
            iv_hint_go.setVisibility(VISIBLE);
        } else {
            iv_hint_go.setVisibility(GONE);
//            if (editable){
//                et_hint_text.setHint(hint_text);
//                et_hint_text.setVisibility(VISIBLE);
//                tv_hint_text.setVisibility(GONE);
//            } else {
//                tv_hint_text.setText(hint_text);
//                et_hint_text.setVisibility(GONE);
//                tv_hint_text.setVisibility(VISIBLE);
//            }
            et_hint_text.setEnabled(editable);
            et_hint_text.setHint(hint_text);
        }
    }

    public String getHintText() {
//        if (editable)
//            return et_hint_text.getText().toString();
//        else
//            return tv_hint_text.getText().toString();
        return et_hint_text.getText().toString();
    }
}
