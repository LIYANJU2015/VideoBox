package com.videobox.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.commonlibs.rxerrorhandler.core.RxErrorHandler;
import com.commonlibs.rxerrorhandler.handler.ErrorHandleSubscriber;
import com.commonlibs.rxerrorhandler.handler.RetryWithDelay;
import com.commonlibs.themvp.presenter.FragmentPresenter;
import com.commonlibs.util.LogUtils;
import com.commonlibs.widget.imageloader.ImageLoader;
import com.paginate.Paginate;
import com.videobox.R;
import com.videobox.model.APIConstant;
import com.videobox.model.dailymotion.DaiyMotionModel;
import com.videobox.model.dailymotion.entity.DMVideoBean;
import com.videobox.model.dailymotion.entity.DMVideosPageBean;
import com.videobox.view.adapter.DailyMotionRecyclerAdapter;
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
        SwipeRefreshLayout.OnRefreshListener, Paginate.Callbacks {

    private static final String TAG = DailyMotionFragment.class.getSimpleName();

    private DaiyMotionModel mDaiyMotionModel;
    private RxErrorHandler mRxErrorHandler;

    private ImageView mHeaderIV;

    private boolean mIsLoadingMore;

    private boolean mIsFirst;

    private ArrayList<DMVideoBean> mDMVideoList = new ArrayList<>();

    private DailyMotionRecyclerAdapter mAdapter;

    private ImageLoader mImageLoader;

    private Context mContext;

    private int pagenum = 1;

    private Paginate mPaginate;

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
        mPaginate = Paginate.with(((RecyclerView) viewDelegate.get(R.id.dm_recyclerview)), this)
                .setLoadingTriggerThreshold(0)
                .build();
        mPaginate.setHasMoreDataToLoad(false);
        getVideoData(true);
    }

    public DailyMotionRecyclerAdapter getDailyMotionRecyclerAdapter() {
        mAdapter = new DailyMotionRecyclerAdapter(mDMVideoList, getActivity());
        return mAdapter;
    }

    private void getVideoData(final boolean pullToRefresh) {
        boolean isEvictCache = true; //不使用缓存
        if (pullToRefresh && mIsFirst) {
            mIsFirst = true;
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

                        if (dmVideosPageBean.has_more) {
                            pagenum = dmVideosPageBean.page + 1;
                        }

                        int preEndIndex = mDMVideoList.size();
                        if (dmVideosPageBean.list != null) {
                            mDMVideoList.addAll(dmVideosPageBean.list);
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
        getVideoData(true);
    }

    @Override
    public void onLoadMore() { //加载更多
        LogUtils.v("onLoadMore " + mIsLoadingMore);
        getVideoData(false);
    }

    @Override
    public boolean isLoading() {
        LogUtils.v("isLoading " + mIsLoadingMore);
        return mIsLoadingMore;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return false;
    }
}
