package com.videobox.data.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;



/**
 * Created by liyanju on 2017/4/17.
 */

public class VideoBoxContentProvider extends ContentProvider{

    private static UriMatcher URI_MATCHER;

    private static final int DMCHANNEL_CODE = 100;
    private static final int DMVIDEO_CODE = 101;
    private static final int CATEGORIES_CODE = 102;
    private static final int LAUGUAGES_CODE = 103;
    private static final int REGIONS_CODE = 104;
    private static final int VIDEO_CODE = 105;

    private VideoBoxDBHelper mVideoBoxDBHelper;

    private Context mContext;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(VideoBoxContract.AUTHORITY, VideoBoxContract.DMChannels.TABLE_NAME, DMCHANNEL_CODE);
        URI_MATCHER.addURI(VideoBoxContract.AUTHORITY, VideoBoxContract.DMVideo.TABLE_NAME, DMVIDEO_CODE);
        URI_MATCHER.addURI(VideoBoxContract.AUTHORITY, VideoBoxContract.YouTubeCategories.TABLE_NAME, CATEGORIES_CODE);
        URI_MATCHER.addURI(VideoBoxContract.AUTHORITY, VideoBoxContract.YouTubeLanguages.TABLE_NAME, LAUGUAGES_CODE);
        URI_MATCHER.addURI(VideoBoxContract.AUTHORITY, VideoBoxContract.YouTubeRgions.TABLE_NAME, REGIONS_CODE);
        URI_MATCHER.addURI(VideoBoxContract.AUTHORITY, VideoBoxContract.YouTubeVideo.TABLE_NAME, VIDEO_CODE);
    }

    @Override
    public boolean onCreate() {
        mVideoBoxDBHelper = new VideoBoxDBHelper(getContext());
        mContext = getContext();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (URI_MATCHER.match(uri)) {
            case DMCHANNEL_CODE:
                qb.setTables(VideoBoxContract.DMChannels.TABLE_NAME);
                break;
            case DMVIDEO_CODE:
                qb.setTables(VideoBoxContract.DMVideo.TABLE_NAME);
                break;
            case CATEGORIES_CODE:
                qb.setTables(VideoBoxContract.YouTubeCategories.TABLE_NAME);
                break;
            case LAUGUAGES_CODE:
                qb.setTables(VideoBoxContract.YouTubeLanguages.TABLE_NAME);
                break;
            case REGIONS_CODE:
                qb.setTables(VideoBoxContract.YouTubeRgions.TABLE_NAME);
                break;
            case VIDEO_CODE:
                qb.setTables(VideoBoxContract.YouTubeVideo.TABLE_NAME);
                break;
            default:
                throw new IllegalStateException("Unknown URL: " + uri.toString());
        }

        SQLiteDatabase db = mVideoBoxDBHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        if (c != null) {
            c.setNotificationUri(mContext.getContentResolver(), uri);
        }
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId = 0;
        SQLiteDatabase db = mVideoBoxDBHelper.getWritableDatabase();
        String table;
        Uri contentUri;

        switch (URI_MATCHER.match(uri)) {
            case DMCHANNEL_CODE:
                table = VideoBoxContract.DMChannels.TABLE_NAME;
                contentUri = VideoBoxContract.DMChannels.CONTENT_URI;
                rowId = db.insert(table, null, values);
                break;
            case DMVIDEO_CODE:
                table = VideoBoxContract.DMVideo.TABLE_NAME;
                contentUri = VideoBoxContract.DMVideo.CONTENT_URI;
                rowId = db.insert(table, null, values);
                break;
            case CATEGORIES_CODE:
                table = VideoBoxContract.YouTubeCategories.TABLE_NAME;
                contentUri = VideoBoxContract.YouTubeCategories.CONTENT_URI;
                rowId = db.insert(table, null, values);
                break;
            case LAUGUAGES_CODE:
                table = VideoBoxContract.YouTubeLanguages.TABLE_NAME;
                contentUri = VideoBoxContract.YouTubeLanguages.CONTENT_URI;
                rowId = db.insert(table, null, values);
                break;
            case REGIONS_CODE:
                table = VideoBoxContract.YouTubeRgions.TABLE_NAME;
                contentUri = VideoBoxContract.YouTubeRgions.CONTENT_URI;
                rowId = db.insert(table, null, values);
                break;
            case VIDEO_CODE:
                table = VideoBoxContract.YouTubeVideo.TABLE_NAME;
                contentUri = VideoBoxContract.YouTubeVideo.CONTENT_URI;
                rowId = db.insert(table, null, values);
                break;
            default:
                throw new UnsupportedOperationException("Invalid URI " + uri);
        }

        if (rowId > 0) {
            Uri newUri = ContentUris.withAppendedId(contentUri, rowId);
            mContext.getContentResolver().notifyChange(uri, null);
            return newUri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mVideoBoxDBHelper.getWritableDatabase();
        String table;
        switch (URI_MATCHER.match(uri)) {
            case DMCHANNEL_CODE:
                table = VideoBoxContract.DMChannels.TABLE_NAME;
                break;
            case DMVIDEO_CODE:
                table = VideoBoxContract.DMVideo.TABLE_NAME;
                break;
            case CATEGORIES_CODE:
                table = VideoBoxContract.YouTubeCategories.TABLE_NAME;
                break;
            case LAUGUAGES_CODE:
                table = VideoBoxContract.YouTubeLanguages.TABLE_NAME;
                break;
            case REGIONS_CODE:
                table = VideoBoxContract.YouTubeRgions.TABLE_NAME;
                break;
            case VIDEO_CODE:
                table = VideoBoxContract.YouTubeVideo.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Invalid URI " + uri);
        }

        int count = db.delete(table, selection, selectionArgs);

        mContext.getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        String tableName = "";
        SQLiteDatabase db = mVideoBoxDBHelper.getWritableDatabase();

        switch (URI_MATCHER.match(uri)) {
            case DMCHANNEL_CODE:
                tableName = VideoBoxContract.DMChannels.TABLE_NAME;
                break;
            case DMVIDEO_CODE:
                tableName = VideoBoxContract.DMVideo.TABLE_NAME;
                break;
            case CATEGORIES_CODE:
                tableName = VideoBoxContract.YouTubeCategories.TABLE_NAME;
                break;
            case LAUGUAGES_CODE:
                tableName = VideoBoxContract.YouTubeLanguages.TABLE_NAME;
                break;
            case REGIONS_CODE:
                tableName = VideoBoxContract.YouTubeRgions.TABLE_NAME;
                break;
            case VIDEO_CODE:
                tableName = VideoBoxContract.YouTubeVideo.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Invalid URI " + uri);
        }

        count = db.update(tableName, values, selection, selectionArgs);

        mContext.getContentResolver().notifyChange(uri, null);

        return count;
    }
}
