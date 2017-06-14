package com.fredericborrel.atomictweets.business.manager;

/**
 * Created by Frederic on 14/06/2017.
 */

public class TweetManagerFactory {

    public static TweetManager getTweetManager() {
        return new TwitterTweetManager();
    }
}
