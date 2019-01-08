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
    private String edit_text;
    private boolean editable;

    public EditText et_hint_text;
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
        edit_text = array.getString(R.styleable.HintSetView_edit_text);
        editable = array.getBoolean(R.styleable.HintSetView_editable,false);

        array.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        TextView tv_key_text = findViewById(R.id.tv_key_text);
        iv_hint_go = findViewById(R.id.iv_hint_go);
        et_hint_text = findViewById(R.id.et_hint_text);

        tv_key_text.setText(key_text);
        if (hint_text == null && edit_text == null) {
            iv_hint_go.setVisibility(VISIBLE);
        } else {
            iv_hint_go.setVisibility(GONE);
            et_hint_text.setEnabled(editable);
            if (hint_text!=null)
                et_hint_text.setHint(hint_text);
            if (edit_text!=null)
                et_hint_text.setText(edit_text);
        }
//        if (hint_text.isEmpty()) {
//            iv_hint_go.setVisibility(VISIBLE);
//        } else {
//            iv_hint_go.setVisibility(GONE);
//            et_hint_text.setEnabled(editable);
//            et_hint_text.setHint(hint_text);
//        }
    }

    public void setHintText(String hint) {
        this.hint_text = hint;
        if (hint_text == null && edit_text == null) {
            iv_hint_go.setVisibility(VISIBLE);
        } else {
            iv_hint_go.setVisibility(GONE);
            et_hint_text.setEnabled(editable);
            if (hint_text!=null)
                et_hint_text.setHint(hint_text);
            if (edit_text!=null)
                et_hint_text.setText(edit_text);
        }
//        if (hint_text.isEmpty()) {
//            iv_hint_go.setVisibility(VISIBLE);
//        } else {
//            iv_hint_go.setVisibility(GONE);
//            et_hint_text.setEnabled(editable);
//            et_hint_text.setHint(hint_text);
//        }
    }

    public void setEditText(String editText) {
        this.edit_text = editText;
        if (hint_text == null && edit_text == null) {
            iv_hint_go.setVisibility(VISIBLE);
        } else {
            iv_hint_go.setVisibility(GONE);
            et_hint_text.setEnabled(editable);
            if (hint_text!=null)
                et_hint_text.setHint(hint_text);
            if (edit_text!=null)
                et_hint_text.setText(edit_text);
        }
    }

    public String getText() {
        return et_hint_text.getText().toString();
    }
}
