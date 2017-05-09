package com.videobox.model.youtube.cache;

import com.videobox.model.youtube.entity.YTBCategoriesBean;
import com.videobox.model.youtube.entity.YTBLanguagesBean;
import com.videobox.model.youtube.entity.YTBVideoPageBean;
import com.videobox.model.youtube.entity.YTbRegionsBean;
import com.videobox.model.youtube.entity.YTbRegionsListBean;

import java.util.concurrent.TimeUnit;

import io.rx_cache.DynamicKey;
import io.rx_cache.EvictProvider;
import io.rx_cache.LifeCache;
import io.rx_cache.Reply;
import rx.Observable;

import static android.R.attr.y;

/**
 * Created by liyanju on 2017/5/7.
 */

public interface YoutubeCache {

    @LifeCache(duration = 30, timeUnit = TimeUnit.DAYS)
    Observable<Reply<YTBVideoPageBean>> getMostPopularVideos(Observable<YTBVideoPageBean> videobean, DynamicKey idLastUserQueried,
                                                             EvictProvider evictProvider);

    @LifeCache(duration = 30, timeUnit = TimeUnit.DAYS)
    Observable<Reply<YTBCategoriesBean>> getYouTubeCategories(Observable<YTBCategoriesBean> categories, DynamicKey idLastUserQueried,
                                                              EvictProvider evictProvider);


    @LifeCache(duration = 30, timeUnit = TimeUnit.DAYS)
    Observable<Reply<YTBLanguagesBean>> getYouTubeLanguages(Observable<YTBLanguagesBean> languages,
                                                            EvictProvider evictProvider);

    @LifeCache(duration = 30, timeUnit = TimeUnit.DAYS)
    Observable<Reply<YTbRegionsListBean>> getYouTubeRegions(Observable<YTbRegionsListBean> regions,
                                                            EvictProvider evictProvider);

    @LifeCache(duration = 5, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<YTBVideoPageBean>> getSearchVideos(Observable<YTBVideoPageBean> videos, DynamicKey idLastUserQueried,
                                                        EvictProvider evictProvider);

    @LifeCache(duration = 5, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<YTBVideoPageBean>> getRelatedVideo(Observable<YTBVideoPageBean> videos, EvictProvider evictProvider);

    @LifeCache(duration = 5, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<YTBVideoPageBean>> getPlaylistItems(Observable<YTBVideoPageBean> videos, DynamicKey idLastUserQueried, EvictProvider evictProvider);

    @LifeCache(duration = 30, timeUnit = TimeUnit.DAYS)
    Observable<Reply<YTBVideoPageBean>> getCategoryVideos(Observable<YTBVideoPageBean> videos, DynamicKey idLastUserQueried, EvictProvider evictProvider);
 }
