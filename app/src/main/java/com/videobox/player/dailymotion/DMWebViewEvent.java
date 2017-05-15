package com.videobox.player.dailymotion;

/**
 * Created by liyanju on 2017/5/15.
 */

public interface DMWebViewEvent {

    /**
     * start
     */
    void onStartVideo();

    /**
     * loadedmetadata
     */
    void onLoadedmetadata();

    /**
     * progress
     * @param bufferedTime
     */
    void onProgress(double bufferedTime);

    /**
     *durationchange
     */
    void onDurationchange(double duration);

    /**
     * rebuffer
     */
    void onRebuffer(boolean rebuffer);

    /**
     * qualitiesavailable
     */
    void qualitiesavailable();

    /**
     * error
     */
    void onError(String error);

    /**
     * pause
     */
    void onPause(boolean paused);

    /**
     * fullscreenchange
     * @param fullscreen
     */
    void onFullscreenchange(boolean fullscreen);

    /**
     * end
     * @param end
     */
    void onEnd(boolean end);

}
