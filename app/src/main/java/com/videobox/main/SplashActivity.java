package com.videobox.main;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.commonlibs.base.BaseActivity;
import com.commonlibs.util.MyAnimatorListener;
import com.commonlibs.util.UIThreadHelper;
import com.commonlibs.util.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.videobox.AppAplication;
import com.videobox.R;
import com.videobox.util.AdViewManager;

/**
 * Created by liyanju on 2017/5/26.
 */

public class SplashActivity extends BaseActivity {

    private CountDownTimer countDownTimer;

    private ShimmerFrameLayout container;

    private ImageView signinBlurredIV;

    private ImageView signiniv;

    private AnimatorSet animatorSet;

    private TextView coutTV;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        AdViewManager.getInstances().requestNewInterstitial();

        if (AppAplication.spUtils.getBoolean("Shortcut", false)) {
            AppAplication.spUtils.put("Shortcut", true);
            Utils.addShortcut(this, MainActivity.class, getString(R.string.app_name), R.mipmap.ic_launcher);
        }

        signiniv = (ImageView) findViewById(R.id.signin_iv);
        container = (ShimmerFrameLayout)findViewById(R.id.shimmer_view_container);
        signinBlurredIV = (ImageView) findViewById(R.id.signin_blurred_iv);
        coutTV = (TextView)findViewById(R.id.count_tv);

        UIThreadHelper.getInstance().runViewUIThread(signinBlurredIV, new Runnable() {
            @Override
            public void run() {
                startCountDownTimer(6 * 1000);
                startAnimation();
            }
        });

    }

    private void startCountDownTimer(long millisInFuture) {
        countDownTimer = new CountDownTimer(millisInFuture , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                coutTV.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                finish();
            }
        };
        countDownTimer.start();
    }

    private void stopCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void startAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(signinBlurredIV, "alpha", 1f, 0f);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.setDuration(2000);
        objectAnimator.addListener(new MyAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                signinBlurredIV.setVisibility(View.GONE);
            }
        });
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(container, "alpha", 0f, 1f);
        objectAnimator2.setDuration(600);
        objectAnimator2.addListener(new MyAnimatorListener(){

            @Override
            public void onAnimationEnd(Animator animator) {
                UIThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        container.startShimmerAnimation();
                    }
                }, 500);
            }
        });

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator, objectAnimator2);
        animatorSet.setStartDelay(2000);
        animatorSet.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        container.stopShimmerAnimation();
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        stopCountDownTimer();
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SplashActivity.class);
        activity.startActivity(intent);
    }
}
