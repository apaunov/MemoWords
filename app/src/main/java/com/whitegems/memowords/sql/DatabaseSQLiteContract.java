package com.whitegems.memowords.sql;

import android.provider.BaseColumns;

/**
 * Created by Andrey on 10/17/2015.
 */
public class DatabaseSQLiteContract
{
    private static final String INT_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String COMMA_SEP = ",";

    // Empty constructor to prevent accidental instantiation
    public DatabaseSQLiteContract()
    {
    }

    public static abstract class WordEntry implements BaseColumns
    {
        // Columns
        public static final String TABLE_NAME = "WORD";
        public static final String COLUMN_NAME_WORD_TEXT = "word_text";
        public static final String COLUMN_NAME_WORD_TRANSCRIPTION = "transcription";
        public static final String COLUMN_NAME_PART_OF_SPEECH = "part_of_speech";
        public static final String COLUMN_NAME_WORD_DEFINITION = "definition";
        public static final String COLUMN_NAME_WORD_LEARNED = "word_learned";

        // Table
        public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
                WordEntry.TABLE_NAME + " (" +
                _ID + INT_TYPE + PRIMARY_KEY + COMMA_SEP +
                COLUMN_NAME_WORD_TEXT + TEXT_TYPE + " NOT NULL" + COMMA_SEP +
                COLUMN_NAME_PART_OF_SPEECH + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_WORD_TRANSCRIPTION + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_WORD_DEFINITION + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_WORD_LEARNED + INT_TYPE + ")";
        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
