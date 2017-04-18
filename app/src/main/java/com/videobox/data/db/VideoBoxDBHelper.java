package com.videobox.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by liyanju on 2017/4/17.
 */

public class VideoBoxDBHelper extends SQLiteOpenHelper{


    public VideoBoxDBHelper(Context context) {
        super(context, VideoBoxContract.DATABASE_NAME, null, VideoBoxContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VideoBoxContract.DMChannels.TABLE_CREATE);
        db.execSQL(VideoBoxContract.DMVideo.TABLE_CREATE);
        db.execSQL(VideoBoxContract.YouTubeCategories.TABLE_CREATE);
        db.execSQL(VideoBoxContract.YouTubeLanguages.TABLE_CREATE);
        db.execSQL(VideoBoxContract.YouTubeRgions.TABLE_CREATE);
        db.execSQL(VideoBoxContract.YouTubeVideo.TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
