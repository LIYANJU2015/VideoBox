package com.videobox.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.commonlibs.provider.TableInfo;
import com.commonlibs.util.StringUtils;
import com.videobox.model.bean.PlayRecordBean;
import com.videobox.model.dailymotion.entity.DMChannelsBean;
import com.videobox.model.dailymotion.entity.DMVideoBean;
import com.videobox.model.youtube.entity.YTBCategoriesBean;
import com.videobox.model.youtube.entity.YTBLanguagesBean;
import com.videobox.model.youtube.entity.YTBVideoPageBean;
import com.videobox.model.youtube.entity.YTbRegionsBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.R.attr.type;
import static com.videobox.model.db.VideoBoxContract.DMVideo.THUMBNAIL_URL;
import static com.videobox.model.db.VideoBoxContract.SearchHistory.SEARCH_CONTENT;
import static com.videobox.model.db.VideoBoxContract.SearchHistory.SEARCH_TIME;
import static com.videobox.model.db.VideoBoxContract.YouTubeRgions.GL;

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
                    contentValues.put(THUMBNAILS, video.snippet.thumbnails.higth.url);
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

    public static class PlayRecord extends TableInfo {

        public static final String TABLE_NAME = "PlayRecord";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String PLAY_TYPE = "play_type";
        public static final String PLAY_TIME = "play_time";
        public static final String VIDEO_ID = "video_id";
        public static final String TITLE = "title";
        public static final String THUMBNAILURL = "thumbnailUrl";
        public static final String PLAYLISTID = "playlistId";
        public static final String TOTAL_TIME = "total_time";

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
            columnsMap.put(PLAY_TYPE, "int");
            columnsMap.put(PLAY_TIME, "int");
            columnsMap.put(VIDEO_ID, "text");
            columnsMap.put(TITLE, "text");
            columnsMap.put(THUMBNAILURL, "text");
            columnsMap.put(PLAYLISTID, "text");
            columnsMap.put(TOTAL_TIME, "int");
        }

        public static void addPlayRecord(Context context, PlayRecordBean recordBean) {
            Cursor cursor = null;
            try {
                String selection;
                if (!StringUtils.isEmpty(recordBean.playlistId)) {
                    selection = PLAY_TYPE + " = " + recordBean.type + " and " + PLAYLISTID + " = '" + recordBean.playlistId+"'";
                    cursor = context.getContentResolver().query(CONTENT_URI, null, selection, null, null);
                } else {
                    selection = PLAY_TYPE + " = " + recordBean.type + " and " + VIDEO_ID + " = '" + recordBean.vid + "'";
                    cursor = context.getContentResolver().query(CONTENT_URI, null, selection, null, null);
                }
                if (cursor != null && cursor.getCount() > 0) {
                    context.getContentResolver().update(CONTENT_URI, PlayRecordBean.toContentValues(recordBean), selection, null);
                } else {
                    context.getContentResolver().insert(CONTENT_URI, PlayRecordBean.toContentValues(recordBean));
                }
            }finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        public static long getPlayRecordTimeByVid(Context context, String vid, int type) {
            Cursor cursor = null;
            try {
                String selection = PLAY_TYPE + " = " + type + " and " + VIDEO_ID + " = '" + vid + "'";
                cursor = context.getContentResolver().query(CONTENT_URI, null, selection, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    return PlayRecordBean.getPlayTime(cursor);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return 0;
        }

        public static PlayRecordBean getPlayRecordTimeByPlaylistId(Context context, String playlistId, int type) {
            Cursor cursor = null;
            try {
                String selection = PLAY_TYPE + " = " + type + " and " + PLAYLISTID + "= '" + playlistId + "'";
                cursor = context.getContentResolver().query(CONTENT_URI, null, selection, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    return PlayRecordBean.cursorToPlayRecord(cursor);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return null;
        }
    }

    public static class SearchHistory extends TableInfo {

        public static final String TABLE_NAME = "SearchHistory";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

        public static final String SEARCH_CONTENT = "search_content";
        public static final String SEARCH_TIME = "search_time";

        public static ContentValues createContentValue(String searchHistory) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SEARCH_CONTENT, searchHistory);
            contentValues.put(SEARCH_TIME, String.valueOf(System.currentTimeMillis()));
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
            columnsMap.put(SEARCH_CONTENT, "text");
            columnsMap.put(SEARCH_TIME, "text");
        }

        public static String getSearchContent(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndex(SEARCH_CONTENT));
        }
        public static String getSearchTime(Cursor cursor) {
            return cursor.getString(cursor.getColumnIndex(SEARCH_TIME));
        }

        public static Cursor query(Context context, String selection) {
            return context.getContentResolver().query(CONTENT_URI, null, selection, null, SEARCH_TIME + " desc ");
        }

        public static Uri insert(Context context, ContentValues values) {
            return context.getContentResolver().insert(CONTENT_URI, values);
        }

        public static Uri insertNewHistroy(Context context, ContentValues values) {
            Cursor cursor = null;
            try {
                cursor = query(context, null);
                if (cursor != null && cursor.getCount() > 10) {
                    cursor.moveToLast();
                    String where = SEARCH_TIME + " = " + getSearchTime(cursor);
                    context.getContentResolver().delete(CONTENT_URI, where, null);
                }
                String selction = SEARCH_CONTENT + " = '" + values.getAsString(SEARCH_CONTENT)+"'";
                cursor = query(context, selction);
                if (cursor.getCount() > 0) {
                    context.getContentResolver().update(CONTENT_URI, values,
                            selction, null);
                } else {
                    return insert(context, values);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return null;
        }

        public static String[] getAllSearchHistory(Context context) {
            Cursor cursor = null;
            try {
                 cursor = query(context, null);
                if (cursor != null) {
                    String strs[] = new String[cursor.getCount()];
                    int i = 0;
                    while (cursor.moveToNext()) {
                        strs[i] = getSearchContent(cursor);
                        i++;
                    }
                    return strs;
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return new String[0];
        }
    }
}
