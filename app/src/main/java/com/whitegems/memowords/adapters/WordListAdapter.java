package com.whitegems.memowords.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whitegems.memowords.R;
import com.whitegems.memowords.model.WordDataContainer;
import com.whitegems.memowords.model.WordItem;
import com.whitegems.memowords.utilities.Utilities;

/**
 * An adapter class to populate the card views with data
 * Created by apaunov on 2015-09-18.
 */
public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder>
{
    private Context context;
    private OnFragmentWordCardClickListener onFragmentWordCardClickListener;
    private OnFragmentLearnedWordClickListener onFragmentLearnedWordClickListener;
    private OnFragmentWordMoreOptionsClickListener onFragmentWordMoreOptionsClickListener;
    private WordDataContainer wordDataContainer;

    // CONSTRUCTOR

    public WordListAdapter(Context context, WordDataContainer wordDataContainer)
    {
        this.context = context;
        this.wordDataContainer = wordDataContainer;
    }

    // LISTENER METHODS

    public void setOnFragmentWordCardClickListener(final OnFragmentWordCardClickListener onFragmentWordCardClickListener)
    {
        this.onFragmentWordCardClickListener = onFragmentWordCardClickListener;
    }

    public void setOnFragmentLearnedWordClickListener(final OnFragmentLearnedWordClickListener onFragmentLearnedWordClickListener)
    {
        this.onFragmentLearnedWordClickListener = onFragmentLearnedWordClickListener;
    }

    public void setOnFragmentWordMoreOptionsClickListener(final OnFragmentWordMoreOptionsClickListener onFragmentWordMoreOptionsClickListener)
    {
        this.onFragmentWordMoreOptionsClickListener = onFragmentWordMoreOptionsClickListener;
    }

    // INHERITED RECYCLERVIEW.VIEWHOLDER METHODS

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_item, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position)
    {
        WordItem wordItem = wordDataContainer.getWord(position);

        holder.wordTextView.setText(wordItem.getText());

        if (wordItem.getTranscription() != null && !wordItem.getTranscription().isEmpty())
        {
            holder.wordTranscriptionView.setText(wordItem.getTranscription());
            holder.wordTranscriptionView.setVisibility(View.VISIBLE);
        }

        holder.wordPartOfSpeechView.setText(wordItem.getPartOfSpeech());
        holder.wordPartOfSpeechView.setVisibility(View.VISIBLE);

        if (wordItem.getDefinition() != null && !wordItem.getDefinition().isEmpty())
        {
            holder.wordDefinitionTextView.setText(wordItem.getDefinition());
            holder.wordDefinitionTextView.setVisibility(View.VISIBLE);
        }

        if (Utilities.convertIntToBoolean(wordItem.isWordLearned()))
        {
            holder.wordLearnedImageView.setImageResource(R.drawable.ic_heart_blue);
        }
        else
        {
            holder.wordLearnedImageView.setImageResource(R.drawable.ic_heart);
        }
    }

    @Override
    public int getItemCount()
    {
        return wordDataContainer.getList().size();
    }

    // Inner interface declaration for the click
    // listener of each word_item.

    public interface OnFragmentWordCardClickListener
    {
        void onItemClick(View view, int position);
    }

    public interface OnFragmentLearnedWordClickListener
    {
        void onItemClick(int wordPosition, boolean isLearned);
    }

    public interface OnFragmentWordMoreOptionsClickListener
    {
        void onItemClick(MenuItem item, int position);
    }

    // Inner class declaration for the view holder to connect
    // the data with the word_item view layout.
    class WordViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout wordContentHolder;
        TextView wordTextView;
        TextView wordPartOfSpeechView;
        TextView wordTranscriptionView;
        TextView wordDefinitionTextView;
        ImageView wordLearnedImageView;
        Toolbar wordMoreOptionsToolbar;
        Toolbar.OnMenuItemClickListener wordMoreOptionsViewHolderClickListener = new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if (onFragmentWordMoreOptionsClickListener != null)
                {
                    onFragmentWordMoreOptionsClickListener.onItemClick(item, getLayoutPosition());
                }

                return true;
            }
        };
        private View.OnClickListener wordLearnedViewHolderClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean isLearned = false;

                if (wordLearnedImageView.getTag().equals("0"))
                {
                    // The image is empty then fill it in.
                    wordLearnedImageView.setImageResource(R.drawable.ic_heart_blue);
                    wordLearnedImageView.setTag("1");
                    isLearned = true;
                }
                else if (wordLearnedImageView.getTag().equals("1"))
                {
                    // The image is full then empty it out.
                    wordLearnedImageView.setImageResource(R.drawable.ic_heart);
                    wordLearnedImageView.setTag("0");
                }

                // Call this interface to save the modified data to the database.
                if (onFragmentLearnedWordClickListener != null)
                {
                    onFragmentLearnedWordClickListener.onItemClick(getLayoutPosition(), isLearned);
                }
            }
        };
        private View.OnClickListener wordCardViewHolderClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onFragmentWordCardClickListener != null)
                {
                    onFragmentWordCardClickListener.onItemClick(itemView, getLayoutPosition());
                }
            }
        };

        WordViewHolder(View itemView)
        {
            super(itemView);

            wordContentHolder = (LinearLayout) itemView.findViewById(R.id.word_content_holder);

            wordTextView = (TextView) itemView.findViewById(R.id.word_text);
            wordTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf"));

            wordTranscriptionView = (TextView) itemView.findViewById(R.id.word_transcription_text);
            wordTranscriptionView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf"));

            wordPartOfSpeechView = (TextView) itemView.findViewById(R.id.word_part_of_speech);
            wordPartOfSpeechView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf"));

            wordDefinitionTextView = (TextView) itemView.findViewById(R.id.word_definition_text);
            wordDefinitionTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf"));

            wordLearnedImageView = (ImageView) itemView.findViewById(R.id.word_learned);
            wordLearnedImageView.setOnClickListener(wordLearnedViewHolderClickListener);

            wordContentHolder.setOnClickListener(wordCardViewHolderClickListener);

            wordMoreOptionsToolbar = (Toolbar) itemView.findViewById(R.id.word_more_options_toolbar);
            wordMoreOptionsToolbar.setOnMenuItemClickListener(wordMoreOptionsViewHolderClickListener);
            wordMoreOptionsToolbar.inflateMenu(R.menu.word_menu);
        }
    }
}
