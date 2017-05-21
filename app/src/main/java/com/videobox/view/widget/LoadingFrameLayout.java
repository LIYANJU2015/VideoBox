package com.videobox.view.widget;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.commonlibs.util.LogUtils;
import com.commonlibs.util.MyAnimatorListener;
import com.commonlibs.util.UIThreadHelper;
import com.videobox.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by liyanju on 2017/5/13.
 */

public class LoadingFrameLayout extends FrameLayout {

    private AVLoadingIndicatorView loadView;

    private ImageView errorIV;

    public LoadingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        loadView = (AVLoadingIndicatorView) findViewById(R.id.loading_view);
        errorIV = (ImageView) findViewById(R.id.error_iv);
        setVisibility(View.GONE);
    }

    public void showNormal() {
        loadView.setVisibility(View.INVISIBLE);
        errorIV.setVisibility(View.INVISIBLE);
        setVisibility(View.GONE);
    }

    public void smoothToshow() {
        setVisibility(View.VISIBLE);
        errorIV.setVisibility(View.INVISIBLE);
        loadView.smoothToShow();
        setVisibility(View.VISIBLE);
    }

    public void showError() {
        UIThreadHelper.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                errorIV.setVisibility(View.VISIBLE);
                errorIV.setImageResource(R.drawable.oops_pacman_ghost);
                loadView.setVisibility(View.INVISIBLE);
                setVisibility(View.VISIBLE);
            }
        });
    }

    public void showDataNull() {
        errorIV.setVisibility(View.VISIBLE);
        errorIV.setImageResource(R.drawable.data_null);
        loadView.setVisibility(View.INVISIBLE);
        setVisibility(View.VISIBLE);
    }

    public void smoothToHide() {
        if (loadView.isShown()) {
            loadView.smoothToHide();
            animate().alpha(0f).setDuration(400).setListener(new MyAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    setVisibility(View.GONE);
                }
            });
        }
    }

}
