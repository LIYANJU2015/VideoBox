package com.videobox.search;

import android.app.Activity;
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
import com.commonlibs.util.UIThreadHelper;
import com.paginate.Paginate;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.videobox.R;
import com.videobox.model.APIConstant;
import com.videobox.model.dailymotion.DaiyMotionModel;
import com.videobox.model.dailymotion.entity.DMVideoBean;
import com.videobox.model.dailymotion.entity.DMVideosPageBean;
import com.videobox.player.dailymotion.DaiyMotionPlayerActivity;
import com.videobox.util.FirebaseAnalyticsUtil;
import com.videobox.view.adapter.BaseRecyclerViewAdapter;
import com.videobox.view.adapter.DMListRecyclerAdapter;
import com.videobox.view.widget.LoadingFrameLayout;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by liyanju on 2017/5/6.
 */

public class DailyMotionSearchFragment extends BaseFragment implements Paginate.Callbacks,
        BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<DMVideoBean> {

    private ArrayList<DMVideoBean> mVideoList = new ArrayList<>();
    private DMListRecyclerAdapter mRecyclerAdapter;
    private DaiyMotionModel mDaiyMotionModel;
    private int pagenum = 1;
    private boolean mIsLoadingMore = false;
    private boolean isLoadedAll = false;

    private Paginate mPaginate;

    private String mSearchStr;

    private RecyclerView searchRecyclerView;

    private LoadingFrameLayout loadingFrameLayout;

    private SearchActivity searchActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        searchActivity = (SearchActivity)activity;
    }

    @Override
    public void onLoadMore() {
        searchDMVideo(false);
    }

    @Override
    public boolean isLoading() {
        LogUtils.v("isLoading ", "mIsLoadingMore " + mIsLoadingMore);
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
        searchRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        searchRecyclerView.setHasFixedSize(true);
        mRecyclerAdapter = new DMListRecyclerAdapter(mVideoList, mActivity);
        mRecyclerAdapter.setOnItemClickListener(this);
        searchRecyclerView.setAdapter(mRecyclerAdapter);

        loadingFrameLayout = (LoadingFrameLayout)view.findViewById(R.id.loading_frame);

        if (getAppComponent() != null) {
            mDaiyMotionModel = new DaiyMotionModel(getAppComponent().repositoryManager());
        }
        return view;
    }

    @Override
    public void onItemClick(View view, int viewType, DMVideoBean data, int position) {
        DaiyMotionPlayerActivity.launch((Activity) view.getContext(), data);
        FirebaseAnalyticsUtil.of().logEventSearchClick("search DM title " + data.title);
    }

    public void gotoSearchVideo(String search) {
        mSearchStr = search;
        pagenum = 1;
        mIsLoadingMore = false;
        isLoadedAll = false;
        mRecyclerAdapter.clearAdapter();
        if (mPaginate != null) {
            mPaginate.unbind();
            mPaginate = null;
        }

        loadingFrameLayout.smoothToshow();
        searchDMVideo(true);
    }

    private void searchDMVideo(final boolean isFirst) {
        LogUtils.v("searchDMVideo", " searchStr " + mSearchStr);
        if (!isAdded()) {
            return;
        }
        mDaiyMotionModel.getSearchVideo(APIConstant.DailyMontion.sSearchVideosMap, mSearchStr, pagenum, true)
                .subscribeOn(Schedulers.io())
                .compose(this.<DMVideosPageBean>bindUntilEvent(FragmentEvent.PAUSE))
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("searchDMVideo doOnSubscribe call");
                        if (!isFirst) {
                            mIsLoadingMore = true;
                        } else {
                            loadingFrameLayout.smoothToshow();
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("searchDMVideo", "doAfterTerminate ");
                        mIsLoadingMore = false;
                        if (isFirst) {
                            loadingFrameLayout.smoothToHide();
                        }
                        loadingFrameLayout.smoothToHide();
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

                        if (mVideoList.size() > 0 && mPaginate == null) {
                            mPaginate = Paginate.with(searchRecyclerView, DailyMotionSearchFragment.this)
                                    .setLoadingTriggerThreshold(0)
                                    .build();
                            mPaginate.setHasMoreDataToLoad(false);
                        }

                        if (isFirst && mVideoList.size() == 0) {
                            loadingFrameLayout.showDataNull();
                        }

                        setDailyMotionTabBadge(dmVideosPageBean.total);
                    }

                    private void setDailyMotionTabBadge(int num){
                        if (isFirst && !searchActivity.isFinishing()) {
                            searchActivity.setDailyMotionTabBadge(num);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        loadingFrameLayout.showError();
                        UIThreadHelper.getInstance().runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                setDailyMotionTabBadge(0);
                            }
                        });
                    }
                });
    }
}
