package com.videobox.view.delegate;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.commonlibs.themvp.view.AppDelegate;
import com.paginate.Paginate;
import com.videobox.R;
import com.videobox.presenter.DailyMotionFragment;

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
        mRecyclerView.setAdapter(mDailymotionFragment.getDailyMotionRecyclerAdapter());

        mSwipeRefreshLayout = get(R.id.dm_swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(mDailymotionFragment);
    }

    public void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    public void hideLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
