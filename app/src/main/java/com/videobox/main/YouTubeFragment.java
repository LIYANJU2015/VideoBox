package com.videobox.main;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.commonlibs.base.BaseRecyclerViewAdapter;
import com.commonlibs.rxerrorhandler.core.RxErrorHandler;
import com.commonlibs.rxerrorhandler.handler.ErrorHandleSubscriber;
import com.commonlibs.rxerrorhandler.handler.RetryWithDelay;
import com.commonlibs.themvp.presenter.FragmentPresenter;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.StringUtils;
import com.paginate.Paginate;
import com.videobox.R;
import com.videobox.model.APIConstant;
import com.videobox.model.youtube.YouTuBeModel;
import com.videobox.model.youtube.entity.YTBVideoPageBean;
import com.videobox.view.adapter.YouTubeListRecyclerAdapter;
import com.videobox.view.adapter.YouTubeMainRecyclerAdapter;
import com.videobox.view.delegate.Contract;
import com.videobox.view.delegate.YouTubeDelegate;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by liyanju on 2017/5/1.
 */

public class YouTubeFragment extends FragmentPresenter<YouTubeDelegate>
        implements Contract.IVideoListFragment, SwipeRefreshLayout.OnRefreshListener,
        BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<YTBVideoPageBean.YouTubeVideo>,
        Paginate.Callbacks {

    private ArrayList<YTBVideoPageBean.YouTubeVideo> mVideoList = new ArrayList<>();

    private YouTubeMainRecyclerAdapter mMainAdapter;
    private YouTubeListRecyclerAdapter mListAdapter;

    private YouTuBeModel mYoutuBeModel;
    private RxErrorHandler mRxErrorHandler;
    private boolean mIsFirst;

    private Paginate mPaginate;

    private String pageToken = "";

    private boolean mIsLoadingMore;
    private boolean mIsLoadedAll;

    private boolean mInHome = true;

    private String mCurChannelID;

    @Override
    protected Class<YouTubeDelegate> getDelegateClass() {
        return YouTubeDelegate.class;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mYoutuBeModel = new YouTuBeModel(getAppComponent().repositoryManager());
        mRxErrorHandler = getAppComponent().rxErrorHandler();
        mIsFirst = true;
    }

    @Override
    protected void initAndBindEvent() {
        getVideoData(true);
    }

    private void getVideoData(final boolean pullToRefresh) {
        boolean isEvictCache = true; //不使用缓存
        if (pullToRefresh && mIsFirst) {
            mIsFirst = true;
            isEvictCache = false;
        }

        mYoutuBeModel.getMostPopularVideos(APIConstant.YouTube.sMostPopularVideos, pageToken, isEvictCache)
                .subscribeOn(Schedulers.io())
                .compose(this.<YTBVideoPageBean>bindToLifecycle())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("getVideoData doOnSubscribe call");
                        if (pullToRefresh && viewDelegate != null) {
                            viewDelegate.showLoading();
                        } else {
                            mIsLoadingMore = true;
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("getVideoData", "doAfterTerminate pullToRefresh " + pullToRefresh);
                        if (viewDelegate != null && pullToRefresh) {
                            viewDelegate.hideLoading();
                        } else {
                            mIsLoadingMore = false;
                        }
                    }
                })
                .subscribe(new ErrorHandleSubscriber<YTBVideoPageBean>(mRxErrorHandler) {
                    @Override
                    public void onNext(YTBVideoPageBean dmVideosPageBean) {
                        LogUtils.v("getVideoData", "onNext");
                        if (pullToRefresh) {
                            mVideoList.clear();
                        }
                        mIsLoadedAll = StringUtils.isEmpty(dmVideosPageBean.nextPageToken);
                        if (!mIsLoadedAll) {
                            pageToken = dmVideosPageBean.nextPageToken;
                        }

                        int preEndIndex = mVideoList.size();
                        if (dmVideosPageBean.items != null) {
                            mVideoList.addAll(dmVideosPageBean.items);
                        }

                        if (mMainAdapter == null) {
                            mMainAdapter = new YouTubeMainRecyclerAdapter(mVideoList, mActivity);
                            mMainAdapter.setOnItemClickListener(YouTubeFragment.this);
                            viewDelegate.setAdapter(mMainAdapter);
                        }

                        if (mPaginate == null && mVideoList.size() > 0) {
                            mPaginate = Paginate.with(((RecyclerView) viewDelegate.get(R.id.ytb_recyclerview)), YouTubeFragment.this)
                                    .setLoadingTriggerThreshold(0)
                                    .build();
                            mPaginate.setHasMoreDataToLoad(false);
                        }

                        if (pullToRefresh)
                            mMainAdapter.notifyDataSetChanged();
                        else
                            mMainAdapter.notifyItemRangeInserted(preEndIndex, dmVideosPageBean.items.size());
                    }
                });
    }

    @Override
    public void showChannelVideoByID(String id) {
        mCurChannelID = id;
        pageToken = "";
        mIsLoadedAll = false;

        if (mPaginate != null) {
            mPaginate.unbind();
            mPaginate = null;
        }

        if (!StringUtils.isEmpty(id)) {
            if (mMainAdapter != null) {
                mMainAdapter.clearAdapter();
                mMainAdapter = null;
            }
            requestChannelVideo(true, true);
        } else {
            mInHome = true;
            if (mListAdapter != null) {
                mListAdapter.clearAdapter();
                mListAdapter = null;
            }
            getVideoData(true);
        }
    }

    private void requestChannelVideo(boolean update, final boolean pullToRefresh) {
        mYoutuBeModel.getCategoryVideos(pageToken, mCurChannelID, APIConstant.YouTube.sMostPopularVideos, update)
                .subscribeOn(Schedulers.io())
                .compose(this.<YTBVideoPageBean>bindToLifecycle())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("requestChannelVideo doOnSubscribe call");
                        if (pullToRefresh && viewDelegate != null) {
                            viewDelegate.showLoading();
                        } else {
                            mIsLoadingMore = true;
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("requestChannelVideo", "doAfterTerminate pullToRefresh " + pullToRefresh);
                        if (viewDelegate != null && pullToRefresh) {
                            viewDelegate.hideLoading();
                        } else {
                            mIsLoadingMore = false;
                        }
                    }
                })
                .subscribe(new ErrorHandleSubscriber<YTBVideoPageBean>(mRxErrorHandler) {
                    @Override
                    public void onNext(YTBVideoPageBean dmVideosPageBean) {
                        LogUtils.v("requestChannelVideo", "onNext");
                        if (pullToRefresh) {
                            mVideoList.clear();
                        }
                        mIsLoadedAll = StringUtils.isEmpty(dmVideosPageBean.nextPageToken);
                        if (!mIsLoadedAll) {
                            pageToken = dmVideosPageBean.nextPageToken;
                        }

                        int preEndIndex = mVideoList.size();
                        if (dmVideosPageBean.items != null) {
                            mVideoList.addAll(dmVideosPageBean.items);
                        }

                        if (mListAdapter == null) {
                            mListAdapter = new YouTubeListRecyclerAdapter(mVideoList, mActivity);
                            mListAdapter.setOnItemClickListener(YouTubeFragment.this);
                            viewDelegate.setAdapter(mListAdapter);
                        }

                        if (mPaginate == null && mVideoList.size() > 0) {
                            mPaginate = Paginate.with(((RecyclerView) viewDelegate.get(R.id.ytb_recyclerview)), YouTubeFragment.this)
                                    .setLoadingTriggerThreshold(0)
                                    .build();
                            mPaginate.setHasMoreDataToLoad(false);
                        }

                        if (pullToRefresh)
                            mListAdapter.notifyDataSetChanged();
                        else
                            mListAdapter.notifyItemRangeInserted(preEndIndex, dmVideosPageBean.items.size());
                    }
                });
    }

    @Override
    public void onRefresh() {
        pageToken = "";
        if (mInHome) {
            getVideoData(true);
        } else {
            requestChannelVideo(true, true);
        }
    }

    @Override
    public void onItemClick(View view, int viewType, YTBVideoPageBean.YouTubeVideo data, int position) {
        data.intoPlayer(mContext);
    }

    @Override
    public void onLoadMore() {
        if (mInHome) {
            getVideoData(false);
        } else {
            requestChannelVideo(true, false);
        }
    }

    @Override
    public boolean isLoading() {
        return mIsLoadingMore;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return mIsLoadedAll;
    }
}