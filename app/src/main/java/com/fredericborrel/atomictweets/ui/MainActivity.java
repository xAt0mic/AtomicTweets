package com.fredericborrel.atomictweets.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fredericborrel.atomictweets.R;
import com.fredericborrel.atomictweets.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    // Constants
    private static final String TAG = "MainActivity";

    // FireBase
    private FirebaseUser mUser;

    // GUI
    private ActionBarDrawerToggle mDrawerToggle;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // Setup the mn_drawer menu
        setupDrawer();

        // Setup the ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // Listen to the BackStack to adapt the ActionBar
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (mBinding.mainFragmentContainer != null) {
            if (savedInstanceState != null) {
                return;
            }

            TwitterFeedFragment feedFragment = new TwitterFeedFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(mBinding.mainFragmentContainer.getId(), feedFragment)
                    .commit();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
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

    private void setupDrawer() {

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

    // Sign out the user and send him back to the Authentication Activity
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent authActivity = new Intent(MainActivity.this, AuthActivity.class);
        MainActivity.this.startActivity(authActivity);
        finish();
    }
}
