package com.commonlibs.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.commonlibs.util.LogUtils;

import java.util.ArrayList;

/**
 * Created by liyanju on 2017/4/17.
 */

public abstract class BaseContentProvider extends ContentProvider {

    private SQLiteOpenHelper mSqLiteOpenHelper;

    private SparseArray<TableInfo> mTableInfoArray = new SparseArray();

    private UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private Context mContext;

    public abstract void onAddTableInfo(SparseArray<TableInfo> tableInfoArray);

    public abstract String onDataBaseName();

    public abstract int onDataBaseVersion();

    public boolean onDBCreate(SQLiteDatabase db) {
        return false;
    }

    public abstract void onDBUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    private void addUriMatcher() {
        for (int i = 0; i < mTableInfoArray.size(); i++) {
            TableInfo tableInfo = mTableInfoArray.valueAt(i);
            int code = mTableInfoArray.keyAt(i);
            String path = tableInfo.onContentUri().getPath();
            path = path.substring(path.indexOf("/") +1, path.length());
            URI_MATCHER.addURI(tableInfo.onContentUri().getAuthority(),
                    path, code);
            URI_MATCHER.addURI(tableInfo.onContentUri().getAuthority(),
                    path + "/#", code);
            LogUtils.v("addUriMatcher", " getAuthority " + tableInfo.onContentUri().getAuthority()
                    + " getPath " + path);
        }
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        onAddTableInfo(mTableInfoArray);
        addUriMatcher();
        mSqLiteOpenHelper = createSqliteOpenHelper(mContext);
        return true;
    }

    private SQLiteOpenHelper createSqliteOpenHelper(Context context) {
        mSqLiteOpenHelper = new SQLiteOpenHelper(context, onDataBaseName(), null, onDataBaseVersion()) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                if (!onDBCreate(db)) {
                    for (int i = 0; i < mTableInfoArray.size(); i++) {
                        TableInfo tableInfo = mTableInfoArray.valueAt(i);
                        db.execSQL(tableInfo.getCreateTableSql());
                    }
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                onDBUpgrade(db, oldVersion, newVersion);
            }
        };
        return mSqLiteOpenHelper;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        TableInfo tableInfo = getTableInfoByUri(uri);
        qb.setTables(tableInfo.onTableName());

        SQLiteDatabase db = mSqLiteOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        if (c != null) {
            c.setNotificationUri(mContext.getContentResolver(), uri);
        }
        return c;
    }

    private TableInfo getTableInfoByUri(Uri uri) {
        int code = URI_MATCHER.match(uri);
        TableInfo tableInfo = mTableInfoArray.get(code);
        if (tableInfo == null) {
            throw new IllegalArgumentException(" TableInfoArray not find TableInfo !!!");
        }
        return tableInfo;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mSqLiteOpenHelper.getWritableDatabase();

        TableInfo tableInfo = getTableInfoByUri(uri);
        long rowId = db.insert(tableInfo.onTableName(), null, values);

        if (rowId > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, rowId);
            mContext.getContentResolver().notifyChange(uri, null);
            return newUri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mSqLiteOpenHelper.getWritableDatabase();

        TableInfo tableInfo = getTableInfoByUri(uri);
        int count = db.delete(tableInfo.onTableName(), selection, selectionArgs);

        mContext.getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mSqLiteOpenHelper.getWritableDatabase();

        TableInfo tableInfo = getTableInfoByUri(uri);
        int count = db.update(tableInfo.onTableName(), values, selection, selectionArgs);

        mContext.getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numValues = 0;
        SQLiteDatabase db = mSqLiteOpenHelper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            TableInfo tableInfo = getTableInfoByUri(uri);

            numValues = values.length;
            for (int i = 0; i < numValues; i++) {
                db.insert(tableInfo.onTableName(), null, values[i]);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return numValues;
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase db = mSqLiteOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return results;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return null;
    }
}
