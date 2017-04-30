package com.videobox.model.dailymotion;

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
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by liyanju on 2017/4/23.
 */

public class DaiyMotionModel extends BaseModel {

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

    public Observable<DMChannelsBean> getChannels(Map<String, String> options, boolean update, int page) {
        Observable<DMChannelsBean> oChannel = mService.getChannels(options, page);
        return mCache.getChannels(oChannel, new DynamicKey(page), new EvictDynamicKey(update))
                .flatMap(new Func1<Reply<DMChannelsBean>, Observable<DMChannelsBean>>() {
                    @Override
                    public Observable<DMChannelsBean> call(Reply<DMChannelsBean> dmChannelsBeanReply) {
                        return Observable.just(dmChannelsBeanReply.getData());
                    }
                });
    }

    public Observable<DMVideosPageBean> getChannelVideos(String id, Map<String, String> options, int page, boolean update) {
        Observable<DMVideosPageBean> oDMChannelVideo = mService.getChannelVideos(id, options, page);
        return mCache.getChannelVideos(oDMChannelVideo, new DynamicKey(page), new EvictDynamicKey(update))
                .flatMap(new Func1<Reply<DMVideosPageBean>, Observable<DMVideosPageBean>>() {
                    @Override
                    public Observable<DMVideosPageBean> call(Reply<DMVideosPageBean> dmVideosPageBeanReply) {
                        return Observable.just(dmVideosPageBeanReply.getData());
                    }
                });
    }

    public Observable<DMVideosPageBean> getSearchVideo(Map<String, String> options, String search, int page, boolean update) {
        Observable<DMVideosPageBean> oDMVideosPageBean = mService.getSearchVideo(options, search, page);
        return mCache.getSearchVideo(oDMVideosPageBean, new DynamicKey(page), new EvictDynamicKey(update))
                .flatMap(new Func1<Reply<DMVideosPageBean>, Observable<DMVideosPageBean>>() {
                    @Override
                    public Observable<DMVideosPageBean> call(Reply<DMVideosPageBean> dmVideosPageBeanReply) {
                        return Observable.just(dmVideosPageBeanReply.getData());
                    }
                });
    }
}
