package com.videobox.model.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.commonlibs.provider.TableInfo;
import com.videobox.model.dailymotion.entity.DMChannelsBean;
import com.videobox.model.dailymotion.entity.DMVideoBean;
import com.videobox.model.youtube.entity.YTBCategoriesBean;
import com.videobox.model.youtube.entity.YTBLanguagesBean;
import com.videobox.model.youtube.entity.YTBVideoPageBean;
import com.videobox.model.youtube.entity.YTbRegionsBean;

import java.util.Map;

import static com.videobox.model.db.VideoBoxContract.DMVideo.THUMBNAIL_URL;

/**
 * Created by liyanju on 2017/4/17.
 */

public class VideoBoxContract {

    public static final String AUTHORITY = "com.videobox.data.db";

    /**
     * 数据库名称
     */
    public static final String DATABASE_NAME = "video_box.db";

    /**
     * 数据库版本号
     */
    public static final int DATABASE_VERSION = 2;

    public static class DMChannels extends TableInfo{

        public static final String TABLE_NAME = "DMChannels";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String CHANCEL_ID = "chancel_id";
        public static final String CHANCEL_NAME = "chancel_name";

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

        @Override
        public String onTableName() {
            return TABLE_NAME;
        }

        @Override
        public Uri onContentUri() {
            return CONTENT_URI;
        }

        @Override
        public void onInitColumnsMap(Map<String, String> columnsMap) {
            columnsMap.put(CHANCEL_ID, "text");
            columnsMap.put(CHANCEL_NAME, "text");
        }
    }

    public static class DMVideo extends TableInfo{

        public static final String TABLE_NAME = "DMVideo";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String CHANNEL = "channel";
        public static final String DESCRIPTION = "description";
        public static final String DURATION = "duration";
        public static final String VIDEO_ID = "video_id";
        public static final String THUMBNAIL_URL = "thumbnail_url";
        public static final String TITLE = "title";
        public static final String UPDATED_TIME = "updated_time";
        public static final String CHANNELID = "channelID";

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

        @Override
        public String onTableName() {
            return TABLE_NAME;
        }

        @Override
        public Uri onContentUri() {
            return CONTENT_URI;
        }

        @Override
        public void onInitColumnsMap(Map<String, String> columnsMap) {
            columnsMap.put(CHANNEL, "text");
            columnsMap.put(DESCRIPTION, "text");
            columnsMap.put(DURATION, "text");
            columnsMap.put(VIDEO_ID, "text");
            columnsMap.put(THUMBNAIL_URL, "text");
            columnsMap.put(TITLE, "text");
            columnsMap.put(UPDATED_TIME, "text");
            columnsMap.put(CHANNELID, "text");
        }
    }


    public static class YouTubeCategories extends TableInfo {

        public static final String TABLE_NAME = "YTBCategoriesBean";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String KIND = "kind";
        public static final String ETAG = "etag";
        public static final String VIDEOCATEGORYID = "videoCategoryId";
        public static final String CHANNELID = "channelId";
        public static final String TITLE = "title";

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

        @Override
        public String onTableName() {
            return TABLE_NAME;
        }

        @Override
        public Uri onContentUri() {
            return CONTENT_URI;
        }

        @Override
        public void onInitColumnsMap(Map<String, String> columnsMap) {
            columnsMap.put(KIND, "text");
            columnsMap.put(ETAG, "text");
            columnsMap.put(VIDEOCATEGORYID, "text");
            columnsMap.put(CHANNELID, "text");
            columnsMap.put(THUMBNAIL_URL, "text");
            columnsMap.put(TITLE, "text");
        }
    }

    public static class YouTubeLanguages extends TableInfo {

        public static final String TABLE_NAME = "YTBLanguages";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String ID = "id";
        public static final String HL = "hl";
        public static final String NAME = "name";

        public static ContentValues createContentValue(YTBLanguagesBean.Languages languagesBean) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ID, languagesBean.id);
            if (languagesBean.snippet != null) {
                contentValues.put(HL, languagesBean.snippet.hl);
                contentValues.put(NAME, languagesBean.snippet.name);
            }
            return contentValues;
        }

        @Override
        public String onTableName() {
            return TABLE_NAME;
        }

        @Override
        public Uri onContentUri() {
            return CONTENT_URI;
        }

        @Override
        public void onInitColumnsMap(Map<String, String> columnsMap) {
            columnsMap.put(ID, "text");
            columnsMap.put(HL, "text");
            columnsMap.put(NAME, "text");
        }
    }

    public static class YouTubeRgions extends TableInfo {

        public static final String TABLE_NAME = "YouTubeRgions";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String ID = "id";
        public static final String GL = "gl";
        public static final String NAME = "name";

        public static ContentValues createContentValue(YTbRegionsBean regionsBean) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ID, regionsBean.id);
            if (regionsBean.snippet != null) {
                contentValues.put(GL, regionsBean.snippet.gl);
                contentValues.put(NAME, regionsBean.snippet.name);
            }
            return contentValues;
        }

        @Override
        public String onTableName() {
            return TABLE_NAME;
        }

        @Override
        public Uri onContentUri() {
            return CONTENT_URI;
        }

        @Override
        public void onInitColumnsMap(Map<String, String> columnsMap) {
            columnsMap.put(ID, "text");
            columnsMap.put(GL, "text");
            columnsMap.put(NAME, "text");
        }
    }

    public static class YouTubeVideo extends TableInfo {

        public static final String TABLE_NAME = "YouTubeVideo";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String VIDEO_ID = "video_id";
        public static final String PUBLISHEDAT = "publishedAt";
        public static final String CHANNELID = "channelId";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String THUMBNAILS = "thumbnails";
        public static final String CHANNELTITLE = "channelTitle";
        public static final String LIVEBROADCASTCONTENT = "liveBroadcastContent";
        public static final String DEFAULTAUDIOLANGUAGE = "defaultAudioLanguage";

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

        @Override
        public String onTableName() {
            return TABLE_NAME;
        }

        @Override
        public Uri onContentUri() {
            return CONTENT_URI;
        }

        @Override
        public void onInitColumnsMap(Map<String, String> columnsMap) {
            columnsMap.put(VIDEO_ID, "text");
            columnsMap.put(PUBLISHEDAT, "text");
            columnsMap.put(CHANNELID, "text");
            columnsMap.put(TITLE, "text");
            columnsMap.put(DESCRIPTION, "text");
            columnsMap.put(THUMBNAILS, "text");
            columnsMap.put(CHANNELTITLE, "text");
            columnsMap.put(LIVEBROADCASTCONTENT, "text");
            columnsMap.put(DEFAULTAUDIOLANGUAGE, "text");
        }
    }
}
