package com.whitegems.memowords.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.whitegems.memowords.R;
import com.whitegems.memowords.activities.MainActivity;
import com.whitegems.memowords.fragments.abstractFragments.WordFragment;
import com.whitegems.memowords.interfaces.DatabaseReadInterface;
import com.whitegems.memowords.model.WordDataContainer;
import com.whitegems.memowords.model.WordItem;
import com.whitegems.memowords.services.DatabaseReadService;
import com.whitegems.memowords.sql.DatabaseSQLiteHelper;
import com.whitegems.memowords.utilities.Utilities;
import com.whitegems.memowords.widget.WordWidget;

import java.util.ArrayList;

/**
 * Copyright © by Andrey on 1/1/2016.
 */
public class ViewWordFragment extends WordFragment
{
    public OnActivityViewWordListeners onActivityViewWordListeners;

    private AppCompatActivity appCompatActivity;
    private View view;
    private Bundle extras;
    private WordDataContainer wordDataContainer;
    private DatabaseSQLiteHelper databaseSQLiteHelper;

    private DatabaseReadInterface onDatabaseReadInterface = new DatabaseReadInterface()
    {
        @Override
        public void onDatabaseRead(ArrayList<WordItem> wordItemList)
        {
            // This method is called only when
            // saving a new or modifying an current word.
            wordDataContainer.setList(wordItemList);
            showWordData();
            sendWidgetBroadcast();
        }
    };

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        try
        {
            onActivityViewWordListeners = (OnActivityViewWordListeners) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + " must implement OnActivityViewWordListeners");
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
        view = inflater.inflate(R.layout.fragment_view_word, container, false);

        appCompatActivity = (AppCompatActivity) getActivity();

        extras = getArguments();

        databaseSQLiteHelper = DatabaseSQLiteHelper.getInstance(appCompatActivity);
        wordDataContainer = WordDataContainer.getInstance();

        databaseReadProcedure();

        return view;
    }

    // INTERNAL METHODS

    public void databaseReadProcedure()
    {
        DatabaseReadService databaseReadService = new DatabaseReadService(databaseSQLiteHelper);
        databaseReadService.setDatabaseReadInterface(onDatabaseReadInterface);
        databaseReadService.execute();
    }

    private void showWordData()
    {
        if (extras != null)
        {
            int wordPosition = extras.getInt(MainActivity.EXTRA_PARAM_WORD_POSITION);

            WordItem extractedWordItem = wordDataContainer.getWord(wordPosition);
            String wordTranscription = extractedWordItem.getTranscription();

            TextView wordTextView = (TextView) view.findViewById(R.id.word_text);
            TextView wordTranscriptionView = (TextView) view.findViewById(R.id.word_transcription_text);
            TextView wordPartOfSpeechView = (TextView) view.findViewById(R.id.word_part_of_speech);
            TextView wordDefinitionView = (TextView) view.findViewById(R.id.word_definition_text);
            Switch wordLearnedSwitch = (Switch) view.findViewById(R.id.word_learned_switch);
            ImageButton editWordButton = (ImageButton) view.findViewById(R.id.add_word_button);

            wordLearnedSwitch.setEnabled(false);
            wordLearnedSwitch.setChecked(Utilities.convertIntToBoolean(extractedWordItem.isWordLearned()));
            wordLearnedSwitch.setVisibility(View.VISIBLE);

            editWordButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onActivityViewWordListeners.onActivityEditWordListener(extras);
                }
            });

            if (!wordTranscription.isEmpty())
            {
                // Special logic for transcription to remove the square brackets
                wordTranscription = wordTranscription.substring(1, wordTranscription.length() - 1);
                wordTranscriptionView.setText(wordTranscription);
                wordTranscriptionView.setVisibility(View.VISIBLE);
            }

            // Display on screen through its views

            if (!extractedWordItem.getText().isEmpty())
            {
                wordTextView.setText(extractedWordItem.getText());
                wordTextView.setVisibility(View.VISIBLE);
            }

            if (!extractedWordItem.getPartOfSpeech().isEmpty())
            {
                wordPartOfSpeechView.setText(extractedWordItem.getPartOfSpeech());
                wordPartOfSpeechView.setVisibility(View.VISIBLE);
            }

            if (!extractedWordItem.getDefinition().isEmpty())
            {
                wordDefinitionView.setText(extractedWordItem.getDefinition());
                wordDefinitionView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void sendWidgetBroadcast()
    {
        // Update the widget after a new change in the database has occurred
        Intent intent = new Intent(WordWidget.WORDS_REFRESHED);
        appCompatActivity.sendBroadcast(intent);
    }

    // ACTIVITY LISTENERS

    public interface OnActivityViewWordListeners
    {
        void onActivityEditWordListener(Bundle extras);
    }
}