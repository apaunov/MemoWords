package com.whitegems.memowords.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.whitegems.memowords.R;
import com.whitegems.memowords.activities.MainActivity;

/**
 * Copyright © by andreypaunov on 2016-09-20.
 */
public class PrivacyPolicyInfoDialogFragment extends DialogFragment
{
    private ProgressBar pageSpinner;

    private WebViewClient webViewClient = new WebViewClient()
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            pageSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            pageSpinner.setVisibility(View.GONE);
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_generic_web_layout, null);

        alertDialogBuilder.setView(dialogView).setPositiveButton(R.string.accept_privacy_policy, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.VOCABWISE_PREFERENCES, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(MainActivity.hasAcceptedTerms, true);
                editor.apply();

                MainActivity.getFirebaseAnalytics().setAnalyticsCollectionEnabled(true);
            }
        }).setNegativeButton(R.string.decline_privacy_policy, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                getActivity().finish();
            }
        });

        pageSpinner = (ProgressBar) dialogView.findViewById(R.id.page_spinner);

        WebView webView = (WebView) dialogView.findViewById(R.id.web_view_content);
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(getString(R.string.privacy_policy_link));

        return alertDialogBuilder.create();
    }
}
