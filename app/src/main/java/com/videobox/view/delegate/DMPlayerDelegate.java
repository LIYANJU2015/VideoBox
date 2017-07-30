package com.videobox.view.delegate;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.commonlibs.base.AdapterViewPager;
import com.commonlibs.themvp.view.AppDelegate;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.ScreenUtils;
import com.commonlibs.util.SizeUtils;
import com.dailymotion.websdk.DMWebVideoView;
import com.videobox.AppAplication;
import com.videobox.R;
import com.videobox.player.dailymotion.DaiyMotionPlayerActivity;
import com.videobox.util.DownloadTubeRecomUtils;

import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static android.R.attr.max;

/**
 * Created by liyanju on 2017/5/1.
 */

public class DMPlayerDelegate extends AppDelegate{

    private CharSequence mTitle[] = {
        AppAplication.getContext().getString(R.string.player_introduction),
                AppAplication.getContext().getString(R.string.player_related)};

    private DaiyMotionPlayerActivity playerActivity;

    private MaterialProgressBar playProgressBar;

    private DMWebVideoView dmWebVideoView;

    private FrameLayout webviewContainer;

    private ImageView mDmDownloadIcon;

    private ObjectAnimator leftRightAnimator;

    @Override
    public int getRootLayoutId() {
        return R.layout.dm_player_layout;
    }

    @Override
    public void initWidget() {
        playerActivity = getActivity();
        AdapterViewPager adapterViewPager = new AdapterViewPager(playerActivity.getSupportFragmentManager());
        adapterViewPager.bindData(playerActivity.getAdapterFragment(), mTitle);

        ViewPager viewPager = get(R.id.dm_player_viewpager);
        viewPager.setAdapter(adapterViewPager);
        TabLayout tabLayout = get(R.id.dm_tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorHeight(SizeUtils.dp2px(3));

        dmWebVideoView = get(R.id.dmWebVideoView);
        dmWebVideoView.getLayoutParams().height = (int)(ScreenUtils.getScreenWidth()*(9.0f/16.0f));
        webviewContainer = get(R.id.webview_container);

        playProgressBar = get(R.id.play_progress);
        playProgressBar.setIndeterminateDrawable(new IndeterminateHorizontalProgressDrawable(mContext));

        mDmDownloadIcon = get(R.id.dm_download_icon);
        if (AppAplication.spUtils.getBoolean("isDmDownloadClick", false)
                || DownloadTubeRecomUtils.isInstallDownloadTube(mContext)) {
            mDmDownloadIcon.setVisibility(View.INVISIBLE);
        } else {
            mDmDownloadIcon.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        leftRightAnimator = DownloadTubeRecomUtils.leftRightShake(mDmDownloadIcon);
                        leftRightAnimator.setRepeatCount(3);
                        leftRightAnimator.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 600);
        }
        mDmDownloadIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!DownloadTubeRecomUtils.isInstallDownloadTube(mContext)) {
                    AppAplication.spUtils.put("isDmDownloadClick", true);
                    DownloadTubeRecomUtils.showDownloadTubeRecomDialog(playerActivity);
                }
            }
        });
    }

    public void setDownloadIconStatus(boolean isShown) {
        if (isShown) {
            if (AppAplication.spUtils.getBoolean("isDmDownloadClick", false)
                    || DownloadTubeRecomUtils.isInstallDownloadTube(mContext)) {
                return;
            }
            mDmDownloadIcon.setVisibility(View.VISIBLE);
        } else {
            if (leftRightAnimator != null && leftRightAnimator.isRunning()) {
                leftRightAnimator.cancel();
            }
            mDmDownloadIcon.setVisibility(View.INVISIBLE);
        }
    }

    public void setMaxProgress(int max) {
        LogUtils.v("setProgress", "setMaxProgress max" + max);
        playProgressBar.setMax(max);
        playProgressBar.setProgress(0);
    }

    public void setProgressEnd() {
        LogUtils.v("setProgress", "setProgressEnd ");
        playProgressBar.setProgress(playProgressBar.getMax());
    }

    public void setProgress(int progress) {
        LogUtils.v("setProgress", "progress" + progress);
        playProgressBar.setProgress(progress);
    }

    public void destroyWebView() {
        try {
            if (leftRightAnimator != null && leftRightAnimator.isRunning()) {
                leftRightAnimator.cancel();
            }

            webviewContainer.removeView(dmWebVideoView);
            dmWebVideoView.destroy();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
