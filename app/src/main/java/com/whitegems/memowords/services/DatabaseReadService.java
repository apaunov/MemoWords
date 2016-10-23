package com.whitegems.memowords.services;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.whitegems.memowords.interfaces.DatabaseReadInterface;
import com.whitegems.memowords.model.WordItem;
import com.whitegems.memowords.sql.DatabaseSQLiteContract;
import com.whitegems.memowords.sql.DatabaseSQLiteHelper;

import java.util.ArrayList;

/**
 * Copyright © by apaunov on 2015-11-10.
 */
public class DatabaseReadService extends AsyncTask<Void, Void, ArrayList<WordItem>>
{
    // ================================================== PRIVATE VARIABLES ==================================================
    private DatabaseSQLiteHelper databaseSQLiteHelper;
    private DatabaseReadInterface databaseReadInterface;

    public DatabaseReadService(DatabaseSQLiteHelper databaseSQLiteHelper)
    {
        this.databaseSQLiteHelper = databaseSQLiteHelper;
    }

    public void setDatabaseReadInterface(DatabaseReadInterface databaseReadInterface)
    {
        this.databaseReadInterface = databaseReadInterface;
    }

    @Override
    protected ArrayList<WordItem> doInBackground(Void... params)
    {
        SQLiteDatabase db = databaseSQLiteHelper.getReadableDatabase();

        // The columns we are interested in
        String[] selection =
                {
                        DatabaseSQLiteContract.WordEntry._ID,
                        DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_TEXT,
                        DatabaseSQLiteContract.WordEntry.COLUMN_NAME_PART_OF_SPEECH,
                        DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_TRANSCRIPTION,
                        DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_DEFINITION,
                        DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_LEARNED
                };

        ArrayList<WordItem> wordItemList = new ArrayList<>();
        String sortOrder = DatabaseSQLiteContract.WordEntry._ID + " ASC";
        Cursor cursor = db.query(DatabaseSQLiteContract.WordEntry.TABLE_NAME, selection, null, null, null, null, sortOrder);

        int totalRecordsCount = cursor.getCount();

        if (totalRecordsCount > 0)
        {
            cursor.moveToFirst();

            do
            {
                int wordId = cursor.getInt(cursor.getColumnIndex(DatabaseSQLiteContract.WordEntry._ID));
                String wordText = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_TEXT));
                String partOfSpeech = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_PART_OF_SPEECH));
                String transcription = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_TRANSCRIPTION));
                String definition = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_DEFINITION));
                int wordLearned = cursor.getInt(cursor.getColumnIndex(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_LEARNED));

                wordItemList.add(new WordItem(wordId, wordText, partOfSpeech, transcription, definition, wordLearned));
            }
            while (cursor.moveToNext());
        }

        cursor.close();

        return wordItemList;
    }

    @Override
    protected void onPostExecute(ArrayList<WordItem> wordItemList)
    {
        databaseReadInterface.onDatabaseRead(wordItemList);
        super.onPostExecute(wordItemList);
    }
}