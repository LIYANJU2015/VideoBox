package com.videobox.model.bean;

import android.content.ContentValues;
import android.database.Cursor;

import com.videobox.model.db.VideoBoxContract;

/**
 * Created by liyanju on 2017/5/11.
 */

public class PlayRecordBean {

    public int type;
    public static final int YOUTUBE_TYPE = 0;
    public static final int DAILYMOTION_TYPE = 1;

    public long playTime;

    public String vid;

    public String title;

    public String thumbnailUrl;

    public String playlistId;

    public long totalTime;

    public static ContentValues toContentValues(PlayRecordBean playRecordBean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VideoBoxContract.PlayRecord.PLAY_TIME, playRecordBean.playTime);
        contentValues.put(VideoBoxContract.PlayRecord.PLAY_TYPE, playRecordBean.type);
        contentValues.put(VideoBoxContract.PlayRecord.VIDEO_ID, playRecordBean.vid);
        contentValues.put(VideoBoxContract.PlayRecord.THUMBNAILURL, playRecordBean.thumbnailUrl);
        contentValues.put(VideoBoxContract.PlayRecord.TITLE, playRecordBean.title);
        contentValues.put(VideoBoxContract.PlayRecord.PLAYLISTID, playRecordBean.playlistId);
        return contentValues;
    }

    public static PlayRecordBean cursorToPlayRecord(Cursor cursor) {
        PlayRecordBean playRecordBean = new PlayRecordBean();
        playRecordBean.playlistId = cursor.getString(cursor.getColumnIndexOrThrow(VideoBoxContract.PlayRecord.PLAYLISTID));
        playRecordBean.playTime = cursor.getLong(cursor.getColumnIndexOrThrow(VideoBoxContract.PlayRecord.PLAY_TIME));
        playRecordBean.thumbnailUrl = cursor.getString(cursor.getColumnIndexOrThrow(VideoBoxContract.PlayRecord.THUMBNAILURL));
        playRecordBean.title = cursor.getString(cursor.getColumnIndexOrThrow(VideoBoxContract.PlayRecord.TITLE));
        playRecordBean.type = cursor.getInt(cursor.getColumnIndexOrThrow(VideoBoxContract.PlayRecord.PLAY_TYPE));
        playRecordBean.vid = cursor.getString(cursor.getColumnIndexOrThrow(VideoBoxContract.PlayRecord.VIDEO_ID));
        playRecordBean.totalTime = cursor.getLong(cursor.getColumnIndexOrThrow(VideoBoxContract.PlayRecord.TOTAL_TIME));
        return  playRecordBean;
    }

    public static String getTitle(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(VideoBoxContract.PlayRecord.TITLE));
    }

    public static String getThumbnailUrl(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(VideoBoxContract.PlayRecord.THUMBNAILURL));
    }

    public static long getPlayTime(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(VideoBoxContract.PlayRecord.PLAY_TIME));
    }


}

