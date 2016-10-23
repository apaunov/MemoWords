package com.whitegems.memowords.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.whitegems.memowords.R;

/**
 * Copyright © by andreypaunov on 2016-09-14.
 */
public class PrivacyPolicyActivity extends GenericWebViewActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        webView.loadUrl(getString(R.string.privacy_policy_link));
    }
}
