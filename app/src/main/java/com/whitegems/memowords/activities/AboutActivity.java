package com.whitegems.memowords.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.whitegems.memowords.R;
import com.whitegems.memowords.fragments.LicenseInfoDialogFragment;

public class AboutActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // App version
        try
        {
            TextView appVersion = (TextView) findViewById(R.id.app_version);
            appVersion.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            // Handle exception
        }

        // Privacy Policy
        TextView privacyPolicy = (TextView) findViewById(R.id.web_view_content);
        privacyPolicy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getApplicationContext(), PrivacyPolicyActivity.class));
            }
        });

        // License
        TextView licenseInfo = (TextView) findViewById(R.id.license_info);
        licenseInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LicenseInfoDialogFragment licenseInfoDialogFragment = new LicenseInfoDialogFragment();
                licenseInfoDialogFragment.show(getSupportFragmentManager(), "LicenseInfoDialogFragment");
            }
        });
    }
}
