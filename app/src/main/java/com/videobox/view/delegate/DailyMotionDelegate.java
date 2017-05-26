package com.videobox.view.delegate;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.OvershootInterpolator;

import com.commonlibs.themvp.view.AppDelegate;
import com.commonlibs.util.NetworkUtils;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.util.AdViewManager;
import com.util.DaiymotionUtil;
import com.videobox.R;
import com.videobox.main.DailyMotionFragment;
import com.videobox.view.adapter.AdViewWrapperAdapter;
import com.videobox.view.adapter.BaseRecyclerViewAdapter;

import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

/**
 * Created by liyanju on 2017/4/29.
 */

public class DailyMotionDelegate extends AppDelegate {

    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private DailyMotionFragment mDailymotionFragment;

    private Activity mActivity;

    private AdViewWrapperAdapter adViewWrapperAdapter;

    @Override
    public int getRootLayoutId() {
        return R.layout.daily_motion_fragment_layout;
    }

    @Override
    public void initWidget() {
        mActivity = getActivity();
        mDailymotionFragment = getFragment();

        mRecyclerView = get(R.id.dm_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));

        mSwipeRefreshLayout = get(R.id.dm_swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(mDailymotionFragment);
    }

    public boolean isAdapterAddAdView() {
        if (adViewWrapperAdapter != null) {
            return adViewWrapperAdapter.isAddAdView();
        }
        return false;
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
            adView.setAdUnitId(mActivity.getString(R.string.main_dailymotionplayer_ad));
            adView.setAdSize(AdSize.BANNER);
            adView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT));
            AdViewWrapperAdapter.AdViewItem adViewItem = new AdViewWrapperAdapter.AdViewItem(adView, 1);
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
