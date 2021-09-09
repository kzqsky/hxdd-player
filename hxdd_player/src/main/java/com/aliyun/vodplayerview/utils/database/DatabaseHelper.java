package com.aliyun.vodplayerview.utils.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类
 *
 * @author hanyu
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mInstance = null;

    /**
     * 数据库版本
     */
    private static int DATABASE_VERSION = 3;


    public DatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }


    public static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DatabaseHelper.class) {
                if (mInstance == null) {
                    mInstance = new DatabaseHelper(context, DatabaseManager.DB_NAME, DATABASE_VERSION);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = DatabaseManager.CREATE_TABLE_SQL;
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 || oldVersion == 2) {
            db.execSQL(DatabaseManager.UPDATE_TABLE_SQL);
        }
    }

}
