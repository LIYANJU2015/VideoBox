package com.videobox.model.youtube.entity;

import android.content.Context;

import com.commonlibs.util.StringUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.videobox.player.youtube.YouTubePlayerActivity;

import java.util.ArrayList;
import java.util.Map;

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

    public static class YouTubeVideo {

        @Expose
        public boolean isPlaying = false;

        public Object id;

        public Snippet snippet;

        public String kind;

        public String etag;

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
