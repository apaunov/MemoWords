package com.whitegems.memowords.interfaces;

import com.whitegems.memowords.model.WordItem;

import java.util.ArrayList;

/**
 * Copyright © by apaunov on 2015-11-11.
 */
public interface DatabaseReadInterface
{
    void onDatabaseRead(ArrayList<WordItem> wordItemList);
}
