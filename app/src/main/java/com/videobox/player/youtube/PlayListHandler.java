package com.videobox.player.youtube;

import android.content.Context;
import android.view.View;

import com.commonlibs.rxerrorhandler.handler.ErrorHandleSubscriber;
import com.commonlibs.rxerrorhandler.handler.RetryWithDelay;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.StringUtils;
import com.google.android.youtube.player.YouTubePlayer;
import com.videobox.AppAplication;
import com.videobox.model.APIConstant;
import com.videobox.model.bean.PlayRecordBean;
import com.videobox.model.bean.YouTubePlayerItem;
import com.videobox.model.db.VideoBoxContract;
import com.videobox.model.youtube.YouTuBeModel;
import com.videobox.model.youtube.entity.YTBVideoPageBean;
import com.videobox.view.adapter.BaseRecyclerViewAdapter;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liyanju on 2017/5/11.
 */

public class PlayListHandler implements YouTubePlayer.PlaylistEventListener,
        BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<YTBVideoPageBean.YouTubeVideo> {

    private String mPlaylistId;

    private YouTuBeModel mYouTuBeModel;

    private String pageToken;

    private boolean mIsLoadingMore;
    private boolean isLoadedAll;

    private Context mContext;

    private ArrayList<YTBVideoPageBean.YouTubeVideo> mVideoList;

    private String mPlayRecordVid;
    private long mCurPlayTime;
    private int mCurIndex;

    private IPlayCallBack mIPlayCallBack;

    private IPlayItemUpdate mItemUpdate;

    private YouTubePlayerItem.IntroduceVideo mCurPlayVideo;

    public PlayListHandler(Context context, String playlistId, YouTuBeModel youTuBeModel,
                           ArrayList<YTBVideoPageBean.YouTubeVideo> videolist, IPlayItemUpdate itemUpdate,
                           YouTubePlayerItem.IntroduceVideo curPlayVideo) {
        mContext = context;
        mPlaylistId = playlistId;
        mYouTuBeModel = youTuBeModel;
        mVideoList = videolist;
        mItemUpdate = itemUpdate;
        mCurPlayVideo = curPlayVideo;
    }

    public void starFirstPlay() {
        mCurIndex = 0;
        mIPlayCallBack.onCanPlayList(mPlaylistId, 0, 0, mVideoList.get(0).snippet.resourceId.videoId);
        updateListPlayStatus();
    }

    public void start(IPlayCallBack iPlayCallBack) {
        mIPlayCallBack = iPlayCallBack;

        PlayRecordBean playRecordBean = VideoBoxContract.PlayRecord
                .getPlayRecordTimeByPlaylistId(mContext, mPlaylistId, PlayRecordBean.YOUTUBE_TYPE);
        if (playRecordBean == null) {
            requestPlaylistItem(mPlaylistId, false);
            iPlayCallBack.onCanPlayList(mPlaylistId, 0, 0, null);
        } else {
            mPlayRecordVid = playRecordBean.vid;
            mCurPlayTime = playRecordBean.playTime;
            requestPlaylistItem(mPlaylistId, true);
        }
    }

    public YTBVideoPageBean.YouTubeVideo getCurPlayVideo() {
        if (mVideoList.size() > mCurIndex) {
            return mVideoList.get(mCurIndex);
        }
        return null;
    }

    private void requestPlaylistItem(final String playlistId, final boolean isPlayItem) {
        mYouTuBeModel.getPlaylistItems(APIConstant.YouTube.sPlayItemMap, playlistId, true, pageToken)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorHandleSubscriber<YTBVideoPageBean>(AppAplication.sRxErrorHandler) {
                    @Override
                    public void onNext(YTBVideoPageBean dmVideosPageBean) {
                        isLoadedAll = StringUtils.isEmpty(dmVideosPageBean.nextPageToken);
                        LogUtils.v("requestPlaylistItem", "onNext " + isLoadedAll);
                        if (!isLoadedAll) {
                            pageToken = dmVideosPageBean.nextPageToken;
                        }

                        if (isPlayItem && mCurIndex == 0) {
                            for (int i = 0; i < dmVideosPageBean.items.size(); i++) {
                                YTBVideoPageBean.YouTubeVideo video = dmVideosPageBean.items.get(i);
                                if (mPlayRecordVid.equals(video.snippet.resourceId.videoId)) {
                                    mCurIndex = i;
                                    mIPlayCallBack.onCanPlayList(mPlaylistId, mCurIndex, (int) mCurPlayTime,
                                            video.snippet.resourceId.videoId);
                                    video.isPlaying = true;
                                    mCurPlayVideo.title = video.snippet.title;
                                    mCurPlayVideo.description = video.snippet.description;
                                } else {
                                    video.isPlaying = false;
                                }
                            }
                        } else {
                            if (mVideoList.size() == 0) {
                                YTBVideoPageBean.YouTubeVideo video = dmVideosPageBean.items.get(0);
                                video.isPlaying = true;
                                mCurPlayVideo.title = video.snippet.title;
                                mCurPlayVideo.description = video.snippet.description;
                                ((YouTubePlayerManager) mIPlayCallBack)
                                        .mRelateVideoHandler.requestRelatedVideo(video.snippet.resourceId.videoId);
                            }
                        }

                        if (dmVideosPageBean.items != null) {
                            mVideoList.addAll(dmVideosPageBean.items);
                        }

                        if (!isLoadedAll) {
                            requestPlaylistItem(mPlaylistId, isPlayItem);
                        } else {
                            mItemUpdate.onUpateAll();
                        }
                    }
                });
    }

    private void updateListPlayStatus() {
        LogUtils.v("updateListPlayStatus", " mCurIndex " + mCurIndex, " mVideoList size " + mVideoList.size());
        for (int i = 0; i < mVideoList.size(); i++) {
            YTBVideoPageBean.YouTubeVideo video = mVideoList.get(i);
            if (mCurIndex == i) {
                video.isPlaying = true;
                mCurPlayVideo.title = video.snippet.title;
                mCurPlayVideo.description = video.snippet.description;
                mItemUpdate.onUpdateItems(1);
                mItemUpdate.onUpdatePlayListItmes(mCurIndex);
            } else if (video.isPlaying) {
                video.isPlaying = false;
                mItemUpdate.onUpdatePlayListItmes(i);
            }
        }
    }

    public void cancelPlayingStatus() {
        for (int i = 0; i < mVideoList.size(); i++) {
            YTBVideoPageBean.YouTubeVideo video = mVideoList.get(i);
            if (i == mCurIndex) {
                video.isPlaying = false;
            }
        }
        mItemUpdate.onUpdatePlayListItmes(mCurIndex);
        mCurIndex = 0;
    }

    @Override
    public void onPrevious() {
        mCurIndex--;
        updateListPlayStatus();
    }

    @Override
    public void onNext() {
        mCurIndex++;
        if (mCurIndex < mVideoList.size()) {
            updateListPlayStatus();
        } else {
            mIPlayCallBack.onPlaylistEnded();
        }
    }

    @Override
    public void onPlaylistEnded() {
    }


    @Override
    public void onItemClick(View view, int viewType, YTBVideoPageBean.YouTubeVideo data, int position) {
        mCurIndex = position;
        updateListPlayStatus();
        mIPlayCallBack.onCanPlayList(mPlaylistId, position, 0,
                mVideoList.get(position).snippet.resourceId.videoId);
    }
}
