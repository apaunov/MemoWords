package com.whitegems.memowords.activities;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.whitegems.memowords.R;
import com.whitegems.memowords.fragments.EditWordFragment;
import com.whitegems.memowords.fragments.ListWordFragment;
import com.whitegems.memowords.fragments.PrivacyPolicyInfoDialogFragment;
import com.whitegems.memowords.fragments.ViewWordFragment;
import com.whitegems.memowords.model.WordDataContainer;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

// This class implements fragment interfaces to carry out inter-fragment communications
public class MainActivity extends AppCompatActivity implements ListWordFragment.OnActivityListWordListeners, EditWordFragment.OnActivityEditWordListeners, ViewWordFragment.OnActivityViewWordListeners
{
    // Shared preferences
    public static final String VOCABWISE_PREFERENCES = "VOCABWISE_PREFERENCES";
    public static final String hasAcceptedTerms = "hasAcceptedTerms";

    public static final String EXTRA_PARAM_WORD_POSITION = "WORD_POSITION";
    private static final String CURRENT_FRAGMENT_TAG = "CURRENT_FRAGMENT_TAG";
    private static final String TAG = "TAG";

    // Firebase analytics
    private static FirebaseAnalytics firebaseAnalytics;

    // Firebase authentication
    private FirebaseAuth firebaseAuth;

    // Other private variables
    private FragmentManager fragmentManager;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private Fragment currentFragment;
    private WordDataContainer wordDataContainer;
    private boolean showNavigationAnimation;

    // HELPER METHODS
    public static FirebaseAnalytics getFirebaseAnalytics()
    {
        return firebaseAnalytics;
    }

    public static void setFirebaseAnalytics(FirebaseAnalytics firebaseAnalytics)
    {
        MainActivity.firebaseAnalytics = firebaseAnalytics;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (!sharedPreferences.getBoolean(hasAcceptedTerms, false))
//        {
//            startActivity(new Intent(MainActivity.this, ScreenSlidePagerActivity.class));
//            finish();
//            return;
//        }
//        else
//        {

//        }

        // Obtain the FirebaseAnalytics instance
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize the FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null)
        {
            // Already signed in
        }
        else
        {
            // Not signed in
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), RC_SIGN_IN);
        }

        wordDataContainer = WordDataContainer.getInstance();

        // Initialize the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        NavigationView navigationDrawerView = (NavigationView) findViewById(R.id.navigation_drawer_view);
        navigationDrawerView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                item.setChecked(false);

                switch (item.getItemId())
                {
//                    case R.id.drawer_settings:
//                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
//                        break;

                    case R.id.drawer_about:
                        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                        break;

                    default:
                        break;
                }

                drawerLayout.closeDrawers();

                return true;
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Toggle action
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
        {
            @Override
            public void onBackStackChanged()
            {
                if (getSupportActionBar() != null)
                {
                    // While we still have screens to show
                    // we will be moving backwards on back tap.
                    if (fragmentManager.getBackStackEntryCount() > 0)
                    {
                        if (showNavigationAnimation)
                        {
                            navigationAnimation(0.0f, 1.0f, 500);
                        }

                        toolbar.setNavigationOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                onBackPressed();
                            }
                        });
                    }
                    else
                    {
                        // When we are on the home screen
                        // we will show the navigation drawer on
                        // home tap.
                        navigationAnimation(1.0f, 0.0f, 500);

                        drawerToggle.syncState();

                        toolbar.setNavigationOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                drawerLayout.openDrawer(GravityCompat.START);
                            }
                        });
                    }
                }
            }
        });

        currentFragment = fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG);

        if (currentFragment == null)
        {
            // The list fragment is the first (default) screen.
            currentFragment = new ListWordFragment();
            fragmentManager.beginTransaction().add(R.id.fragment_container, currentFragment, CURRENT_FRAGMENT_TAG).commit();
        }

        SharedPreferences sharedPreferences = getSharedPreferences(VOCABWISE_PREFERENCES, 0);

        if (!sharedPreferences.getBoolean(hasAcceptedTerms, false))
        {
            PrivacyPolicyInfoDialogFragment privacyPolicyInfoDialogFragment = new PrivacyPolicyInfoDialogFragment();
            privacyPolicyInfoDialogFragment.show(fragmentManager, "PrivacyPolicyInfoDialogFragment");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        if (drawerToggle != null)
        {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if (drawerToggle != null)
        {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if (menu != null)
        {
            if (wordDataContainer.getList().isEmpty())
            {
                menu.findItem(R.id.action_delete_all).setEnabled(false);
            }
            else
            {
                menu.findItem(R.id.action_delete_all).setEnabled(true);
            }
        }

        return true;
    }

    // LIST WORD LISTENERS
    @Override
    public void onActivityAddWordClickListener()
    {
        showNavigationAnimation = true;

        currentFragment = new EditWordFragment();

        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, currentFragment).addToBackStack(null).commit();
    }

    @Override
    public void onActivityViewWordListener(int position)
    {
        showNavigationAnimation = true;

        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_PARAM_WORD_POSITION, position);

        currentFragment = new ViewWordFragment();
        currentFragment.setArguments(bundle);

        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, currentFragment).addToBackStack(null).commit();
    }

    // EDIT WORD LISTENERS
    @Override
    public void onActivityInsertWordListener()
    {
        fragmentManager.popBackStack();
    }

    @Override
    public void onActivityUpdateWordListener()
    {
        fragmentManager.popBackStack();
    }

    // VIEW WORD LISTENERS
    @Override
    public void onActivityEditWordListener(Bundle extras)
    {
        showNavigationAnimation = false;

        EditWordFragment editWordFragment = new EditWordFragment();
        editWordFragment.setArguments(extras);

        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, editWordFragment).addToBackStack(null).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //no inspection SimplifiableIfStatement
        if (id == R.id.action_delete_all)
        {
            if (currentFragment instanceof ListWordFragment)
            {
                ((ListWordFragment) currentFragment).deleteAllWords();
            }

            return true;
        }

        return true;
    }

    private void navigationAnimation(float start, float end, long duration)
    {
        // The following code was obtained from:
        // http://stackoverflow.com/questions/26577006/animate-drawer-icon-into-arrow-on-setdisplayhomeasupenabled

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            ValueAnimator anim = ValueAnimator.ofFloat(start, end);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                public void onAnimationUpdate(ValueAnimator valueAnimator)
                {
                    float slideOffset = (Float) valueAnimator.getAnimatedValue();
                    drawerToggle.onDrawerSlide(drawerLayout, slideOffset);
                }
            });

            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(duration);
            anim.start();
        }
    }
}