package com.htmessage.cola_marketing.activity.homepageFunc.xiaoshou;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class ChooseImageView extends AppCompatImageView {
    private boolean isChoosen;

    public ChooseImageView(Context context) {
        super(context);
    }

    public ChooseImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChooseImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setIsChoosen(boolean isChoosen) {
        this.isChoosen = isChoosen;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = canvas.getHeight();
        int width = canvas.getWidth();
    }
}
