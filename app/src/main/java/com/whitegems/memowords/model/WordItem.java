package com.whitegems.memowords.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Andrey on 9/7/2015.
 */
public class WordItem implements Parcelable
{
    public static final Creator<WordItem> CREATOR = new Creator<WordItem>()
    {
        @Override
        public WordItem createFromParcel(Parcel source)
        {
            return new WordItem(source);
        }

        @Override
        public WordItem[] newArray(int size)
        {
            return new WordItem[size];
        }
    };
    // ======================== Parcelable Methods ========================
    private int myData;
    private int id;
    private String text;
    private String transcription;
    private String partOfSpeech;
    private String definition;
    private int wordLearned;
    private boolean isSelectable;
    private boolean isChecked;

    private WordItem(Parcel source)
    {
        myData = source.readInt();
        id = source.readInt();
        text = source.readString();
        transcription = source.readString();
        partOfSpeech = source.readString();
        definition = source.readString();
        wordLearned = source.readInt();
        boolean[] booleans = new boolean[2];
        source.readBooleanArray(booleans);
        isChecked = booleans[0];
        isSelectable = booleans[1];
    }

    // Empty word constructor
    public WordItem()
    {
        this.id = 0;
    }

    // Specific word constructor
    public WordItem(int id, String text, String partOfSpeech, String transcription, String definition, int wordLearned)
    {
        this.id = id;
        this.text = text;
        this.transcription = transcription;
        this.partOfSpeech = partOfSpeech;
        this.definition = definition;
        this.wordLearned = wordLearned;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(myData);
        dest.writeInt(id);
        dest.writeString(text);
        dest.writeString(transcription);
        dest.writeString(partOfSpeech);
        dest.writeString(definition);
        dest.writeInt(wordLearned);
        dest.writeBooleanArray(new boolean[]{isSelectable, isChecked});
    }

    // ======================== Getters and Setters START ========================
    public String getText()
    {
        return text;
    }

    public String getDefinition()
    {
        return definition;
    }

    public String getTranscription()
    {
        return transcription;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getPartOfSpeech()
    {
        return partOfSpeech;
    }

    public int isWordLearned()
    {
        return wordLearned;
    }
// ======================== Getters and Setters END ========================
}
