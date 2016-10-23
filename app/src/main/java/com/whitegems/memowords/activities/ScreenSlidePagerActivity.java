package com.whitegems.memowords.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.whitegems.memowords.R;
import com.whitegems.memowords.fragments.ScreenSlidePageFragment;

/**
 * Copyright © by andreypaunov on 2016-09-15.
 */
public class ScreenSlidePagerActivity extends AppCompatActivity
{
    private static final int PAGE_COUNT = 3;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TextView backSlideTextView;
    private TextView nextSlideTextView;
    private TextView acceptSlideTextView;
    private View.OnClickListener backSlideTextViewClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

            acceptSlideTextView.setVisibility(View.GONE);
            nextSlideTextView.setVisibility(View.VISIBLE);

            if (viewPager.getCurrentItem() == 0)
            {
                // Not the first page
                backSlideTextView.setVisibility(View.GONE);
            }
        }
    };
    private View.OnClickListener nextSlideTextViewClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

            backSlideTextView.setVisibility(View.VISIBLE);

            if (viewPager.getCurrentItem() == pagerAdapter.getCount() - 1)
            {
                // At the last page
                nextSlideTextView.setVisibility(View.GONE);
                acceptSlideTextView.setVisibility(View.VISIBLE);
            }
        }
    };
    private View.OnClickListener acceptSlideTextViewClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.VOCABWISE_PREFERENCES, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(MainActivity.hasAcceptedTerms, true);
            editor.apply();

            startActivity(new Intent(ScreenSlidePagerActivity.this, MainActivity.class));
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);

        backSlideTextView = (TextView) findViewById(R.id.back_slide);
        nextSlideTextView = (TextView) findViewById(R.id.next_slide);
        acceptSlideTextView = (TextView) findViewById(R.id.accept_slide);

        backSlideTextView.setOnClickListener(backSlideTextViewClickListener);
        nextSlideTextView.setOnClickListener(nextSlideTextViewClickListener);
        acceptSlideTextView.setOnClickListener(acceptSlideTextViewClickListener);
    }

    @Override
    public void onBackPressed()
    {
        if (viewPager.getCurrentItem() == 0)
        {
            super.onBackPressed();
        }
        else
        {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
    {
        private ScreenSlidePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return ScreenSlidePageFragment.create(position, getSupportFragmentManager());
        }

        @Override
        public int getCount()
        {
            return PAGE_COUNT;
        }
    }
}
