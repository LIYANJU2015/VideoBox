package com.videobox.player.youtube;

/**
 * Created by liyanju on 2017/5/12.
 */

public interface IPlayCallBack {

    void onCanPlayList(String playlistId, int startIndex, int timeMillis);

    /**
     * 播放列表视频播放完毕
     */
    void onPlaylistEnded();

    void onCanPlayVideo(String videoId, int timeMills);

    /**
     * 关联视频播放完毕
     */
    void onPlayRelateVideoEnd();
}
