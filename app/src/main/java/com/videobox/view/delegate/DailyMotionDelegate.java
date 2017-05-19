package com.videobox.view.delegate;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.OvershootInterpolator;

import com.commonlibs.base.BaseRecyclerViewAdapter;
import com.commonlibs.themvp.view.AppDelegate;
import com.videobox.R;
import com.videobox.main.DailyMotionFragment;

import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

/**
 * Created by liyanju on 2017/4/29.
 */

public class DailyMotionDelegate extends AppDelegate {

    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private DailyMotionFragment mDailymotionFragment;

    @Override
    public int getRootLayoutId() {
        return R.layout.daily_motion_fragment_layout;
    }

    @Override
    public void initWidget() {
        mDailymotionFragment = getFragment();

        mRecyclerView = get(R.id.dm_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));

        mSwipeRefreshLayout = get(R.id.dm_swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(mDailymotionFragment);
    }

    public void setAdapter(BaseRecyclerViewAdapter adapter) {
        SlideInBottomAnimationAdapter animationAdapter = new SlideInBottomAnimationAdapter(adapter);
        animationAdapter.setFirstOnly(true);
        animationAdapter.setDuration(800);
        animationAdapter.setInterpolator(new OvershootInterpolator(.5f));
        mRecyclerView.setAdapter(animationAdapter);
    }

    public void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    public void hideLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
