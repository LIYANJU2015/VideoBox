package com.videobox.model.youtube;

import com.commonlibs.base.BaseModel;
import com.commonlibs.integration.IRepositoryManager;
import com.videobox.model.youtube.entity.YTBCategoriesBean;
import com.videobox.model.youtube.entity.YTBLanguagesBean;
import com.videobox.model.youtube.entity.YTBVideoPageBean;
import com.videobox.model.youtube.entity.YTbRegionsBean;
import com.videobox.model.youtube.service.YouTubeService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.QueryMap;

/**
 * Created by liyanju on 2017/4/23.
 */

public class YouTuBeModel extends BaseModel implements YouTubeService{

    private YouTubeService mYouTubeService;

    public YouTuBeModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
        mYouTubeService = mRepositoryManager.obtainRetrofitService(YouTubeService.class);
    }

    @Override
    public Call<YTBVideoPageBean> getMostPopularVideos(@QueryMap Map<String, String> options) {

        return null;
    }

    @Override
    public Call<YTBCategoriesBean> getYouTubeCategories(@QueryMap Map<String, String> options) {
        return null;
    }

    @Override
    public Call<YTBLanguagesBean> getYouTubeLanguages() {
        return null;
    }

    @Override
    public Call<YTbRegionsBean> getYouTubeRegions() {
        return null;
    }

    @Override
    public Call<YTBVideoPageBean> getSearchVideos(@QueryMap Map<String, String> options) {
        return null;
    }
}
