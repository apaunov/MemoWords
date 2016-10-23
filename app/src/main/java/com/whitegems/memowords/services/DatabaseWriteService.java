package com.whitegems.memowords.services;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.whitegems.memowords.interfaces.DatabaseWriteInterface;
import com.whitegems.memowords.sql.DatabaseSQLiteContract;
import com.whitegems.memowords.sql.DatabaseSQLiteHelper;
import com.whitegems.memowords.utilities.Utilities;

import java.util.HashMap;

/**
 * Copyright © by apaunov on 2015-11-11.
 */
public class DatabaseWriteService extends AsyncTask<HashMap<String, Object>, Void, Integer>
{
    // ================================================== DB WORD MODIFICATION CONSTANTS ==================================================
    public static final int INSERT_WORD = 0;
    public static final int UPDATE_WORD = 1;
    public static final int DELETE_WORD = 2;
    public static final int DELETE_ALL_WORDS = 3;

    // ================================================== DB TOAST RESULT CONSTANTS ==================================================
    public static final int DEFAULT_ERROR_TOAST = -1;
    public static final int INSERT_TOAST = 0;
    public static final int INSERT_ERROR_TOAST = 1;
    public static final int ERROR_EMPTY_WORD_TOAST = 2;
    public static final int DELETE_TOAST = 3;
    public static final int DELETE_ERROR_TOAST = 4;
    public static final int UPDATE_TOAST = 5;
    public static final int UPDATE_ERROR_TOAST = 6;
    public static final int DELETE_ALL_TOAST = 7;
    public static final int DELETE_ALL_ERROR_TOAST = 8;

    // ================================================== PRIVATE VARIABLES ==================================================
    private int methodType;
    private int wordId;
    private DatabaseSQLiteHelper databaseSQLiteHelper;
    private DatabaseWriteInterface databaseWriteInterface;

    public DatabaseWriteService(DatabaseSQLiteHelper databaseSQLiteHelper, int methodType, int wordId)
    {
        this.databaseSQLiteHelper = databaseSQLiteHelper;
        this.methodType = methodType;
        this.wordId = wordId;
    }

    public void setOnDatabaseWordWrite(DatabaseWriteInterface databaseWriteInterface)
    {
        this.databaseWriteInterface = databaseWriteInterface;
    }

    @SafeVarargs
    @Override
    protected final Integer doInBackground(HashMap<String, Object>... dataFields)
    {
        long status;
        int result = DEFAULT_ERROR_TOAST;

        // Common for all switch cases below
        SQLiteDatabase db = databaseSQLiteHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String selection = DatabaseSQLiteContract.WordEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(wordId)};

        if (methodType != DELETE_WORD && methodType != DELETE_ALL_WORDS)
        {
            String word = (String) dataFields[0].get(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_TEXT);
            String transcription = (String) dataFields[0].get(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_TRANSCRIPTION);
            String partOfSpeech = (String) dataFields[0].get(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_PART_OF_SPEECH);
            String definition = (String) dataFields[0].get(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_DEFINITION);
            int wordLearned = Utilities.convertBooleanToInt((boolean) dataFields[0].get(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_LEARNED));

            if (word == null || word.isEmpty())
            {
                return ERROR_EMPTY_WORD_TOAST;
            }

            if (transcription != null && !transcription.isEmpty())
            {
                // Only add square brackets if there is an actual transcription.
                transcription = String.format("[%s]", transcription);
            }

            contentValues.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_TEXT, word);
            contentValues.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_TRANSCRIPTION, transcription);
            contentValues.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_PART_OF_SPEECH, partOfSpeech);
            contentValues.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_DEFINITION, definition);
            contentValues.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_LEARNED, wordLearned);
        }

        switch (methodType)
        {
            case INSERT_WORD:
                status = db.insert(DatabaseSQLiteContract.WordEntry.TABLE_NAME, null, contentValues);
                result = (status > 0L) ? INSERT_TOAST : INSERT_ERROR_TOAST;
                break;

            case UPDATE_WORD:
                status = db.update(DatabaseSQLiteContract.WordEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                result = (status > 0L) ? UPDATE_TOAST : UPDATE_ERROR_TOAST;
                break;

            case DELETE_WORD:
                status = db.delete(DatabaseSQLiteContract.WordEntry.TABLE_NAME, selection, selectionArgs);
                result = (status > 0L) ? DELETE_TOAST : DELETE_ERROR_TOAST;
                break;

            case DELETE_ALL_WORDS:
                status = db.delete(DatabaseSQLiteContract.WordEntry.TABLE_NAME, null, null);
                result = (status > 0L) ? DELETE_ALL_TOAST : DELETE_ALL_ERROR_TOAST;
                break;
        }

        db.close();
        return result;
    }

    @Override
    protected void onPostExecute(Integer responseCode)
    {
        databaseWriteInterface.onDatabaseWrite(responseCode);
        super.onPostExecute(responseCode);
    }
}