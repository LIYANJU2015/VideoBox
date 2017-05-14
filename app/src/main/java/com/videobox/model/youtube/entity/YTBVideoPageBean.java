package com.videobox.model.youtube.entity;

import android.content.Context;

import com.commonlibs.commonloader.ComDataLoadTask;
import com.commonlibs.commonloader.IComDataLoader;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.StringUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.util.YouTubeUtil;
import com.videobox.AppAplication;
import com.videobox.model.APIConstant;
import com.videobox.model.youtube.service.YouTubeService;
import com.videobox.player.youtube.YouTubePlayerActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


import retrofit2.Call;
import retrofit2.Response;

import static android.R.attr.data;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by liyanju on 2017/4/14.
 */

public class YTBVideoPageBean {

    public String nextPageToken;

    public String prevPageToken;

    public PageInfo pageInfo;

    public ArrayList<YouTubeVideo> items;

    public static class PageInfo {

        public int totalResults;

        public int resultsPerPage;
    }

    public static class ContentDetails {

        public int itemCount;

        public String duration;
        public String dimension;
        public String definition;
        public String caption;
        public boolean licensedContent;
        public String projection;
    }

    public static class Status {

        /**
         * public, private
         */
        public String privacyStatus;

        public boolean isPrivacy() {
            return "private".equals(privacyStatus);
        }
    }

    public static class YouTubeVideo implements IComDataLoader<String>{
        @Override
        public void setLoadSuccess() {

        }

        @Override
        public boolean isLoadSuccess() {
            return contentDetails != null;
        }

        @Override
        public String getId() {
            return snippet.resourceId.videoId;
        }

        @Override
        public String onHandleSelfData(ComDataLoadTask.AsyncHandleListener listener) {
            LogUtils.v("onHandleSelfData", "vid "+snippet.resourceId.videoId);
                     YouTubeService service = ((AppAplication)AppAplication.getContext()).getAppComponent().repositoryManager()
                             .obtainRetrofitService(YouTubeService.class);
            Call<YTBVideoPageBean> call = service.getVideoInfoByVid2(APIConstant.YouTube.sVideoContentDetails, snippet.resourceId.videoId);
            try {
                YTBVideoPageBean pageBean = call.execute().body();
                LogUtils.v("onHandleSelfData", "pageBean "+ pageBean);
                if (pageBean.items.size() > 0 && pageBean.items.get(0).contentDetails != null) {
                    String duration = pageBean.items.get(0).contentDetails.duration;
                    return YouTubeUtil.convertDuration(duration);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
             return null;
        }

        @Override
        public void setLoadDataObj(String duration) {
            contentDetails = new ContentDetails();
            contentDetails.duration = duration;
        }

        @Override
        public String getLoadDataObj() {
            return contentDetails.duration;
        }

        @Expose
        public boolean isPlaying = false;

        public Object id;

        public Snippet snippet;

        public String kind;

        public String etag;

        public ContentDetails contentDetails;

        public Status status;

        public String getVideoID() {
            if (id instanceof String) {
                return (String)id;
            } else if (id instanceof Map) {
                return ((Map<String, String>)id).get("videoId");
            }
            return "";
        }

        public String getPlaylistID() {
            if (id != null && id instanceof Map) {
                return ((Map<String, String>)id).get("playlistId");
            }
            return "";
        }

        public String getThumbnailsUrl(){
            String url = "";
            if (snippet != null && snippet.thumbnails != null) {
                if (snippet.thumbnails.standard != null) {
                    url = snippet.thumbnails.standard.url;
                } else if (snippet.thumbnails.higth != null) {
                    url = snippet.thumbnails.higth.url;
                } else if (snippet.thumbnails.default1 != null) {
                    url = snippet.thumbnails.default1.url;
                }
            }
            return url;
        }

        public class ID {

            public String kind;

            public String videoId;

            public String playlistId;
        }

        public void intoPlayer(Context context) {
            String videoID = getVideoID();
            String playlistID = getPlaylistID();
            if (!StringUtils.isEmpty(videoID)) {
                YouTubePlayerActivity.launchVideoID(context, videoID);
            } else if (!StringUtils.isEmpty(playlistID)) {
                YouTubePlayerActivity.launchPlayListID(context, playlistID);
            }
        }

    }

    public static class Snippet {

        public String publishedAt;

        public String channelId;

        public String title;

        public String description;

        public Thumbnails thumbnails;

        public String channelTitle;

        public String tags[];

        public String liveBroadcastContent;

        public String defaultAudioLanguage;

        public ResourceId resourceId;

        public int position;

        public class Thumbnails {

            @SerializedName("default")
            public Default default1;

            public High higth;

            public Standard standard;

            public class Standard {
                public String url;
            }

            public class High {
                public String url;
            }

            public class Default {
                public String url;
            }
        }

        public class ResourceId {
            public String videoId;
        }
    }

}
