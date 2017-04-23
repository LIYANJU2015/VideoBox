package com.videobox.model.dailymotion;

import com.commonlibs.base.BaseModel;
import com.commonlibs.integration.IRepositoryManager;
import com.videobox.model.dailymotion.entity.DMChannelsBean;
import com.videobox.model.dailymotion.entity.DMVideosPageBean;
import com.videobox.model.dailymotion.service.DailymotionService;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by liyanju on 2017/4/23.
 */

public class DaiyMotionModel extends BaseModel implements DailymotionService{

    private DailymotionService mDailymotionService;

    public DaiyMotionModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
        mDailymotionService = mRepositoryManager.obtainCacheService(DailymotionService.class);
    }

    @Override
    public Observable<DMVideosPageBean> getVideos(@QueryMap Map<String, String> options) {
        return null;
    }

    @Override
    public Observable<DMChannelsBean> getChannels(@QueryMap Map<String, String> options) {
        return null;
    }

    @Override
    public Observable<DMVideosPageBean> getChannelVideos(@Path("id") String id, @QueryMap Map<String, String> options) {
        return null;
    }

    @Override
    public Observable<DMVideosPageBean> getSearchVideo(@QueryMap Map<String, String> options, @Query("search") String search) {
        return null;
    }
}
