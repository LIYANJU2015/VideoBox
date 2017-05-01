package com.videobox.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by liyanju on 2017/5/1.
 */

public class DailyMotionImageView extends ImageView {

    public DailyMotionImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = (int) (getMeasuredWidth() * (9.f/16.f));
        setMeasuredDimension(getMeasuredWidth(), height);
    }
}
