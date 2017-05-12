package com.videobox.player.dailymotion;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.commonlibs.base.BaseFragment;
import com.commonlibs.base.BaseRecyclerViewAdapter;
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
import com.videobox.view.delegate.Contract;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by liyanju on 2017/5/5.
 */

public class RelatedFragment extends BaseFragment<Contract.DMPlayerHost> implements Paginate.Callbacks, BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<DMVideoBean> {

    private ArrayList<DMVideoBean> mVideoList = new ArrayList<>();
    private DMListRecyclerAdapter mRecyclerAdapter;
    private RecyclerView mRelatedRecyclerView;

    private DaiyMotionModel mDaiyMotionModel;

    private int pagenum = 1;
    private boolean mIsLoadingMore;

    private Paginate mPaginate;

    private boolean isLoadedAll = false; //是否已经全部加载完毕

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_layout, null);

        mRelatedRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerAdapter = new DMListRecyclerAdapter(mVideoList, getActivity());
        mRelatedRecyclerView.setHasFixedSize(true);
        mRelatedRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRelatedRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.setOnItemClickListener(this);

        mPaginate = Paginate.with(mRelatedRecyclerView, this)
                .setLoadingTriggerThreshold(0)
                .build();
        mPaginate.setHasMoreDataToLoad(false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDaiyMotionModel = new DaiyMotionModel(getAppComponent().repositoryManager());
        initData();
    }

    @Override
    public void onItemClick(View view, int viewType, DMVideoBean data, int position) {
        mHost.getCurrentPlayer().setVideoId(data.id);
        mHost.getCurrentPlayer().load();
        mHost.getCurrentPlayer().play();
    }

    private void initData() {
        mDaiyMotionModel.getVideoRelated(APIConstant.DailyMontion.sRelatedVideosMap,
                mHost.getCurrentVid(), pagenum, true).subscribeOn(Schedulers.io())
                .compose(this.<DMVideosPageBean>bindToLifecycle())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("getVideoRelated doOnSubscribe call");
                        mIsLoadingMore = true;
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("getVideoRelated", "doAfterTerminate ");
                        mIsLoadingMore = false;
                    }
                })
                .subscribe(new ErrorHandleSubscriber<DMVideosPageBean>(getAppComponent().rxErrorHandler()) {
                    @Override
                    public void onNext(DMVideosPageBean dmVideosPageBean) {
                        isLoadedAll = !dmVideosPageBean.has_more;
                        LogUtils.v("getVideoRelated", "onNext " + isLoadedAll);
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

    @Override
    public void onLoadMore() {
        initData();
    }

    @Override
    public boolean isLoading() {
        return mIsLoadingMore;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return isLoadedAll;
    }
}
