package com.videobox.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by liyanju on 2017/4/16.
 */

public class APIConstant {

    public static class YouTube {
        public static final String HOST_URL = "https://www.googleapis.com/youtube/v3/";

        public static final String DEVELOPER_KEY = "AIzaSyBUgQUzZJTZrdW9LAY0-hr__UYnKoQRRNU";

        /**
         * value:
         * contentDetails：2
         * fileDetails：1
         * id：0
         * liveStreamingDetails：2
         * localizations：2
         * player：0
         * processingDetails：1
         * recordingDetails：2
         * snippet：2
         * statistics：2
         * status：2
         * suggestions：1
         * topicDetails：2
         */
        public static final String PART = "part";

        /**
         * 该regionCode参数指示API选择指定区域中可用的视频图表。
         * 该参数只能与参数一起使用chart。参数值是ISO 3166-1 alpha-2国家代码。
         */
        public static final String REGION_CODE = "regionCode";

        /**
         * 该videoCategoryId参数标识应检索图表的视频类别。
         * 该参数只能与参数一起使用chart。默认情况下，图表不限于特定类别。默认值为0
         */
        public static final String VIDEOCATEGORYID = "videoCategoryId";

        public static final String CHART = "chart";

        public static final String KEY = "key";

        public static final String HL = "hl"; //该hl参数指定应用于API响应中文本值的语言。默认值为en_US

        public static final String PAGETOKEN = "pageToken"; //赋值使用nextPageToken prevPageTokent 用于翻页

        public static final String QUERY_CONTNET = "q";

        public static HashMap<String, String> sMostPopularVideos = new HashMap<>();

        public static HashMap<String, String> sCategoriesMap = new HashMap<>();

        public static HashMap<String, String> sSearchMap = new HashMap<>();

        public static final ArrayList<HashMap<String, String>> sParamsMapList = new ArrayList<>();

        static {
            sParamsMapList.add(sMostPopularVideos);
            sParamsMapList.add(sCategoriesMap);
            sParamsMapList.add(sSearchMap);

            //add common param
            for (HashMap<String, String> map : sParamsMapList) {
                map.put(KEY, DEVELOPER_KEY);
                map.put(PART, "snippet");
            }

            String country = Locale.getDefault().getCountry().toLowerCase();
            if ("cn".equals(country)) {
                country = "hk";
            }

            sMostPopularVideos.put(CHART, "mostPopular");
            sMostPopularVideos.put(REGION_CODE, country);

            sCategoriesMap.put(REGION_CODE, country);
        }
    }

    public static class DailyMontion {

        public static HashMap<String, String> sWatchVideosMap = new HashMap<>();

        public static HashMap<String, String> sChannelsMap = new HashMap<>();

        public static HashMap<String, String> sChannelVideosMap = new HashMap<>();

        public static HashMap<String, String> sSearchVideosMap = new HashMap<>();

        public static String HOST_URL = "https://api.dailymotion.com/";

        public static String PAGE = "page";

        static {
            String country = Locale.getDefault().getCountry().toLowerCase();
            if ("cn".equals(country)) {
                country = "en";
            }
            sWatchVideosMap.put("list", "what-to-watch");
            //sWatchVideosMap.put("languages", country);
            sWatchVideosMap.put("sort", "visited");
            sWatchVideosMap.put("fields", "title,channel,channel.id,description,duration,id,thumbnail_url,updated_time");
            sWatchVideosMap.put("limit", "10");

            sChannelsMap.put("fields", "id,name,description,");

            sChannelVideosMap.put("fields", "title,channel,channel.id,description,duration,id,thumbnail_url,updated_time");
            sChannelVideosMap.put("sort", "visited");
            sChannelVideosMap.put("limit", "10");

            sSearchVideosMap.put("fields", "description,id,name,thumbnail_url,updated_time,");
        }
    }
}
