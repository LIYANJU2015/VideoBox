package com.videobox.player.youtube;

import android.os.Message;

import com.commonlibs.util.HandlerUtils;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.StringUtils;
import com.google.android.youtube.player.YouTubePlayer;
import com.videobox.AppAplication;
import com.videobox.model.bean.PlayRecordBean;
import com.videobox.model.db.VideoBoxContract;
import com.videobox.model.youtube.entity.YTBVideoPageBean;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by liyanju on 2017/5/12.
 */
public class YouTubePlayerManager implements IPlayCallBack,
        YouTubePlayer.PlayerStateChangeListener, YouTubePlayer.PlaybackEventListener,
        HandlerUtils.OnReceiveMessageListener{

    private YouTubePlayer mPlayer;

    private PlayListHandler mPlayerListHander;

    protected RelateVideoHandler mRelateVideoHandler;

    private MaterialProgressBar mPlayProgressBar;

    private HandlerUtils.HandlerHolder mMainHandler;

    public YouTubePlayerManager(YouTubePlayer youTubePlayer,
                                PlayListHandler playListHandler,
                                RelateVideoHandler relateVideoHandler, MaterialProgressBar playProgressBar) {
        mPlayer = youTubePlayer;
        mPlayerListHander = playListHandler;
        mRelateVideoHandler = relateVideoHandler;
        mPlayProgressBar = playProgressBar;
        if (playListHandler != null) {
            mPlayer.setPlaylistEventListener(playListHandler);
            playListHandler.start(this);
            mRelateVideoHandler.start(this, true);
        } else {
            mRelateVideoHandler.start(this, false);
        }
        mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        mPlayer.setPlayerStateChangeListener(this);
        mPlayer.setPlaybackEventListener(this);

        mMainHandler = new HandlerUtils.HandlerHolder(this);
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

        if (mRelateVideoHandler != null) {
            return mRelateVideoHandler.getCurPlayVideo();
        }

        return null;
    }

    private boolean isPlayinglist;

    @Override
    public void onCanPlayList(String playlistId, int startIndex, int timeMillis, String vid) {
        try {
            mPlayer.loadPlaylist(playlistId, startIndex, timeMillis);
        } catch (Throwable e) {
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
        } catch (Throwable e) {
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
        LogUtils.v("PlayState", " onLoaded getDurationMillis " + mPlayer.getDurationMillis());
        mPlayProgressBar.setMax(mPlayer.getDurationMillis());
    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onPlaying() {
        LogUtils.v("PlayState", "onPlaying " + mPlayer.getCurrentTimeMillis());
        mPlayProgressBar.setProgress(mPlayer.getCurrentTimeMillis());
    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onStopped() {
        LogUtils.v("PlayState", "onStopped " + mPlayer.getCurrentTimeMillis());
    }

    @Override
    public void onBuffering(boolean b) {

    }

    @Override
    public void onSeekTo(int i) {

    }

    @Override
    public void onVideoStarted() {
        LogUtils.v("PlayState", " onVideoStarted ");
        startUpdateProgress();
    }

    @Override
    public void onVideoEnded() {
        LogUtils.v("PlayState", " onVideoEnded isPlayinglist " + isPlayinglist);
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

        stopUpdateProgress();

        mPlayProgressBar.setMax(0);
        mPlayProgressBar.setProgress(0);
        LogUtils.v("PlayState", " onVideoEnded getDurationMillis " + mPlayer.getDurationMillis() + "getCurrentTimeMillis " + mPlayer.getCurrentTimeMillis());
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        if (errorReason == YouTubePlayer.ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) {
            // When this error occurs the player is released and can no longer be used.
            mPlayer = null;
        }
    }

    @Override
    public void handlerMessage(Message msg) {
        if (mPlayer == null) {
            return;
        }
        mPlayProgressBar.setProgress(mPlayer.getCurrentTimeMillis());
        if (!isStopUpdate) {
            mMainHandler.sendEmptyMessageDelayed(0, 500);
        }
    }

    private boolean isStopUpdate = false;

    public void onDestory() {
        stopUpdateProgress();
    }

    public void startUpdateProgress() {
        isStopUpdate = false;
        mMainHandler.sendEmptyMessageDelayed(0, 500);
    }

    public void stopUpdateProgress() {
        isStopUpdate = true;
        mMainHandler.removeCallbacksAndMessages(null);
    }
}
