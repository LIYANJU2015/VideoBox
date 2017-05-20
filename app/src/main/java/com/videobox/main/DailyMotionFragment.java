package com.videobox.main;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.commonlibs.base.BaseRecyclerViewAdapter;
import com.commonlibs.rxerrorhandler.core.RxErrorHandler;
import com.commonlibs.rxerrorhandler.handler.ErrorHandleSubscriber;
import com.commonlibs.rxerrorhandler.handler.RetryWithDelay;
import com.commonlibs.themvp.presenter.FragmentPresenter;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.StringUtils;
import com.commonlibs.util.UIThreadHelper;
import com.commonlibs.widget.imageloader.ImageLoader;
import com.paginate.Paginate;
import com.videobox.R;
import com.videobox.model.APIConstant;
import com.videobox.model.dailymotion.DaiyMotionModel;
import com.videobox.model.dailymotion.entity.DMVideoBean;
import com.videobox.model.dailymotion.entity.DMVideosPageBean;
import com.videobox.player.dailymotion.DaiyMotionPlayerActivity;
import com.videobox.view.adapter.DMListRecyclerAdapter;
import com.videobox.view.adapter.DMMainRecyclerAdapter;
import com.videobox.view.delegate.Contract;
import com.videobox.view.delegate.DailyMotionDelegate;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static android.media.CamcorderProfile.get;


/**
 * Created by liyanju on 2017/4/29.
 */

public class DailyMotionFragment extends FragmentPresenter<DailyMotionDelegate> implements
        SwipeRefreshLayout.OnRefreshListener, Paginate.Callbacks,Contract.IVideoListFragment,
        BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<DMVideoBean> {

    private static final String TAG = DailyMotionFragment.class.getSimpleName();

    private DaiyMotionModel mDaiyMotionModel;
    private RxErrorHandler mRxErrorHandler;

    private ImageView mHeaderIV;

    private boolean mIsLoadingMore;

    private boolean mIsFirst;

    private ArrayList<DMVideoBean> mDMVideoList = new ArrayList<>();

    private DMMainRecyclerAdapter mAdapter;
    private DMListRecyclerAdapter mListAdapter;


    private ImageLoader mImageLoader;

    private Context mContext;

    private int pagenum = 1;

    private Paginate mPaginate;

    private boolean mInHome = true;

    private String mCurChannelID;

    private boolean mIsLoadedAll = false;

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
        UIThreadHelper.getInstance().runViewUIThread(getView(), new Runnable() {
            @Override
            public void run() {
                getVideoData(true);
            }
        });
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
                .compose(this.<DMVideosPageBean>bindToLifecycle())
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

                        if (pullToRefresh)
                            mListAdapter.notifyDataSetChanged();
                        else
                            mListAdapter.notifyItemRangeInserted(preEndIndex, dmVideosPageBean.list.size());
                    }
                });
    }

    private void getVideoData(final boolean pullToRefresh) {
        boolean isEvictCache = true; //不使用缓存
        if (pullToRefresh && mIsFirst) {
            mIsFirst = false;
            isEvictCache = false;
        }

        mDaiyMotionModel.getVideos(APIConstant.DailyMontion.sWatchVideosMap, isEvictCache, pagenum)
                .subscribeOn(Schedulers.io())
                .compose(this.<DMVideosPageBean>bindToLifecycle())
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
                .subscribe(new ErrorHandleSubscriber<DMVideosPageBean>(mRxErrorHandler) {
                    @Override
                    public void onNext(DMVideosPageBean dmVideosPageBean) {
                        LogUtils.v("getVideoData", "onNext");
                        if (pullToRefresh) {
                            mDMVideoList.clear();
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

                        if (pullToRefresh)
                            mAdapter.notifyDataSetChanged();
                        else
                            mAdapter.notifyItemRangeInserted(preEndIndex, dmVideosPageBean.list.size());
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
        DaiyMotionPlayerActivity.launch(data);
    }
}
