package com.whitegems.memowords.fragments.abstractFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by andreypaunov on 2016-09-10.
 */
public abstract class WordFragment extends Fragment
{
    protected AppCompatActivity appCompatActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        appCompatActivity = (AppCompatActivity) getActivity();
    }
}
