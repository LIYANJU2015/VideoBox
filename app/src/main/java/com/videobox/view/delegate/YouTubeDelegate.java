package com.videobox.view.delegate;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.OvershootInterpolator;

import com.commonlibs.themvp.view.AppDelegate;
import com.commonlibs.util.NetworkUtils;
import com.commonlibs.util.ScreenUtils;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.videobox.util.AdViewManager;
import com.videobox.util.DaiymotionUtil;
import com.videobox.R;
import com.videobox.main.YouTubeFragment;
import com.videobox.view.adapter.AdViewWrapperAdapter;
import com.videobox.view.adapter.BaseRecyclerViewAdapter;

import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

import static com.commonlibs.util.LogUtils.A;

/**
 * Created by liyanju on 2017/5/1.
 */

public class YouTubeDelegate extends AppDelegate{

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private YouTubeFragment mYouTubeFragment;
    private Activity mActivity;

    @Override
    public int getRootLayoutId() {
        return R.layout.youtube_fragment;
    }

    @Override
    public void initWidget() {
        mYouTubeFragment = getFragment();
        mActivity = getActivity();

        mRecyclerView = get(R.id.ytb_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));

        mSwipeRefreshLayout = get(R.id.ytb_swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(mYouTubeFragment);
    }

    public void setAdapter(BaseRecyclerViewAdapter adapter) {
        SlideInBottomAnimationAdapter animationAdapter = new SlideInBottomAnimationAdapter(adapter);
        animationAdapter.setFirstOnly(true);
        animationAdapter.setDuration(800);
        animationAdapter.setInterpolator(new OvershootInterpolator(.5f));

        AdViewWrapperAdapter adViewWrapperAdapter = new AdViewWrapperAdapter(animationAdapter);
        adapter.setAdViewAdapter(adViewWrapperAdapter);

        if (NetworkUtils.isConnected()) {
            AdView adView = new AdView(mActivity);
            adView.setAdUnitId(mActivity.getString(R.string.main_youtube_ad));
            adView.setAdSize(AdSize.BANNER);
            adView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT));
            AdViewWrapperAdapter.AdViewItem adViewItem = new AdViewWrapperAdapter.AdViewItem(adView, 4);
            adViewWrapperAdapter.addAdView(DaiymotionUtil.MAIN_DM_AD_VIEWTYPE, adViewItem);

            AdViewManager.getInstances().loadCurrShowAdView(mActivity.getClass(), adView);
        }

        mRecyclerView.setAdapter(adViewWrapperAdapter);
    }

    public void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    public void hideLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
