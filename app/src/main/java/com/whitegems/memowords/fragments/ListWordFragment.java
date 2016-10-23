package com.whitegems.memowords.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.whitegems.memowords.R;
import com.whitegems.memowords.adapters.WordListAdapter;
import com.whitegems.memowords.fragments.abstractFragments.WordFragment;
import com.whitegems.memowords.interfaces.DatabaseReadInterface;
import com.whitegems.memowords.interfaces.DatabaseWriteInterface;
import com.whitegems.memowords.model.WordDataContainer;
import com.whitegems.memowords.model.WordItem;
import com.whitegems.memowords.services.DatabaseReadService;
import com.whitegems.memowords.services.DatabaseWriteService;
import com.whitegems.memowords.sql.DatabaseSQLiteContract;
import com.whitegems.memowords.sql.DatabaseSQLiteHelper;
import com.whitegems.memowords.widget.WordWidget;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Copyright © by apaunov on 2016-07-19.
 */
public class ListWordFragment extends WordFragment
{
    public OnActivityListWordListeners onActivityListWordListeners;

    private AppCompatActivity appCompatActivity;
    private TextView defaultTextEmptyListView;
    private DatabaseSQLiteHelper databaseSQLiteHelper;
    private WordDataContainer wordDataContainer;
    private WordListAdapter wordListAdapter;
    private int wordMenuSelectedItemPosition;

    // FRAGMENT LISTENERS

    private View.OnClickListener onFragmentAddWordClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            onActivityListWordListeners.onActivityAddWordClickListener();
        }
    };
    private WordListAdapter.OnFragmentWordCardClickListener onFragmentWordCardClickListener = new WordListAdapter.OnFragmentWordCardClickListener()
    {
        @Override
        public void onItemClick(View view, int position)
        {
            onActivityListWordListeners.onActivityViewWordListener(position);
        }
    };
    private DatabaseReadInterface onFragmentDatabaseReadInterface = new DatabaseReadInterface()
    {
        @Override
        public void onDatabaseRead(ArrayList<WordItem> wordItemList)
        {
            wordDataContainer.setList(wordItemList);
            showDefaultText();
            wordListAdapter.notifyDataSetChanged();
        }
    };
    private DatabaseWriteInterface onFragmentDatabaseWriteInterface = new DatabaseWriteInterface()
    {
        @Override
        public void onDatabaseWrite(Integer responseCode)
        {
            switch (responseCode)
            {
                case DatabaseWriteService.DELETE_TOAST:
                    Toast.makeText(appCompatActivity.getApplicationContext(), "Successfully deleted.", Toast.LENGTH_LONG).show();
                    wordDataContainer.getList().remove(wordMenuSelectedItemPosition);
                    wordListAdapter.notifyItemRemoved(wordMenuSelectedItemPosition);
                    showDefaultText();
                    sendWidgetBroadcast();
                    break;

                case DatabaseWriteService.DELETE_ERROR_TOAST:
                    Toast.makeText(appCompatActivity.getApplicationContext(), "Delete unsuccessful.", Toast.LENGTH_LONG).show();
                    break;

                case DatabaseWriteService.UPDATE_TOAST:
                    sendWidgetBroadcast();
                    break;

                case DatabaseWriteService.DELETE_ALL_TOAST:
                    Toast.makeText(appCompatActivity.getApplicationContext(), "Successfully deleted all words.", Toast.LENGTH_LONG).show();
                    wordDataContainer.getList().clear();
                    wordListAdapter.notifyDataSetChanged();
                    showDefaultText();
                    sendWidgetBroadcast();
                    break;

                case DatabaseWriteService.DELETE_ALL_ERROR_TOAST:
                    Toast.makeText(appCompatActivity.getApplicationContext(), "Cannot delete all words.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    private WordListAdapter.OnFragmentLearnedWordClickListener onFragmentLearnedWordClickListener = new WordListAdapter.OnFragmentLearnedWordClickListener()
    {
        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(int wordPosition, boolean isLearned)
        {
            WordItem currentWordItem = wordDataContainer.getWord(wordPosition);
            HashMap<String, Object> dataFields = new HashMap<>();

            dataFields.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_TEXT, currentWordItem.getText());
            dataFields.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_TRANSCRIPTION, currentWordItem.getTranscription());
            dataFields.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_PART_OF_SPEECH, currentWordItem.getPartOfSpeech());
            dataFields.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_DEFINITION, currentWordItem.getDefinition());
            dataFields.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_LEARNED, isLearned);

            DatabaseWriteService databaseWriteService = new DatabaseWriteService(databaseSQLiteHelper, DatabaseWriteService.UPDATE_WORD, wordDataContainer.getWord(wordPosition).getId());
            databaseWriteService.setOnDatabaseWordWrite(onFragmentDatabaseWriteInterface);
            databaseWriteService.execute(dataFields);
        }
    };
    private WordListAdapter.OnFragmentWordMoreOptionsClickListener onFragmentWordMoreOptionsClickListener = new WordListAdapter.OnFragmentWordMoreOptionsClickListener()
    {
        @Override
        public void onItemClick(MenuItem item, int position)
        {
            switch (item.getItemId())
            {
                case R.id.action_delete:
                    wordMenuSelectedItemPosition = position;
                    DatabaseWriteService databaseWriteService = new DatabaseWriteService(databaseSQLiteHelper, DatabaseWriteService.DELETE_WORD, wordDataContainer.getWord(position).getId());
                    databaseWriteService.setOnDatabaseWordWrite(onFragmentDatabaseWriteInterface);
                    databaseWriteService.execute((HashMap) null);
                    break;
            }
        }
    };

    // OVERRIDE METHODS

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        try
        {
            onActivityListWordListeners = (OnActivityListWordListeners) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + " must implement OnActivityListWordListeners");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Retain this fragment
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_list_word, container, false);

        appCompatActivity = (AppCompatActivity) getActivity();

        databaseSQLiteHelper = DatabaseSQLiteHelper.getInstance(appCompatActivity.getApplicationContext());
        wordDataContainer = WordDataContainer.getInstance();

        // The first thing we do is to read data from the database.
        // We read a specified amount of data for performance purposes.
        databaseReadProcedure();
        sendWidgetBroadcast();

        // Create the word item recycleView and its layout manager
        RecyclerView wordItemRecyclerView = (RecyclerView) view.findViewById(R.id.added_words_list);
        LinearLayoutManager wordItemLinearLayoutManager = new LinearLayoutManager(appCompatActivity);

        wordListAdapter = new WordListAdapter(appCompatActivity, wordDataContainer);
        wordListAdapter.setOnFragmentWordCardClickListener(onFragmentWordCardClickListener);
        wordListAdapter.setOnFragmentLearnedWordClickListener(onFragmentLearnedWordClickListener);
        wordListAdapter.setOnFragmentWordMoreOptionsClickListener(onFragmentWordMoreOptionsClickListener);

        wordItemRecyclerView.setLayoutManager(wordItemLinearLayoutManager);
        wordItemRecyclerView.setAdapter(wordListAdapter);

        defaultTextEmptyListView = (TextView) view.findViewById(R.id.default_text_empty_list);

        ImageButton addWordButton = (ImageButton) view.findViewById(R.id.add_word_button);
        addWordButton.setOnClickListener(onFragmentAddWordClickListener);

        return view;
    }

    // INTERNAL METHODS

    public void deleteAllWords()
    {
        DatabaseWriteService databaseWriteService = new DatabaseWriteService(databaseSQLiteHelper, DatabaseWriteService.DELETE_ALL_WORDS, 0);
        databaseWriteService.setOnDatabaseWordWrite(onFragmentDatabaseWriteInterface);
        databaseWriteService.execute((HashMap) null);
    }

    private void databaseReadProcedure()
    {
        DatabaseReadService databaseReadService = new DatabaseReadService(databaseSQLiteHelper);
        databaseReadService.setDatabaseReadInterface(onFragmentDatabaseReadInterface);
        databaseReadService.execute();
    }

    private void sendWidgetBroadcast()
    {
        // Update the widget after a new change in the database has occurred
        Intent intent = new Intent(WordWidget.WORDS_REFRESHED);
        appCompatActivity.sendBroadcast(intent);
    }

    private void showDefaultText()
    {
        defaultTextEmptyListView.setVisibility(View.GONE);

        if (wordDataContainer.getList().isEmpty())
        {
            defaultTextEmptyListView.setVisibility(View.VISIBLE);
        }
    }

    // ACTIVITY LISTENERS

    public interface OnActivityListWordListeners
    {
        void onActivityAddWordClickListener();

        void onActivityViewWordListener(int position);
    }
}