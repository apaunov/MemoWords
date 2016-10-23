package com.whitegems.memowords.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.whitegems.memowords.R;
import com.whitegems.memowords.activities.MainActivity;
import com.whitegems.memowords.fragments.ViewWordFragment;
import com.whitegems.memowords.interfaces.DatabaseReadInterface;
import com.whitegems.memowords.model.WordItem;
import com.whitegems.memowords.services.DatabaseReadService;
import com.whitegems.memowords.sql.DatabaseSQLiteHelper;
import com.whitegems.memowords.utilities.Utilities;

import java.util.ArrayList;

/**
 * Copyright © by Andrey on 11/15/2015.
 */
public class WordWidget extends AppWidgetProvider
{
    // Broadcast constants
    public static final String WORDS_REFRESHED = "com.paunov.andrey.WORDS_REFRESHED";

    // Shared preferences constants
    private static final String WIDGET_PREFS = "WIDGET_PREFS";
    private static final String NEXT_WORD = "NEXT_WORD";
    private static final String PREVIOUS_WORD = "PREVIOUS_WORD";
    private static final String SHOW_WORD_DETAILS = "SHOW_WORD_DETAILS";
    private static final String COUNT = "COUNT";
    private static final String TOTAL_RECORDS = "TOTAL_RECORDS";

    // Private variables
    private int[] appWidgetIds;
    private Context context;
    private AppWidgetManager appWidgetManager;
    private SharedPreferences sharedPreferences;

    private DatabaseReadInterface onDatabaseReadInterface = new DatabaseReadInterface()
    {
        @Override
        public void onDatabaseRead(ArrayList<WordItem> wordItemList)
        {
            if (wordItemList != null && !wordItemList.isEmpty())
            {
                int count = sharedPreferences.getInt(COUNT, 0);
                int totalRecords = wordItemList.size();

                for (int appWidgetId : appWidgetIds)
                {
                    Log.i("appWidgetId: ", appWidgetId + "");

                    // We will default the widget
                    // to the first word_item of the list.
                    WordItem displayedWordItem = wordItemList.get(count);
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

                    if (Utilities.convertIntToBoolean(displayedWordItem.isWordLearned()))
                    {
                        if (hasLearnedAllWordsFromList(wordItemList))
                        {
                            // Then notify the user that all current words in their list have been learned.
                            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_empty);
                            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                        }

                        // Learned words are removed from the total records and they will be skipped.
                        count--;
                        totalRecords--;
                        continue;
                    }

                    // Show the scroll arrows only if there is more than one word.
                    if (totalRecords > 1)
                    {
                        remoteViews.setViewVisibility(R.id.action_previous, View.VISIBLE);
                        remoteViews.setOnClickPendingIntent(R.id.action_previous, getPendingSelfIntent(context, PREVIOUS_WORD));
                        remoteViews.setOnClickPendingIntent(R.id.action_next, getPendingSelfIntent(context, NEXT_WORD));
                        remoteViews.setViewVisibility(R.id.action_next, View.VISIBLE);
                    }
                    else
                    {
                        remoteViews.setViewVisibility(R.id.action_previous, View.GONE);
                        remoteViews.setViewVisibility(R.id.action_next, View.GONE);
                    }

                    // A word should always be present, therefore, no isEmpty() check is necessary.
                    remoteViews.setTextViewText(R.id.widget_word, displayedWordItem.getText());
                    remoteViews.setOnClickPendingIntent(R.id.widget, getPendingSelfIntent(context, SHOW_WORD_DETAILS));

                    if (!displayedWordItem.getTranscription().isEmpty())
                    {
                        remoteViews.setTextViewText(R.id.widget_transcription, displayedWordItem.getTranscription());
                        remoteViews.setViewVisibility(R.id.widget_transcription, View.VISIBLE);
                    }
                    else
                    {
                        remoteViews.setViewVisibility(R.id.widget_transcription, View.GONE);
                    }

                    if (!displayedWordItem.getPartOfSpeech().isEmpty())
                    {
                        remoteViews.setTextViewText(R.id.widget_part_of_speech, displayedWordItem.getPartOfSpeech());
                        remoteViews.setViewVisibility(R.id.widget_part_of_speech, View.VISIBLE);
                    }
                    else
                    {
                        remoteViews.setViewVisibility(R.id.widget_part_of_speech, View.GONE);
                    }

                    if (!displayedWordItem.getDefinition().isEmpty())
                    {
                        remoteViews.setTextViewText(R.id.widget_definition, displayedWordItem.getDefinition());
                        remoteViews.setViewVisibility(R.id.widget_definition, View.VISIBLE);
                    }
                    else
                    {
                        remoteViews.setViewVisibility(R.id.widget_definition, View.GONE);
                    }

                    remoteViews.setTextViewText(R.id.widget_position_in_list, String.format("%s / %s", count + 1, totalRecords));

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(COUNT, count);
                    editor.putInt(TOTAL_RECORDS, totalRecords);
                    editor.apply();

                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                }
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        sharedPreferences = context.getSharedPreferences(WIDGET_PREFS, 0);
        int count = sharedPreferences.getInt(COUNT, 0);
        int totalRecords = sharedPreferences.getInt(TOTAL_RECORDS, 0);

        switch (intent.getAction())
        {
            case WORDS_REFRESHED:
                break;

            case PREVIOUS_WORD:
                if (count - 1 < 0)
                {
                    count = totalRecords - 1;
                }
                else
                {
                    count--;
                }
                break;

            case NEXT_WORD:
                if (count + 1 >= totalRecords)
                {
                    count = 0;
                }
                else
                {
                    count++;
                }
                break;

            case SHOW_WORD_DETAILS:
                // Create an intent to start the view word activity if the word contents are clicked.
                Intent viewWordIntent = new Intent(context, ViewWordFragment.class);
                viewWordIntent.putExtra(MainActivity.EXTRA_PARAM_WORD_POSITION, count);
                viewWordIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(viewWordIntent);
                break;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(COUNT, count);
        editor.putInt(TOTAL_RECORDS, totalRecords);
        editor.apply();

        updateVocab(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateVocab(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        sharedPreferences = context.getSharedPreferences(WIDGET_PREFS, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context)
    {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context)
    {
        super.onDisabled(context);
    }

    public void updateVocab(Context context)
    {
        ComponentName thisWidget = new ComponentName(context, WordWidget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        updateVocab(context, appWidgetManager, appWidgetIds);
    }

    public void updateVocab(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        this.context = context;
        this.appWidgetManager = appWidgetManager;
        this.appWidgetIds = appWidgetIds;

        databaseReadProcedure();
    }

    private void databaseReadProcedure()
    {
        DatabaseSQLiteHelper databaseSQLiteHelper = DatabaseSQLiteHelper.getInstance(context.getApplicationContext());
        DatabaseReadService databaseReadService = new DatabaseReadService(databaseSQLiteHelper);
        databaseReadService.setDatabaseReadInterface(onDatabaseReadInterface);
        databaseReadService.execute();
    }

    private PendingIntent getPendingSelfIntent(Context context, String action)
    {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private boolean hasLearnedAllWordsFromList(ArrayList<WordItem> wordItemList)
    {
        // Loop through the entire list of words and see if the user has a word they do not know.
        for (WordItem wordItem : wordItemList)
        {
            // There is at least one wordItem the user has not learned by heart yet.
            if (!Utilities.convertIntToBoolean(wordItem.isWordLearned()))
            {
                return false;
            }
        }

        // All the words in the list have been learned by the user.
        return true;
    }
}
