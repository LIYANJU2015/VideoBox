package com.videobox.model.dailymotion;

import android.support.annotation.NonNull;

import com.commonlibs.base.BaseModel;
import com.commonlibs.integration.IRepositoryManager;
import com.videobox.model.dailymotion.cache.DailyMotionCache;
import com.videobox.model.dailymotion.entity.DMChannelsBean;
import com.videobox.model.dailymotion.entity.DMVideosPageBean;
import com.videobox.model.dailymotion.service.DailymotionService;

import java.util.Map;


import io.rx_cache.DynamicKey;
import io.rx_cache.EvictDynamicKey;
import io.rx_cache.Reply;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by liyanju on 2017/4/23.
 */

public class DaiyMotionModel extends BaseModel{

    private DailymotionService mService;
    private DailyMotionCache mCache;

    public DaiyMotionModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
        mService = mRepositoryManager.obtainRetrofitService(DailymotionService.class);
        mCache = mRepositoryManager.obtainCacheService(DailyMotionCache.class);
    }

    public Observable<DMVideosPageBean> getVideos(Map<String, String> options, boolean update, int page) {
        Observable<DMVideosPageBean> oVideoPage = mService.getVideos(page, options);
        return mCache.getVideos(oVideoPage, new DynamicKey(page), new EvictDynamicKey(update))
                .flatMap(new Func1<Reply<DMVideosPageBean>, Observable<DMVideosPageBean>>() {
                    @Override
                    public Observable<DMVideosPageBean> call(Reply<DMVideosPageBean> dmVideosPageBeanReply) {
                        return Observable.just(dmVideosPageBeanReply.getData());
                    }
                });
    }

    public Observable<DMChannelsBean> getChannels(@QueryMap Map<String, String> options) {
        return null;
    }

    public Observable<DMVideosPageBean> getChannelVideos(@Path("id") String id, @QueryMap Map<String, String> options) {
        return null;
    }

    public Observable<DMVideosPageBean> getSearchVideo(@QueryMap Map<String, String> options, @Query("search") String search) {
        return null;
    }
}
