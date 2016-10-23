package com.whitegems.memowords.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.whitegems.memowords.R;

/**
 * Copyright © by andreypaunov on 2016-09-20.
 */

public abstract class GenericWebViewActivity extends AppCompatActivity
{
    protected WebView webView;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_web_layout);

        pageSpinner = (ProgressBar) findViewById(R.id.page_spinner);

        webView = (WebView) findViewById(R.id.web_view_content);
        webView.setWebViewClient(webViewClient);
    }
}
