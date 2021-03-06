package com.videobox.main;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.commonlibs.rxerrorhandler.core.RxErrorHandler;
import com.commonlibs.rxerrorhandler.handler.ErrorHandleSubscriber;
import com.commonlibs.rxerrorhandler.handler.RetryWithDelay;
import com.commonlibs.themvp.presenter.FragmentPresenter;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.StringUtils;
import com.commonlibs.util.UIThreadHelper;
import com.commonlibs.widget.imageloader.ImageLoader;
import com.paginate.Paginate;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.videobox.R;
import com.videobox.model.APIConstant;
import com.videobox.model.dailymotion.DaiyMotionModel;
import com.videobox.model.dailymotion.entity.DMVideoBean;
import com.videobox.model.dailymotion.entity.DMVideosPageBean;
import com.videobox.player.dailymotion.DaiyMotionPlayerActivity;
import com.videobox.view.adapter.AdViewWrapperAdapter;
import com.videobox.view.adapter.BaseRecyclerViewAdapter;
import com.videobox.view.adapter.DMListRecyclerAdapter;
import com.videobox.view.adapter.DMMainRecyclerAdapter;
import com.videobox.view.delegate.Contract;
import com.videobox.view.delegate.DailyMotionDelegate;
import com.videobox.view.widget.LoadingFrameLayout;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


/**
 * Created by liyanju on 2017/4/29.
 */

public class DailyMotionFragment extends FragmentPresenter<DailyMotionDelegate> implements
        SwipeRefreshLayout.OnRefreshListener, Paginate.Callbacks, Contract.IVideoListFragment,
        BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<DMVideoBean> {

    private static final String TAG = DailyMotionFragment.class.getSimpleName();

    private DaiyMotionModel mDaiyMotionModel;
    private RxErrorHandler mRxErrorHandler;

    private ImageView mHeaderIV;

    private boolean mIsLoadingMore;

    private boolean mIsFirst;

    private ArrayList<DMVideoBean> mDMVideoList = new ArrayList<>();

    private ImageLoader mImageLoader;

    private Context mContext;

    private int pagenum = 1;

    private Paginate mPaginate;

    private boolean mInHome = true;

    private String mCurChannelID;

    private boolean mIsLoadedAll = false;

    private LoadingFrameLayout loadingFrameLayout;

    private DMMainRecyclerAdapter mAdapter;
    private DMListRecyclerAdapter mListAdapter;


    @Override
    protected Class<DailyMotionDelegate> getDelegateClass() {
        return DailyMotionDelegate.class;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getContext();
        mDaiyMotionModel = new DaiyMotionModel(getAppComponent().repositoryManager());
        mRxErrorHandler = getAppComponent().rxErrorHandler();
        mImageLoader = getAppComponent().imageLoader();
        mIsFirst = true;
    }

    @Override
    protected void initAndBindEvent() {
        loadingFrameLayout = viewDelegate.get(R.id.loading_frame);

        getVideoData(true);

    }

    @Override
    public void showChannelVideoByID(String id) {
        pagenum = 1;
        mCurChannelID = id;
        mIsLoadedAll = false;

        if (mPaginate != null) {
            mPaginate.unbind();
            mPaginate = null;
        }

        if (!StringUtils.isEmpty(id)) {
            mInHome = false;
            if (mAdapter != null) {
                mAdapter.clearAdapter();
                mAdapter = null;
            }
            getChannelVideoByID(id, pagenum, true, true);
        } else {
            mInHome = true;
            if (mListAdapter != null) {
                mListAdapter.clearAdapter();
                mListAdapter = null;
            }
            getVideoData(true);
        }
    }

    private void getChannelVideoByID(String id, int page, boolean update, final boolean pullToRefresh) {
        LogUtils.v("getChannelVideoByID ", "id " + id);
        mDaiyMotionModel.getChannelVideos(id,
                APIConstant.DailyMontion.sChannelVideosMap, page, update)
                .subscribeOn(Schedulers.io())
                .compose(this.<DMVideosPageBean>bindUntilEvent(FragmentEvent.PAUSE))
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("getChannelVideoByID doOnSubscribe call");
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
                        LogUtils.v("getChannelVideoByID", "doAfterTerminate pullToRefresh " + pullToRefresh);
                        if (viewDelegate != null && pullToRefresh) {
                            viewDelegate.hideLoading();
                        } else {
                            mIsLoadingMore = false;
                        }
                    }
                })
                .subscribe(new ErrorHandleSubscriber<DMVideosPageBean>(mRxErrorHandler) {
                    @Override
                    public void onNext(DMVideosPageBean dmVideosPageBean) {
                        LogUtils.v("getChannelVideoByID", "onNext");
                        if (pullToRefresh) {
                            mDMVideoList.clear();
                        }

                        if (mDMVideoList.contains(dmVideosPageBean.list)) {
                            return;
                        }

                        mIsLoadedAll = !dmVideosPageBean.has_more;
                        if (dmVideosPageBean.has_more) {
                            pagenum = dmVideosPageBean.page + 1;
                        }

                        int preEndIndex = mDMVideoList.size();
                        if (dmVideosPageBean.list != null) {
                            mDMVideoList.addAll(dmVideosPageBean.list);
                        }

                        if (mListAdapter == null) {
                            mListAdapter = new DMListRecyclerAdapter(mDMVideoList, mActivity);
                            mListAdapter.setOnItemClickListener(DailyMotionFragment.this);
                            viewDelegate.setAdapter(mListAdapter);
                        }

                        if (mPaginate == null && mDMVideoList.size() > 0) {
                            mPaginate = Paginate.with(((RecyclerView) viewDelegate.get(R.id.dm_recyclerview)), DailyMotionFragment.this)
                                    .setLoadingTriggerThreshold(0)
                                    .build();
                            mPaginate.setHasMoreDataToLoad(false);
                        }

                        if (pullToRefresh) {
                            mListAdapter.notifyDataSetChanged();
                        }else {
                            if (mListAdapter.isAddAdView()) {
                                mListAdapter.notifyItemRangeInserted(preEndIndex + 1, dmVideosPageBean.list.size());
                            } else {
                                mListAdapter.notifyItemRangeInserted(preEndIndex, dmVideosPageBean.list.size());
                            }
                        }
                    }
                });
    }

    private boolean isEvictCache = true;

    private void getVideoData(final boolean pullToRefresh) {
        isEvictCache = true; //不使用缓存
        if (pullToRefresh && mIsFirst) {
            mIsFirst = false;
            isEvictCache = false;
        }

        LogUtils.v("getVideoData", "isEvictCache " + isEvictCache);

        mDaiyMotionModel.getVideos(APIConstant.DailyMontion.sWatchVideosMap, isEvictCache, pagenum)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(2, 1))
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
                            if (!isEvictCache) {
                                UIThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (viewDelegate != null) {
                                            viewDelegate.hideLoading();
                                        }
                                    }
                                }, 1000);
                            } else {
                                viewDelegate.hideLoading();
                            }
                        } else {
                            mIsLoadingMore = false;
                        }
                    }
                }).subscribe(new ErrorHandleSubscriber<DMVideosPageBean>(mRxErrorHandler) {
            @Override
            public void onNext(DMVideosPageBean dmVideosPageBean) {
                LogUtils.v("getVideoData", "onNext");
                if (pullToRefresh) {
                    mDMVideoList.clear();
                }

                if (mDMVideoList.contains(dmVideosPageBean.list)) {
                    return;
                }

                mIsLoadedAll = !dmVideosPageBean.has_more;
                if (dmVideosPageBean.has_more) {
                    pagenum = dmVideosPageBean.page + 1;
                }

                int preEndIndex = mDMVideoList.size();
                if (dmVideosPageBean.list != null) {
                    mDMVideoList.addAll(dmVideosPageBean.list);
                }

                if (mAdapter == null) {
                    mAdapter = new DMMainRecyclerAdapter(mDMVideoList, mActivity);
                    mAdapter.setOnItemClickListener(DailyMotionFragment.this);
                    viewDelegate.setAdapter(mAdapter);
                }

                if (mPaginate == null && mDMVideoList.size() > 0) {
                    mPaginate = Paginate.with(((RecyclerView) viewDelegate.get(R.id.dm_recyclerview)), DailyMotionFragment.this)
                            .setLoadingTriggerThreshold(0)
                            .build();
                    mPaginate.setHasMoreDataToLoad(false);
                }

                if (mDMVideoList.size() > 0) {
                    loadingFrameLayout.showNormal();
                } else {
                    loadingFrameLayout.showDataNull();
                }

                if (pullToRefresh) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    if (mAdapter.isAddAdView()) {
                        mAdapter.notifyItemRangeInserted(preEndIndex + 1, dmVideosPageBean.list.size());
                    } else {
                        mAdapter.notifyItemRangeInserted(preEndIndex, dmVideosPageBean.list.size());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                LogUtils.v("getVideoData", "onErroronError");
                if (mDMVideoList.size() == 0) {
                    loadingFrameLayout.showError();
                }
            }
        });
    }

    @Override
    public void onRefresh() { //下拉刷新当前页
        if (mInHome) {
            pagenum = 1;
            getVideoData(true);
        } else {
            getChannelVideoByID(mCurChannelID, pagenum, true, true);
        }
    }

    @Override
    public void onLoadMore() { //加载更多
        LogUtils.v("onLoadMore " + mIsLoadingMore);
        if (mInHome) {
            getVideoData(false);
        } else {
            getChannelVideoByID(mCurChannelID, pagenum, true, false);
        }
    }

    @Override
    public boolean isLoading() {
        LogUtils.v("isLoading " + mIsLoadingMore);
        return mIsLoadingMore;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return mIsLoadedAll;
    }

    @Override
    public void onItemClick(View view, int viewType, DMVideoBean data, int position) {
        LogUtils.v("onItemClick", " video id " + data.id);
        DaiyMotionPlayerActivity.launch((Activity) view.getContext(), data);
    }
}
