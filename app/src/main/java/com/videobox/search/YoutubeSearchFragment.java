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
import com.commonlibs.util.StringUtils;
import com.commonlibs.util.UIThreadHelper;
import com.paginate.Paginate;
import com.videobox.R;
import com.videobox.model.APIConstant;
import com.videobox.model.youtube.YouTuBeModel;
import com.videobox.model.youtube.entity.YTBVideoPageBean;
import com.videobox.view.adapter.BaseRecyclerViewAdapter;
import com.videobox.view.adapter.YouTubeListRecyclerAdapter;
import com.videobox.view.widget.LoadingFrameLayout;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by liyanju on 2017/5/6.
 */

public class YoutubeSearchFragment extends BaseFragment implements Paginate.Callbacks,
        BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<YTBVideoPageBean.YouTubeVideo> {

    private YouTuBeModel mYoutubeModel;

    private String pageToken = "";

    private boolean startSearch = false;

    private boolean mIsLoadingMore = false;

    private boolean isLoadedAll = false;

    private ArrayList<YTBVideoPageBean.YouTubeVideo> mVideoList = new ArrayList<>();

    private RecyclerView searchRecyclerView;

    private YouTubeListRecyclerAdapter mRecyclerAdapter;

    private Paginate mPaginate;

    private String searchStr;

    private LoadingFrameLayout loadingFrameLayout;

    private SearchActivity searchActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        searchActivity = (SearchActivity)activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_layout, null);
        searchRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerAdapter = new YouTubeListRecyclerAdapter(mVideoList, mActivity);
        mRecyclerAdapter.setOnItemClickListener(this);
        searchRecyclerView.setAdapter(mRecyclerAdapter);

        loadingFrameLayout =(LoadingFrameLayout)view.findViewById(R.id.loading_frame);

        mYoutubeModel = new YouTuBeModel(getAppComponent().repositoryManager());
        return view;
    }

    @Override
    public void onItemClick(View view, int viewType, YTBVideoPageBean.YouTubeVideo data, int position) {
        data.intoPlayer(searchActivity);
    }

    @Override
    public void onLoadMore() {
        searchVideo();
    }

    @Override
    public boolean isLoading() {
        return mIsLoadingMore;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return isLoadedAll;
    }

    public void gotoSearchVideo(String search) {
        if (mPaginate != null) {
            mPaginate.unbind();
            mPaginate = null;
        }

        startSearch = true;
        searchStr = search;
        mRecyclerAdapter.clearAdapter();
        isLoadedAll = false;
        mIsLoadingMore = false;
        pageToken = "";

        loadingFrameLayout.smoothToshow();

        searchVideo();
    }

    private void searchVideo() {
        mYoutubeModel.getSearchVideos(APIConstant.YouTube.sSearchMap, searchStr, pageToken, true)
                .subscribeOn(Schedulers.io())
                .compose(this.<YTBVideoPageBean>bindToLifecycle())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("searchVideo doOnSubscribe call " + startSearch);
                        if (startSearch) {
                            loadingFrameLayout.smoothToshow();
                        } else {
                            mIsLoadingMore = true;
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("searchVideo", "doAfterTerminate startSearch " + startSearch);
                        if (startSearch) {
                            startSearch = false;
                            loadingFrameLayout.smoothToHide();
                        } else {
                            mIsLoadingMore = false;
                        }
                        loadingFrameLayout.smoothToHide();
                    }
                })
                .subscribe(new ErrorHandleSubscriber<YTBVideoPageBean>(getAppComponent().rxErrorHandler()) {
                    @Override
                    public void onNext(YTBVideoPageBean dmVideosPageBean) {
                        isLoadedAll = StringUtils.isEmpty(dmVideosPageBean.nextPageToken);
                        LogUtils.v("searchVideo", "onNext " + isLoadedAll);
                        if (!StringUtils.isEmpty(dmVideosPageBean.nextPageToken)) {
                            pageToken = dmVideosPageBean.nextPageToken;
                        }

                        int preEndIndex = mVideoList.size();
                        if (dmVideosPageBean.items != null) {
                            mVideoList.addAll(dmVideosPageBean.items);
                        }

                        mRecyclerAdapter.notifyItemRangeInserted(preEndIndex,
                                dmVideosPageBean.items.size());

                        if (mVideoList.size() > 0 && mPaginate == null) {
                            LogUtils.v("searchVideo", "Paginate.with");
                            mPaginate = Paginate.with(searchRecyclerView, YoutubeSearchFragment.this)
                                    .setLoadingTriggerThreshold(0)
                                    .build();
                            mPaginate.setHasMoreDataToLoad(false);
                        }

                        if (startSearch && mVideoList.size() == 0) {
                            loadingFrameLayout.showDataNull();
                        }

                        setYouTubeTabBadge(dmVideosPageBean.pageInfo.totalResults);
                    }

                    private void setYouTubeTabBadge(int num) {
                        if (startSearch && !searchActivity.isFinishing()) {
                            searchActivity.setYouTubeTabBadge(num);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        loadingFrameLayout.showError();
                        UIThreadHelper.getInstance().runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                setYouTubeTabBadge(0);
                            }
                        });
                    }
                });
    }
}
