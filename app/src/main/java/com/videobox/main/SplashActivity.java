package com.videobox.main;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.commonlibs.base.BaseActivity;
import com.commonlibs.util.MyAnimatorListener;
import com.commonlibs.util.UIThreadHelper;
import com.commonlibs.util.Utils;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.videobox.AppAplication;
import com.videobox.R;
import com.videobox.util.AdViewManager;

import static com.videobox.util.AdViewManager.getInstances;

/**
 * Created by liyanju on 2017/5/26.
 */

public class SplashActivity extends BaseActivity {

    private CountDownTimer countDownTimer;

    private AnimatorSet animatorSet;

    private TextView coutTV;

    private ShimmerTextView shimmerTitleTV;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    Shimmer shimmer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash_layout);

        getInstances().requestNewInterstitial();

        shimmerTitleTV = (ShimmerTextView) findViewById(R.id.title);
        shimmerTitleTV.setTypeface(AppAplication.sCanaroExtraBold);
        coutTV = (TextView) findViewById(R.id.count_tv);

        findViewById(R.id.skip_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        UIThreadHelper.getInstance().runViewUIThread(coutTV, new Runnable() {
            @Override
            public void run() {
                startCountDownTimer(8 * 1000);
                startAnimation();
            }
        });

    }

    private void startCountDownTimer(long millisInFuture) {
        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                coutTV.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                coutTV.setText(String.valueOf(0));
                if (AppAplication.isShowABC()) {
                    AdViewManager.getInstances().interstitialAdShow();
                }
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
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(shimmerTitleTV, "alpha", 1f, 0f);
        objectAnimator2.setDuration(1200);
        objectAnimator2.addListener(new MyAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                shimmerTitleTV.setAlpha(1);
                shimmerTitleTV.setTextColor(ContextCompat.getColor(SplashActivity.this,
                        R.color.dailymotion_color2));
                shimmer = new Shimmer();
                shimmer.setRepeatCount(2)
                        .setDuration(1500)
                        .setStartDelay(300)
                        .setDirection(Shimmer.ANIMATION_DIRECTION_RTL)
                        .setAnimatorListener(new MyAnimatorListener() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                shimmerTitleTV.setTextColor(ContextCompat.getColor(mContext,
                                        R.color.material_white));
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                                super.onAnimationRepeat(animator);
                                shimmerTitleTV.setTextColor(ContextCompat.getColor(mContext,
                                        R.color.youtube_color));
                            }
                        })
                        .start(shimmerTitleTV);
            }
        });

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator2);
        animatorSet.setStartDelay(500);
        animatorSet.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animatorSet != null) {
            animatorSet.cancel();
        }

        if (shimmer != null) {
            shimmer.cancel();
        }

        stopCountDownTimer();
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SplashActivity.class);
        activity.startActivity(intent);
    }
}
