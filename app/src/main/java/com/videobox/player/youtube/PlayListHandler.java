package com.videobox.player.youtube;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.commonlibs.base.AppComponent;
import com.commonlibs.rxerrorhandler.handler.ErrorHandleSubscriber;
import com.commonlibs.rxerrorhandler.handler.RetryWithDelay;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.StringUtils;
import com.google.android.youtube.player.YouTubePlayer;
import com.paginate.Paginate;
import com.videobox.AppAplication;
import com.videobox.model.APIConstant;
import com.videobox.model.bean.PlayRecordBean;
import com.videobox.model.db.VideoBoxContract;
import com.videobox.model.youtube.YouTuBeModel;
import com.videobox.model.youtube.entity.YTBVideoPageBean;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static android.media.CamcorderProfile.get;

/**
 * Created by liyanju on 2017/5/11.
 */

public class PlayListHandler implements YouTubePlayer.PlaylistEventListener, Paginate.Callbacks{

    private String mPlaylistId;

    private YouTuBeModel mYouTuBeModel;

    private String pageToken;

    private boolean mIsLoadingMore;
    private boolean isLoadedAll;

    private Paginate mPaginate;

    private Context mContext;

    private AppComponent mAppComponent;

    private ArrayList<YTBVideoPageBean.YouTubeVideo> mVideoList = new ArrayList<>();

    private RecyclerView mRecyclerView;

    private String mCurPlayVid;
    private long mCurPlayTime;
    private int mCurIndex;

    private IPlayCallBack mIPlayCallBack;

    public PlayListHandler(Context context, String playlistId, YouTuBeModel youTuBeModel, RecyclerView recyclerView) {
        mContext = context;
        mAppComponent = ((AppAplication)context.getApplicationContext()).getAppComponent();
        mPlaylistId = playlistId;
        mYouTuBeModel = youTuBeModel;
        mRecyclerView = recyclerView;
    }

    public void start(IPlayCallBack iPlayCallBack) {
        mIPlayCallBack = iPlayCallBack;

        PlayRecordBean playRecordBean = VideoBoxContract.PlayRecord
                .getPlayRecordTimeByPlaylistId(mContext, mPlaylistId, PlayRecordBean.YOUTUBE_TYPE);
        if (playRecordBean == null) {
            requestPlaylistItem(mPlaylistId, false);
            iPlayCallBack.onCanPlayList(mPlaylistId, 0, 0);
        } else {
            mCurPlayVid = playRecordBean.vid;
            mCurPlayTime = playRecordBean.playTime;
            requestPlaylistItem(mPlaylistId, true);
        }
    }

    private void requestPlaylistItem(final String playlistId, final boolean isPlayItem) {
        mYouTuBeModel.getPlaylistItems(APIConstant.YouTube.sPlayItemMap, playlistId, true, pageToken)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("requestPlaylistItem doOnSubscribe call");
                        mIsLoadingMore = true;
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.v("requestPlaylistItem", "doAfterTerminate ");
                        mIsLoadingMore = false;
                    }
                })
                .subscribe(new ErrorHandleSubscriber<YTBVideoPageBean>(mAppComponent.rxErrorHandler()) {
                    @Override
                    public void onNext(YTBVideoPageBean dmVideosPageBean) {
                        isLoadedAll = StringUtils.isEmpty(dmVideosPageBean.nextPageToken);
                        LogUtils.v("requestPlaylistItem", "onNext " + isLoadedAll);
                        if (!isLoadedAll) {
                            pageToken = dmVideosPageBean.nextPageToken;
                        }

                        int preEndIndex = mVideoList.size();
                        if (dmVideosPageBean.items != null) {
                            mVideoList.addAll(dmVideosPageBean.items);
                        }

                        if (mVideoList.size() > 0 && mPaginate == null) {
                            mPaginate = Paginate.with(mRecyclerView, PlayListHandler.this)
                                    .setLoadingTriggerThreshold(0).addLoadingListItem(true)
                                    .setLoadingListItemCreator(new PlayListItemCreator())
                                    .build();
                            mPaginate.setHasMoreDataToLoad(false);
                        }

                        mRecyclerView.getAdapter().notifyItemRangeInserted(preEndIndex, dmVideosPageBean.items.size());

                        if (isPlayItem) {
                            for (int i = 0; i < mVideoList.size(); i++) {
                                YTBVideoPageBean.YouTubeVideo video = mVideoList.get(i);
                                if (mCurPlayVid.equals(video.snippet.resourceId.videoId)) {
                                    mCurIndex = i;
                                    mIPlayCallBack.onCanPlayList(mPlaylistId, mCurIndex, (int) mCurPlayTime);
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    private void updateListPlayStatus() {
        LogUtils.v("updateListPlayStatus", " mCurIndex " + mCurIndex, " mVideoList size "+ mVideoList.size());
        if (mVideoList.size() > mCurIndex && mCurIndex >= 0) {
            YTBVideoPageBean.YouTubeVideo video = mVideoList.get(mCurIndex);
            video.isPlaying = true;
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadMore() {
        requestPlaylistItem(mPlaylistId, false);
    }

    @Override
    public boolean isLoading() {
        return mIsLoadingMore;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return isLoadedAll;
    }

    @Override
    public void onPrevious() {
        mCurIndex--;
        updateListPlayStatus();
    }

    @Override
    public void onNext() {
        mCurIndex++;
        updateListPlayStatus();
    }

    @Override
    public void onPlaylistEnded() {

    }
}
