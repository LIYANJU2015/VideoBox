package com.videobox.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.commonlibs.base.BaseFragment;
import com.commonlibs.rxerrorhandler.handler.ErrorHandleSubscriber;
import com.commonlibs.rxerrorhandler.handler.RetryWithDelay;
import com.commonlibs.util.LogUtils;
import com.paginate.Paginate;
import com.videobox.R;
import com.videobox.model.APIConstant;
import com.videobox.model.dailymotion.DaiyMotionModel;
import com.videobox.model.dailymotion.entity.DMVideoBean;
import com.videobox.model.dailymotion.entity.DMVideosPageBean;
import com.videobox.view.adapter.DMListRecyclerAdapter;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by liyanju on 2017/5/6.
 */

public class DailyMotionSearchFragment extends BaseFragment implements Paginate.Callbacks{

    private ArrayList<DMVideoBean> mVideoList = new ArrayList<>();
    private DMListRecyclerAdapter mRecyclerAdapter;
    private DaiyMotionModel mDaiyMotionModel;
    private int pagenum = 1;
    private boolean mIsLoadingMore = false;
    private boolean isLoadedAll = false;
    private Paginate mPaginate;

    private String mSearchStr;

    @Override
    public void onLoadMore() {
        searchDMVideo();
    }

    @Override
    public boolean isLoading() {
        return mIsLoadingMore;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return isLoadedAll;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_layout, null);
        RecyclerView searchRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        searchRecyclerView.setHasFixedSize(true);
        mRecyclerAdapter = new DMListRecyclerAdapter(mVideoList, mActivity);
        searchRecyclerView.setAdapter(mRecyclerAdapter);

        mPaginate = Paginate.with(searchRecyclerView, this)
                .setLoadingTriggerThreshold(0)
                .build();
        mPaginate.setHasMoreDataToLoad(false);

        mDaiyMotionModel = new DaiyMotionModel(getAppComponent().repositoryManager());
        return view;
    }

    public void gotoSearchDMVideo(String search) {
        mSearchStr = search;
        pagenum = 1;
        mIsLoadingMore = false;
        isLoadedAll = false;
        mRecyclerAdapter.clearAdapter();
        searchDMVideo();
    }

    private void searchDMVideo() {
        LogUtils.v("searchDMVideo", " searchStr " + mSearchStr);
        if (!isAdded()) {
            return;
        }
        mDaiyMotionModel.getSearchVideo(APIConstant.DailyMontion.sSearchVideosMap, mSearchStr, pagenum, true)
                .subscribeOn(Schedulers.io())
                .compose(this.<DMVideosPageBean>bindToLifecycle())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("searchDMVideo doOnSubscribe call");
                        mIsLoadingMore = true;
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("searchDMVideo", "doAfterTerminate ");
                        mIsLoadingMore = false;
                    }
                })
                .subscribe(new ErrorHandleSubscriber<DMVideosPageBean>(getAppComponent().rxErrorHandler()) {
                    @Override
                    public void onNext(DMVideosPageBean dmVideosPageBean) {
                        isLoadedAll = !dmVideosPageBean.has_more;
                        LogUtils.v("searchDMVideo", "onNext " + isLoadedAll);
                        if (dmVideosPageBean.has_more) {
                            pagenum = dmVideosPageBean.page + 1;
                        }

                        int preEndIndex = mVideoList.size();
                        if (dmVideosPageBean.list != null) {
                            mVideoList.addAll(dmVideosPageBean.list);
                        }

                        mRecyclerAdapter.notifyItemRangeInserted(preEndIndex, dmVideosPageBean.list.size());
                    }
                });
    }
}
