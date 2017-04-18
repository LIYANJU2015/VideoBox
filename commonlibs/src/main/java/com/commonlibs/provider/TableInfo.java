package com.commonlibs.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by liyanju on 2017/4/17.
 */

public abstract class TableInfo implements BaseColumns{

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    private static final String LEFT_BRACKET = "(";
    private static final String RIGHT_BRACKET = ");";
    private static final String COMMA = ",";

    private LinkedHashMap<String, String> mMapTable = new LinkedHashMap<>(8);

    public abstract String onTableName();

    public abstract Uri onContentUri();

    public abstract void onInitColumnsMap(Map<String, String> columnsMap);

    public String getCreateTableSql(){
        mMapTable.clear();
        onInitColumnsMap(mMapTable);
        if (mMapTable.size() == 0) {
            throw new IllegalArgumentException("mMapTable size == 0");
        }
        return genTableCreationSql(onTableName(), mMapTable);
    }

    public String genTableCreationSql(String tableName, HashMap<String, String> mapTable) {
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
