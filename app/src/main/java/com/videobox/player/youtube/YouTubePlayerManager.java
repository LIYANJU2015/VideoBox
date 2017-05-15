package com.videobox.player.youtube;

import com.commonlibs.util.LogUtils;
import com.commonlibs.util.StringUtils;
import com.google.android.youtube.player.YouTubePlayer;
import com.videobox.AppAplication;
import com.videobox.model.bean.PlayRecordBean;
import com.videobox.model.db.VideoBoxContract;
import com.videobox.model.youtube.entity.YTBVideoPageBean;

/**
 * Created by liyanju on 2017/5/12.
 */
public class YouTubePlayerManager implements IPlayCallBack, YouTubePlayer.PlayerStateChangeListener{

    private YouTubePlayer mPlayer;

    private PlayListHandler mPlayerListHander;

    protected RelateVideoHandler mRelateVideoHandler;

    public YouTubePlayerManager(YouTubePlayer youTubePlayer,
                                PlayListHandler playListHandler,
                                RelateVideoHandler relateVideoHandler) {
        mPlayer = youTubePlayer;
        mPlayerListHander = playListHandler;
        mRelateVideoHandler = relateVideoHandler;
        if (playListHandler != null) {
            mPlayer.setPlaylistEventListener(playListHandler);
            playListHandler.start(this);
            mRelateVideoHandler.start(this, true);
        } else {
            mRelateVideoHandler.start(this, false);
        }
        mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
    }

    public boolean isCurInPlaylist() {
        return isPlayinglist;
    }

    public int getCurrentTimeMillis() {
        return mPlayer.getCurrentTimeMillis();
    }

    public int getDurationMillis() {
        return mPlayer.getDurationMillis();
    }

    public YTBVideoPageBean.YouTubeVideo getCurPlayVideo() {
        if (mPlayerListHander != null) {
            return mPlayerListHander.getCurPlayVideo();
        }
        return null;
    }

    private boolean isPlayinglist;

    @Override
    public void onCanPlayList(String playlistId, int startIndex, int timeMillis, String vid) {
        try {
            mPlayer.loadPlaylist(playlistId, startIndex, timeMillis);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        isPlayinglist = true;

        if (!StringUtils.isEmpty(vid)) {
            mRelateVideoHandler.requestRelatedVideo(vid);
        }

        if (mRelateVideoHandler != null) {
            mRelateVideoHandler.cancelPlayingStatus();
        }
    }

    @Override
    public void onCanPlayVideo(String videoId, int timeMills) {
        isPlayinglist = false;
        try {
            mPlayer.loadVideo(videoId, timeMills);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        if (mPlayerListHander != null) {
            mPlayerListHander.cancelPlayingStatus();
        }
    }

    /**
     * 播放列表播放完成
     */
    @Override
    public void onPlaylistEnded() {
        LogUtils.v("onPlaylistEnded", "isPlayinglist " + isPlayinglist);
        if (isPlayinglist) {
            isPlayinglist = false;
            if (mPlayerListHander != null) {
                mPlayerListHander.cancelPlayingStatus();
            }
            playNextRelateVideo();
        }
    }

    /**
     * 播放相关完成
     */
    @Override
    public void onPlayRelateVideoEnd() {
        if (mPlayerListHander != null) {
            mPlayerListHander.starFirstPlay();
            isPlayinglist = true;
        }
    }

    private void playNextRelateVideo() {
        String vid = mRelateVideoHandler.getNextVideoId();
        LogUtils.v("playNextRelateVideo", "vid " + vid);
        if (!StringUtils.isEmpty(vid)) {
            long time = VideoBoxContract.PlayRecord.getPlayRecordTimeByVid(AppAplication.getContext(), vid, PlayRecordBean.YOUTUBE_TYPE);
            mPlayer.loadVideo(vid, (int) time);
        }
    }

    @Override
    public void onLoading() {
    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {
        LogUtils.v("onVideoEnded", "isPlayinglist " + isPlayinglist);
        if (!isPlayinglist) {
            String vid = mRelateVideoHandler.getNextVideoId();
            LogUtils.v("onVideoEnded", " vid " + vid);
            if (!StringUtils.isEmpty(vid)) {
                long time = VideoBoxContract.PlayRecord.getPlayRecordTimeByVid(AppAplication.getContext(), vid, PlayRecordBean.YOUTUBE_TYPE);
                onCanPlayVideo(vid, (int) time);
            } else {
                onPlayRelateVideoEnd();
            }
        }
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        if (errorReason == YouTubePlayer.ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) {
            // When this error occurs the player is released and can no longer be used.
            mPlayer = null;
        }
    }
}
