package com.videobox.view.widget;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.commonlibs.util.MyAnimatorListener;
import com.videobox.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by liyanju on 2017/5/13.
 */

public class LoadingFrameLayout extends FrameLayout{

    private AVLoadingIndicatorView loadView;

    public LoadingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        loadView = (AVLoadingIndicatorView)findViewById(R.id.loading_view);
    }

    public void smoothToshow() {
        setVisibility(View.VISIBLE);
        loadView.smoothToShow();
    }

    public void smoothToHide() {
        loadView.smoothToHide();
        animate().alpha(0f).setDuration(400).setListener(new MyAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animator) {
                setVisibility(View.GONE);
            }
        });
    }

}
