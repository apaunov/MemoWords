package com.whitegems.memowords.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.whitegems.memowords.R;

/**
 * Copyright © by andreypaunov on 2016-09-15.
 */
public class ScreenSlidePageFragment extends Fragment
{
    public static final String ARG_PAGE = "page";

    private static FragmentManager fragmentManager;

    private int pageNumber;

    public ScreenSlidePageFragment()
    {

    }

    public static ScreenSlidePageFragment create(int pageNumber, FragmentManager fragmentManager)
    {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        ScreenSlidePageFragment.fragmentManager = fragmentManager;

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        pageNumber = getArguments().getInt(ARG_PAGE) + 1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

        switch (pageNumber)
        {
            case 1:
                ImageView screenSlideImage = (ImageView) rootView.findViewById(R.id.screen_slide_image);
                TextView screenSlideDescription = (TextView) rootView.findViewById(R.id.screen_slide_description);



                break;

            case 2:
                break;

            case 3:

                CheckBox acceptPrivacyPolicyCheckBox = (CheckBox) rootView.findViewById(R.id.accept_privacy_policy_check);
                TextView acceptPrivacyPolicyStatement = (TextView) rootView.findViewById(R.id.accept_privacy_policy_statement);

                acceptPrivacyPolicyCheckBox.setVisibility(View.VISIBLE);
                acceptPrivacyPolicyStatement.setVisibility(View.VISIBLE);
                acceptPrivacyPolicyStatement.setMovementMethod(new LinkMovementMethod()
                {
                    @Override
                    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event)
                    {
                        if (event.getAction() == MotionEvent.ACTION_UP)
                        {
                            PrivacyPolicyInfoDialogFragment privacyPolicyInfoDialogFragment = new PrivacyPolicyInfoDialogFragment();
                            privacyPolicyInfoDialogFragment.show(fragmentManager, "PrivacyPolicyInfoDialogFragment");

                            return true;
                        }

                        return super.onTouchEvent(widget, buffer, event);
                    }
                });

                break;

            default:
                break;
        }

        return rootView;
    }
}
