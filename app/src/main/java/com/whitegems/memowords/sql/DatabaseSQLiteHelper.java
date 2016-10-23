package com.whitegems.memowords.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Andrey on 10/17/2015.
 */
public class DatabaseSQLiteHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WordReader.db";
    private static DatabaseSQLiteHelper databaseSQLiteHelperInstance;

    private DatabaseSQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseSQLiteHelper getInstance(Context context)
    {
        if (databaseSQLiteHelperInstance == null)
        {
            databaseSQLiteHelperInstance = new DatabaseSQLiteHelper(context.getApplicationContext());
        }

        return databaseSQLiteHelperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DatabaseSQLiteContract.WordEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Deletes the old data base and creates a new one
        db.execSQL(DatabaseSQLiteContract.WordEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }
}
