package com.videobox.model.dailymotion.cache;

import com.videobox.model.dailymotion.entity.DMChannelsBean;
import com.videobox.model.dailymotion.entity.DMVideosPageBean;

import java.util.concurrent.TimeUnit;

import io.rx_cache.DynamicKey;
import io.rx_cache.EvictProvider;
import io.rx_cache.LifeCache;
import io.rx_cache.Reply;
import rx.Observable;


/**
 * Created by liyanju on 2017/4/23.
 */

public interface DailyMotionCache {

    @LifeCache(duration = 30, timeUnit = TimeUnit.DAYS)
    Observable<Reply<DMVideosPageBean>> getVideos(Observable<DMVideosPageBean> oVideoPage,
                                                  DynamicKey idLastUserQueried,
                                                  EvictProvider evictProvider);

    @LifeCache(duration = 30, timeUnit = TimeUnit.DAYS)
    Observable<Reply<DMChannelsBean>> getChannels(Observable<DMChannelsBean> oChannel,
                                                  DynamicKey idLastUserQueried,
                                                  EvictProvider evictProvider);

    @LifeCache(duration = 30, timeUnit = TimeUnit.DAYS)
    Observable<Reply<DMVideosPageBean>> getChannelVideos(Observable<DMVideosPageBean> oVideoPage,
                                                  DynamicKey idLastUserQueried,
                                                  EvictProvider evictProvider);

    @LifeCache(duration = 30, timeUnit = TimeUnit.DAYS)
    Observable<Reply<DMVideosPageBean>> getSearchVideo(Observable<DMVideosPageBean> oVideoPage,
                                                         DynamicKey idLastUserQueried,
                                                         EvictProvider evictProvider);
}


