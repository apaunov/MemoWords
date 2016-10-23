package com.whitegems.memowords.model;

import java.util.ArrayList;

/**
 * Created by apaunov on 2015-09-18.
 */
public class WordDataContainer
{
    private static WordDataContainer wordDataContainer;
    private ArrayList<WordItem> list;

    public static synchronized WordDataContainer getInstance()
    {
        if (wordDataContainer == null)
        {
            wordDataContainer = new WordDataContainer();
        }

        wordDataContainer.initList();
        return wordDataContainer;
    }

    private void initList()
    {
        if (list == null)
        {
            list = new ArrayList<>();
        }
    }

    public WordItem getWord(int position)
    {
        // Special case: If the position is -1
        // then return the 0th element as we
        // are inserting new data into the database.
        if (position == -1)
        {
            return new WordItem();
        }

        return list.get(position);
    }

    public void addWord(WordItem wordItem)
    {
        if (list != null)
        {
            list.add(wordItem);
        }
    }

    public ArrayList<WordItem> getList()
    {
        return list;
    }

    public void setList(ArrayList<WordItem> list)
    {
        this.list = list;
    }
}
