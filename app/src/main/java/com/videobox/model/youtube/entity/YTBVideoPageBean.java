package com.videobox.model.youtube.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

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


        public Object id;

        public Snippet snippet;

        public String getVideoID() {
            if (id instanceof String) {
                return (String)id;
            } else if (id instanceof ID) {
                return ((ID)id).videoId;
            }
            return "-1";
        }

        public class ID {

            public String kind;

            public String videoId;

            public String playlistId;
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
