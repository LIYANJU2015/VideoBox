package com.videobox.player.youtube;

/**
 * Created by liyanju on 2017/5/12.
 */

public interface IPlayCallBack {

    void onCanPlayList(String playlistId, int startIndex, int timeMillis);

    void onCanPlayVideo(String videoId, int timeMills);
}
