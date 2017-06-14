package com.fredericborrel.atomictweets.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.twitter.sdk.android.core.Twitter;

/**
 * Created by Frederic on 14/06/2017.
 */

public class SplashActivity extends AppCompatActivity {

    private static final String TWITTER_CONSUMER_KEY = "SlxIi97nBEC5MEKRLUfH6BhSy";
    private static final String TWITTER_CONSUMER_SECRET = "LWLhdQJ6dfPQCy5v4hvS0JxKeaJhlVIbqcx1fsTuvGxEui1tdh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init Twitter
        Twitter.initialize(this);

        // Start AuthActivity
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}
