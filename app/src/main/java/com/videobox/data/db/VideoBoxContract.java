package com.videobox.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.videobox.bean.DMChannelsBean;
import com.videobox.bean.DMVideoBean;
import com.videobox.bean.YTBCategoriesBean;
import com.videobox.bean.YTBLanguagesBean;
import com.videobox.bean.YTBVideoPageBean;
import com.videobox.bean.YTbRegionsBean;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static com.videobox.data.db.VideoBoxContract.DMVideo.THUMBNAIL_URL;

/**
 * Created by liyanju on 2017/4/17.
 */

public class VideoBoxContract {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    private static final String LEFT_BRACKET = "(";
    private static final String RIGHT_BRACKET = ");";
    private static final String COMMA = ",";

    public static final String AUTHORITY = "com.videobox.data.db";

    /**
     * 数据库名称
     */
    public static final String DATABASE_NAME = "video_box.db";

    /**
     * 数据库版本号
     * 2.2 version 2 CONTACTS_ID SIM_ID
     */
    public static final int DATABASE_VERSION = 2;

    public static class DMChannels implements BaseColumns{

        public static final String TABLE_NAME = "DMChannelsBean";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String TABLE_CREATE = genTableCreationSql(TABLE_NAME, getColumHashMap());

        public static final String CHANCEL_ID = "chancel_id";
        public static final String CHANCEL_NAME = "chancel_name";

        private static HashMap<String, String> getColumHashMap() {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(_ID, " integer primary key autoincrement");
            map.put(CHANCEL_ID, "text");
            map.put(CHANCEL_NAME, "text");
            return map;
        }

        public static ContentValues createContentValues(DMChannelsBean.Channel channel) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CHANCEL_ID, channel.id);
            contentValues.put(CHANCEL_NAME, channel.name);
            return contentValues;
        }

        public static String getID(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndexOrThrow(CHANCEL_ID));
        }

        public static String getChannelName(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndexOrThrow(CHANCEL_NAME));
        }

    }

    public static class DMVideo implements BaseColumns{

        public static final String TABLE_NAME = "DMVideo";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String TABLE_CREATE = genTableCreationSql(TABLE_NAME, getColumHashMap());

        public static final String CHANNEL = "channel";
        public static final String DESCRIPTION = "description";
        public static final String DURATION = "duration";
        public static final String VIDEO_ID = "video_id";
        public static final String THUMBNAIL_URL = "thumbnail_url";
        public static final String TITLE = "title";
        public static final String UPDATED_TIME = "updated_time";
        public static final String CHANNELID = "channelID";

        private static HashMap<String, String> getColumHashMap() {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(_ID, " integer primary key autoincrement");
            map.put(CHANNEL, "text");
            map.put(DESCRIPTION, "text");
            map.put(DURATION, "text");
            map.put(VIDEO_ID, "text");
            map.put(THUMBNAIL_URL, "text");
            map.put(TITLE, "text");
            map.put(UPDATED_TIME, "text");
            map.put(CHANNELID, "text");
            return map;
        }

        public static ContentValues createContentValues(DMVideoBean dmVideoBean) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CHANNEL, dmVideoBean.channel);
            contentValues.put(DESCRIPTION, dmVideoBean.description);
            contentValues.put(DURATION, dmVideoBean.duration);
            contentValues.put(VIDEO_ID, dmVideoBean.id);
            contentValues.put(THUMBNAIL_URL, dmVideoBean.thumbnail_url);
            contentValues.put(TITLE, dmVideoBean.title);
            contentValues.put(UPDATED_TIME, dmVideoBean.updated_time);
            contentValues.put(CHANNELID, dmVideoBean.channelID);
            return contentValues;
        }

        public static String getChannel(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndexOrThrow(CHANNEL));
        }

        public static String getDescription(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION));
        }

        public static String getVideoId(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_ID));
        }

        public static String getThumbnailUrl(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndexOrThrow(THUMBNAIL_URL));
        }

        public static String getTitle(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
        }

        public static String getChannelid(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndexOrThrow(CHANNELID));
        }

    }


    public static class YouTubeCategories implements BaseColumns {

        public static final String TABLE_NAME = "YTBCategoriesBean";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String TABLE_CREATE = genTableCreationSql(TABLE_NAME, getColumHashMap());

        public static final String KIND = "kind";
        public static final String ETAG = "etag";
        public static final String VIDEOCATEGORYID = "videoCategoryId";
        public static final String CHANNELID = "channelId";
        public static final String TITLE = "title";

        private static HashMap<String, String> getColumHashMap() {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(_ID, " integer primary key autoincrement");
            map.put(KIND, "text");
            map.put(ETAG, "text");
            map.put(VIDEOCATEGORYID, "text");
            map.put(CHANNELID, "text");
            map.put(THUMBNAIL_URL, "text");
            map.put(TITLE, "text");
            return map;
        }

        public static ContentValues createContentValue(YTBCategoriesBean.Categories categoriesBean) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KIND, categoriesBean.kind);
            contentValues.put(ETAG, categoriesBean.etag);
            contentValues.put(VIDEOCATEGORYID, categoriesBean.id);
            if (categoriesBean.snippet != null) {
                contentValues.put(CHANNELID, categoriesBean.snippet.channelId);
                contentValues.put(TITLE, categoriesBean.snippet.title);
            }
            return contentValues;
        }
    }

    public static class YouTubeLanguages implements BaseColumns {

        public static final String TABLE_NAME = "YTBLanguagesBean";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String TABLE_CREATE = genTableCreationSql(TABLE_NAME, getColumHashMap());

        public static final String ID = "id";
        public static final String HL = "hl";
        public static final String NAME = "name";

        private static HashMap<String, String> getColumHashMap() {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(_ID, " integer primary key autoincrement");
            map.put(ID, "text");
            map.put(HL, "text");
            map.put(NAME, "text");
            return map;
        }

        public static ContentValues createContentValue(YTBLanguagesBean.Languages languagesBean) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ID, languagesBean.id);
            if (languagesBean.snippet != null) {
                contentValues.put(HL, languagesBean.snippet.hl);
                contentValues.put(NAME, languagesBean.snippet.name);
            }
            return contentValues;
        }
    }

    public static class YouTubeRgions implements BaseColumns {

        public static final String TABLE_NAME = "YouTubeRgions";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String TABLE_CREATE = genTableCreationSql(TABLE_NAME, getColumHashMap());

        public static final String ID = "id";
        public static final String GL = "gl";
        public static final String NAME = "name";

        private static HashMap<String, String> getColumHashMap() {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(_ID, " integer primary key autoincrement");
            map.put(ID, "text");
            map.put(GL, "text");
            map.put(NAME, "text");
            return map;
        }

        public static ContentValues createContentValue(YTbRegionsBean regionsBean) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ID, regionsBean.id);
            if (regionsBean.snippet != null) {
                contentValues.put(GL, regionsBean.snippet.gl);
                contentValues.put(NAME, regionsBean.snippet.name);
            }
            return contentValues;
        }
    }

    public static class YouTubeVideo implements BaseColumns {

        public static final String TABLE_NAME = "YouTubeVideo";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String TABLE_CREATE = genTableCreationSql(TABLE_NAME, getColumHashMap());

        public static final String VIDEO_ID = "video_id";
        public static final String PUBLISHEDAT = "publishedAt";
        public static final String CHANNELID = "channelId";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String THUMBNAILS = "thumbnails";
        public static final String CHANNELTITLE = "channelTitle";
        public static final String LIVEBROADCASTCONTENT = "liveBroadcastContent";
        public static final String DEFAULTAUDIOLANGUAGE = "defaultAudioLanguage";

        private static HashMap<String, String> getColumHashMap() {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(_ID, " integer primary key autoincrement");
            map.put(VIDEO_ID, "text");
            map.put(PUBLISHEDAT, "text");
            map.put(CHANNELID, "text");
            map.put(TITLE, "text");
            map.put(DESCRIPTION, "text");
            map.put(THUMBNAILS, "text");
            map.put(CHANNELTITLE, "text");
            map.put(LIVEBROADCASTCONTENT, "text");
            map.put(DEFAULTAUDIOLANGUAGE, "text");
            return map;
        }

        public static ContentValues createContentValue(YTBVideoPageBean.YouTubeVideo video) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(VIDEO_ID, video.getVideoID());
            if (video.snippet != null) {
                contentValues.put(PUBLISHEDAT, video.snippet.publishedAt);
                contentValues.put(CHANNELID, video.snippet.channelId);
                contentValues.put(TITLE, video.snippet.title);
                contentValues.put(DESCRIPTION, video.snippet.description);
                if (video.snippet.thumbnails != null) {
                    contentValues.put(THUMBNAILS, video.snippet.thumbnails.standard.url);
                }
                contentValues.put(CHANNELTITLE, video.snippet.channelTitle);
                contentValues.put(LIVEBROADCASTCONTENT, video.snippet.liveBroadcastContent);
                contentValues.put(DEFAULTAUDIOLANGUAGE, video.snippet.defaultAudioLanguage);
            }
            return contentValues;
        }
    }


    public static String genTableCreationSql(String tableName, HashMap<String, String> mapTable) {
        StringBuffer sqlStringBuffer = new StringBuffer();
        int i = 0;

        for (String key : mapTable.keySet()) {
            if (i == 0) {
                sqlStringBuffer.append(CREATE_TABLE).append(tableName).append(LEFT_BRACKET);
            }

            sqlStringBuffer.append(key).append(" ").append(mapTable.get(key));

            if (i == mapTable.size() - 1) {
                sqlStringBuffer.append(RIGHT_BRACKET);
            } else {
                sqlStringBuffer.append(COMMA);
            }

            i++;
        }

        return sqlStringBuffer.toString();
    }

}
