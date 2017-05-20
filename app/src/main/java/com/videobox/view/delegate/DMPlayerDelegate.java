package com.videobox.view.delegate;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.commonlibs.base.AdapterViewPager;
import com.commonlibs.themvp.view.AppDelegate;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.ScreenUtils;
import com.commonlibs.util.SizeUtils;
import com.dailymotion.websdk.DMWebVideoView;
import com.videobox.AppAplication;
import com.videobox.R;
import com.videobox.player.dailymotion.DaiyMotionPlayerActivity;

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

        DMWebVideoView dmWebVideoView = get(R.id.dmWebVideoView);
        dmWebVideoView.getLayoutParams().height = (int)(ScreenUtils.getScreenWidth()*(9.0f/16.0f));

        playProgressBar = get(R.id.play_progress);
        playProgressBar.setIndeterminateDrawable(new IndeterminateHorizontalProgressDrawable(mContext));
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
}
