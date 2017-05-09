package com.videobox.view.delegate;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.commonlibs.themvp.view.AppDelegate;
import com.videobox.R;
import com.videobox.presenter.YouTubeFragment;

/**
 * Created by liyanju on 2017/5/1.
 */

public class YouTubeDelegate extends AppDelegate{

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private YouTubeFragment mYouTubeFragment;

    @Override
    public int getRootLayoutId() {
        return R.layout.youtube_fragment;
    }

    @Override
    public void initWidget() {
        mYouTubeFragment = getFragment();

        mRecyclerView = get(R.id.ytb_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setAdapter(mYouTubeFragment.getYouTubeRecyclerAdapter());

        mSwipeRefreshLayout = get(R.id.ytb_swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(mYouTubeFragment);
    }

    public void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    public void hideLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
