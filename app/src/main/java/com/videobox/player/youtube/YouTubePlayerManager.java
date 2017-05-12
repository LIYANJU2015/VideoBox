package com.videobox.player.youtube;

import com.google.android.youtube.player.YouTubePlayer;

/**
 * Created by liyanju on 2017/5/12.
 */

public class YouTubePlayerManager implements IPlayCallBack{

    private YouTubePlayer mPlayer;

    public YouTubePlayerManager(YouTubePlayer youTubePlayer, PlayListHandler playListHandler) {
        mPlayer = youTubePlayer;
        if (playListHandler != null) {
            mPlayer.setPlaylistEventListener(playListHandler);
            playListHandler.start(this);
        }
    }

    @Override
    public void onCanPlayList(String playlistId, int startIndex, int timeMillis) {
        mPlayer.loadPlaylist(playlistId, startIndex, timeMillis);
    }

    @Override
    public void onCanPlayVideo(String videoId, int timeMills) {
        mPlayer.loadVideo(videoId, timeMills);
    }
}
