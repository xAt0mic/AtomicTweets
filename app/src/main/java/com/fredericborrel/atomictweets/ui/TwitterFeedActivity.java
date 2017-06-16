package com.fredericborrel.atomictweets.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fredericborrel.atomictweets.R;
import com.fredericborrel.atomictweets.databinding.ActivityFeedBinding;
import com.fredericborrel.atomictweets.utils.NetworkUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

public class TwitterFeedActivity extends AppCompatActivity implements
        FragmentManager.OnBackStackChangedListener, SwipeRefreshLayout.OnRefreshListener {

    // Constants
    private static final String TAG = "TwitterFeedActivity";
    private static final String DEFAULT_LANGUAGE_CODE = "en";
    private static final String DEFAULT_QUERY = "Android";

    // FireBase
    private FirebaseUser mUser;

    // GUI
    private ActionBarDrawerToggle mDrawerToggle;
    private ActivityFeedBinding mBinding;

    // Twitter
    private TweetTimelineListAdapter mAdapter;
    private SearchTimeline mTimeline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_feed);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // Setup
        initDrawer();
        initRefreshListener();
        setTwitterTimeline(DEFAULT_QUERY);
        initSearchView();
        initActionBar();

        // Listen to the BackStack to adapt the ActionBar
        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mn_feed, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mBinding.searchView.setMenuItem(item);

        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        shouldDisplayHomeUp();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
        shouldDisplayHomeUp();
    }

    /******************************************************************************************
     **     Set up components
     ******************************************************************************************/

    private void initDrawer() {
        View header = mBinding.mainNavList.getHeaderView(0);
        TextView userNameMenu = (TextView) header.findViewById(R.id.menu_username);
        TextView emailMenu = (TextView) header.findViewById(R.id.menu_email);
        CircleImageView profilePictureMenu = (CircleImageView) header.findViewById(R.id.menu_image);

        // Adding behavior when we are opening/closing the mn_drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mBinding.mainDrawerLayout, R.string.menu_open, R.string.menu_close) {
            // Called when a mn_drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            // Called when a mn_drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mBinding.mainDrawerLayout.addDrawerListener(mDrawerToggle);

        mBinding.mainNavList.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        item.setChecked(true);
                        mBinding.mainDrawerLayout.closeDrawers();
                        switch (item.getItemId()) {
                            case R.id.sign_out_menu_item:
                                signOut();
                        }
                        return true;
                    }
                }
        );

        // Set Google Account Information to the header
        emailMenu.setText(mUser.getEmail());
        userNameMenu.setText(mUser.getDisplayName());
        Picasso.with(getApplicationContext())
                .load(mUser.getPhotoUrl())
                .into(profilePictureMenu);
    }

    private void initRefreshListener() {
        mBinding.refreshLayout.setOnRefreshListener(this);
    }

    private void initSearchView() {
        mBinding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setTwitterTimeline(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    // No way to update dataset, so unfortunately, for time sake, I have to reinstantiate
    // a timeline and an adapter. I also have no control over the emptiness of the listview
    // or when the data is actually loaded.
    private void setTwitterTimeline(String query) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            mTimeline = new SearchTimeline.Builder()
                    .query(query)
                    .languageCode(DEFAULT_LANGUAGE_CODE)
                    .build();

            mAdapter = new TweetTimelineListAdapter.Builder(this)
                    .setTimeline(mTimeline)
                    .build();
            mBinding.lvTweets.setAdapter(mAdapter);
        } else {
            Toast.makeText(this, getString(R.string.feed_network_not_available), Toast.LENGTH_LONG).show();
        }
    }

    /******************************************************************************************
     **     Navigation
     ******************************************************************************************/

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    private void shouldDisplayHomeUp() {
        boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        mDrawerToggle.setDrawerIndicatorEnabled(!canGoBack);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Activate the navigation mn_drawer toggle
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            mAdapter.refresh(new Callback<TimelineResult<Tweet>>() {
                @Override
                public void success(Result<TimelineResult<Tweet>> result) {
                    mBinding.refreshLayout.setRefreshing(false);
                }

                @Override
                public void failure(TwitterException exception) {
                    mBinding.refreshLayout.setRefreshing(false);
                    Toast.makeText(TwitterFeedActivity.this, getString(R.string.feed_unexpected_error), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.feed_network_not_available), Toast.LENGTH_LONG).show();
            mBinding.refreshLayout.setRefreshing(false);
        }
    }

    /******************************************************************************************
     **     Authentication
     ******************************************************************************************/

    // Sign out the user and send him back to the Authentication Activity
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent authActivity = new Intent(TwitterFeedActivity.this, AuthActivity.class);
        TwitterFeedActivity.this.startActivity(authActivity);
        finish();
    }
}
