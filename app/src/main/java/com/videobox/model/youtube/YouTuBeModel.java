package com.videobox.model.youtube;

import com.commonlibs.base.BaseModel;
import com.commonlibs.integration.IRepositoryManager;
import com.commonlibs.util.StringUtils;
import com.videobox.model.dailymotion.cache.DailyMotionCache;
import com.videobox.model.dailymotion.entity.DMVideosPageBean;
import com.videobox.model.youtube.cache.YoutubeCache;
import com.videobox.model.youtube.entity.YTBCategoriesBean;
import com.videobox.model.youtube.entity.YTBLanguagesBean;
import com.videobox.model.youtube.entity.YTBVideoPageBean;
import com.videobox.model.youtube.entity.YTbRegionsBean;
import com.videobox.model.youtube.entity.YTbRegionsListBean;
import com.videobox.model.youtube.service.YouTubeService;

import java.util.Map;

import io.rx_cache.DynamicKey;
import io.rx_cache.EvictDynamicKey;
import io.rx_cache.EvictProvider;
import io.rx_cache.Reply;
import retrofit2.Call;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;
import rx.functions.Func1;

import static android.view.View.Y;

/**
 * Created by liyanju on 2017/4/23.
 */

public class YouTuBeModel extends BaseModel {

    private YouTubeService mService;

    private YoutubeCache mCache;

    public YouTuBeModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
        mService = mRepositoryManager.obtainRetrofitService(YouTubeService.class);
        mCache = mRepositoryManager.obtainCacheService(YoutubeCache.class);
    }

    public Observable<YTBVideoPageBean> getMostPopularVideos(Map<String, String> options, String pageToken, boolean isUpdate) {
        Observable<YTBVideoPageBean> videoObserable = mService.getMostPopularVideos(pageToken, options);
        if (StringUtils.isEmpty(pageToken)) {
            pageToken = "pageToken";
        }
        return mCache.getMostPopularVideos(videoObserable, new DynamicKey(pageToken), new EvictDynamicKey(isUpdate))
                .flatMap(new Func1<Reply<YTBVideoPageBean>, Observable<YTBVideoPageBean>>() {
                    @Override
                    public Observable<YTBVideoPageBean> call(Reply<YTBVideoPageBean> ytbVideoPageBeanReply) {
                        return Observable.just(ytbVideoPageBeanReply.getData());
                    }
                });
    }


    public Observable<YTBCategoriesBean> getYouTubeCategories(String regionCode, Map<String, String> options, boolean update) {
        Observable<YTBCategoriesBean> categoriesObservale = mService.getYouTubeCategories(regionCode, options);
        return mCache.getYouTubeCategories(categoriesObservale,
                new DynamicKey(regionCode), new EvictDynamicKey(update))
                .flatMap(new Func1<Reply<YTBCategoriesBean>, Observable<YTBCategoriesBean>>() {
                    @Override
                    public Observable<YTBCategoriesBean> call(Reply<YTBCategoriesBean> ytbCategoriesBeanReply) {
                        return Observable.just(ytbCategoriesBeanReply.getData());
                    }
                });
    }

    public Observable<YTBLanguagesBean> getYouTubeLanguages(boolean update) {
        Observable<YTBLanguagesBean> languageObservable = mService.getYouTubeLanguages();
        return mCache.getYouTubeLanguages(languageObservable, new EvictProvider(update))
                .flatMap(new Func1<Reply<YTBLanguagesBean>,
                        Observable<YTBLanguagesBean>>() {
                    @Override
                    public Observable<YTBLanguagesBean> call(Reply<YTBLanguagesBean> ytbLanguagesBeanReply) {
                        return Observable.just(ytbLanguagesBeanReply.getData());
                    }
                });
    }

    public Observable<YTbRegionsListBean> getYouTubeRegions(boolean update) {
        Observable<YTbRegionsListBean> regionObservable = mService.getYouTubeRegions();
        return mCache.getYouTubeRegions(regionObservable, new EvictProvider(update))
                .flatMap(new Func1<Reply<YTbRegionsListBean>, Observable<YTbRegionsListBean>>() {
                    @Override
                    public Observable<YTbRegionsListBean> call(Reply<YTbRegionsListBean> yTbRegionsBeanReply) {
                        return Observable.just(yTbRegionsBeanReply.getData());
                    }
                });
    }


    public Observable<YTBVideoPageBean> getSearchVideos(Map<String, String> options, String query,
                                                        String pageToken, boolean update) {
        Observable<YTBVideoPageBean> videoObservable = mService.getSearchVideos(pageToken, query, options);
        return mCache.getSearchVideos(videoObservable, new DynamicKey(pageToken), new EvictProvider(update))
                .flatMap(new Func1<Reply<YTBVideoPageBean>,
                        Observable<YTBVideoPageBean>>() {
                    @Override
                    public Observable<YTBVideoPageBean> call(Reply<YTBVideoPageBean> ytbVideoPageBeanReply) {
                        return Observable.just(ytbVideoPageBeanReply.getData());
                    }
                });
    }

    public Observable<YTBVideoPageBean> getRelatedVideo(Map<String, String> options, String relatedToVideoId, boolean update) {
        Observable<YTBVideoPageBean> videoObservable = mService.getRelatedVideo(relatedToVideoId, options);
        return mCache.getRelatedVideo(videoObservable, new EvictProvider(update))
                .flatMap(new Func1<Reply<YTBVideoPageBean>,
                        Observable<YTBVideoPageBean>>() {
                    @Override
                    public Observable<YTBVideoPageBean> call(Reply<YTBVideoPageBean> ytbVideoPageBeanReply) {
                        return Observable.just(ytbVideoPageBeanReply.getData());
                    }
                });
    }

    public Observable<YTBVideoPageBean> getPlaylistItems(Map<String, String> options, String playlistId,
                                                         boolean update, String pageToken) {
        Observable<YTBVideoPageBean> videoObservable = mService.getPlaylistItems(playlistId, options, pageToken);
        if (StringUtils.isEmpty(pageToken)) {
            pageToken = "pageToken";
        }
        return mCache.getPlaylistItems(videoObservable, new DynamicKey(pageToken), new EvictProvider(update))
                .flatMap(new Func1<Reply<YTBVideoPageBean>,
                        Observable<YTBVideoPageBean>>() {
                    @Override
                    public Observable<YTBVideoPageBean> call(Reply<YTBVideoPageBean> ytbVideoPageBeanReply) {
                        return Observable.just(ytbVideoPageBeanReply.getData());
                    }
                });
    }

    public Observable<YTBVideoPageBean> getCategoryVideos(String pageToken, String videoCategoryId,
                                                          Map<String, String> options, boolean update) {
        Observable<YTBVideoPageBean> videoObservable = mService.getCategoryVideos(pageToken, videoCategoryId, options);
        if (StringUtils.isEmpty(pageToken)) {
            pageToken = "pageToken";
        }
        return mCache.getCategoryVideos(videoObservable, new DynamicKey(pageToken), new EvictProvider(update))
                .flatMap(new Func1<Reply<YTBVideoPageBean>,
                        Observable<YTBVideoPageBean>>() {
                    @Override
                    public Observable<YTBVideoPageBean> call(Reply<YTBVideoPageBean> ytbVideoPageBeanReply) {
                        return Observable.just(ytbVideoPageBeanReply.getData());
                    }
                });

    }

    public Observable<YTBVideoPageBean> getVideoInfoByVid(Map<String, String> options, String vid, boolean update) {
        Observable<YTBVideoPageBean> videoObservable = mService.getVideoInfoByVid(options, vid);
        return mCache.getVideoInfoByVid(videoObservable, new EvictProvider(update))
                .flatMap(new Func1<Reply<YTBVideoPageBean>,
                        Observable<YTBVideoPageBean>>() {
                    @Override
                    public Observable<YTBVideoPageBean> call(Reply<YTBVideoPageBean> ytbVideoPageBeanReply) {
                        return Observable.just(ytbVideoPageBeanReply.getData());
                    }
                });
    }
}
