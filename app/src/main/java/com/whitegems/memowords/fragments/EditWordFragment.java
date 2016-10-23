package com.whitegems.memowords.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.whitegems.memowords.R;
import com.whitegems.memowords.activities.MainActivity;
import com.whitegems.memowords.fragments.abstractFragments.WordFragment;
import com.whitegems.memowords.interfaces.DatabaseWriteInterface;
import com.whitegems.memowords.model.WordDataContainer;
import com.whitegems.memowords.model.WordItem;
import com.whitegems.memowords.services.DatabaseWriteService;
import com.whitegems.memowords.sql.DatabaseSQLiteContract;
import com.whitegems.memowords.sql.DatabaseSQLiteHelper;
import com.whitegems.memowords.utilities.Utilities;

import java.util.HashMap;

/**
 * Copyright © by Andrey on 10/4/2015.
 */
public class EditWordFragment extends WordFragment
{
    public OnActivityEditWordListeners onActivityEditWordListeners;

    private EditText wordTextView;
    private String wordPartOfSpeechString = "";
    private EditText wordTranscriptionView;
    private EditText wordDefinitionView;
    private Switch wordLearnedSwitch;
    private DatabaseSQLiteHelper databaseSQLiteHelper;
    private WordDataContainer wordDataContainer;
    private int wordPosition = -1;

    // DATABASE METHODS

    private DatabaseWriteInterface databaseWriteInterface = new DatabaseWriteInterface()
    {
        @Override
        public void onDatabaseWrite(Integer responseCode)
        {
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();

            switch (responseCode)
            {
                case DatabaseWriteService.DEFAULT_ERROR_TOAST:
                    Toast.makeText(appCompatActivity.getApplicationContext(), "Internal error occurred. Please try again.", Toast.LENGTH_LONG).show();
                    break;

                case DatabaseWriteService.INSERT_TOAST:
                    Toast.makeText(appCompatActivity.getApplicationContext(), "Save completed.", Toast.LENGTH_LONG).show();
                    onActivityEditWordListeners.onActivityInsertWordListener();
                    break;

                case DatabaseWriteService.UPDATE_TOAST:
                    Toast.makeText(appCompatActivity.getApplicationContext(), "Save completed.", Toast.LENGTH_LONG).show();
                    onActivityEditWordListeners.onActivityUpdateWordListener();
                    break;

                case DatabaseWriteService.INSERT_ERROR_TOAST:
                case DatabaseWriteService.UPDATE_ERROR_TOAST:
                    Toast.makeText(appCompatActivity.getApplicationContext(), "Save unsuccessful. Please try again.", Toast.LENGTH_LONG).show();
                    break;

                case DatabaseWriteService.ERROR_EMPTY_WORD_TOAST:
                    Toast.makeText(appCompatActivity.getApplicationContext(), "Please enter a word to save.", Toast.LENGTH_LONG).show();
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
            onActivityEditWordListeners = (OnActivityEditWordListeners) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + " must implement OnActivityEditWordListeners");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_edit_word, container, false);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();

        Bundle extras = getArguments();

        ArrayAdapter<CharSequence> partOfSpeechAdapter = ArrayAdapter.createFromResource(appCompatActivity, R.array.part_of_speech_array, R.layout.support_simple_spinner_dropdown_item);
        ImageButton saveWordButton = (ImageButton) view.findViewById(R.id.add_word_button);
        Spinner wordPartOfSpeechView = (Spinner) view.findViewById(R.id.word_part_of_speech);

        databaseSQLiteHelper = DatabaseSQLiteHelper.getInstance(appCompatActivity.getApplicationContext());
        wordDataContainer = WordDataContainer.getInstance();

        wordTextView = (EditText) view.findViewById(R.id.word_text);
        wordTranscriptionView = (EditText) view.findViewById(R.id.word_transcription_text);
        wordDefinitionView = (EditText) view.findViewById(R.id.word_definition_text);
        wordLearnedSwitch = (Switch) view.findViewById(R.id.word_learned_switch);

        wordPartOfSpeechView.setAdapter(partOfSpeechAdapter);
        wordPartOfSpeechView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position == 0)
                {
                    // If the user did not choose the part of speech, i.e. the default
                    // value is selected, then the part of speech is set to an empty string.
                    wordPartOfSpeechString = "";
                }
                else
                {
                    wordPartOfSpeechString = (String) parent.getItemAtPosition(position);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        saveWordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            @SuppressWarnings("unchecked")
            public void onClick(View v)
            {
                DatabaseWriteService databaseWriteService;
                HashMap<String, Object> dataFields = new HashMap<>();
                dataFields.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_TEXT, wordTextView.getText().toString());
                dataFields.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_TRANSCRIPTION, wordTranscriptionView.getText().toString());
                dataFields.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_PART_OF_SPEECH, wordPartOfSpeechString);
                dataFields.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_DEFINITION, wordDefinitionView.getText().toString());
                dataFields.put(DatabaseSQLiteContract.WordEntry.COLUMN_NAME_WORD_LEARNED, wordLearnedSwitch.isChecked());

                if (wordPosition == -1)
                {
                    databaseWriteService = new DatabaseWriteService(databaseSQLiteHelper, DatabaseWriteService.INSERT_WORD, -1);
                }
                else
                {
                    databaseWriteService = new DatabaseWriteService(databaseSQLiteHelper, DatabaseWriteService.UPDATE_WORD, wordDataContainer.getWord(wordPosition).getId());
                }

                databaseWriteService.setOnDatabaseWordWrite(databaseWriteInterface);
                databaseWriteService.execute(dataFields);
            }
        });

        if (extras != null)
        {
            wordPosition = extras.getInt(MainActivity.EXTRA_PARAM_WORD_POSITION);

            WordItem extractedWordItem = wordDataContainer.getWord(wordPosition);
            String wordTranscription = extractedWordItem.getTranscription();

            // Special logic for transcription to remove the square brackets

            if (!wordTranscription.isEmpty())
            {
                wordTranscription = wordTranscription.substring(1, wordTranscription.length() - 1);
            }

            // Display on screen through its views
            wordTextView.setText(extractedWordItem.getText());
            wordTranscriptionView.setText(wordTranscription);
            wordPartOfSpeechView.setSelection(partOfSpeechAdapter.getPosition(wordPartOfSpeechString));
            wordDefinitionView.setText(extractedWordItem.getDefinition());
            wordLearnedSwitch.setChecked(Utilities.convertIntToBoolean(extractedWordItem.isWordLearned()));
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Retain this fragment
        setRetainInstance(true);
    }

    // ACTIVITY LISTENERS

    public interface OnActivityEditWordListeners
    {
        void onActivityInsertWordListener();

        void onActivityUpdateWordListener();
    }
}
