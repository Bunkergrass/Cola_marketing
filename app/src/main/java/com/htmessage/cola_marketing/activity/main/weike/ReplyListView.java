package com.htmessage.cola_marketing.activity.main.weike;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ReplyListView extends ListView {
    public ReplyListView(Context context) {
        super(context);
    }

    public ReplyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReplyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ReplyListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
