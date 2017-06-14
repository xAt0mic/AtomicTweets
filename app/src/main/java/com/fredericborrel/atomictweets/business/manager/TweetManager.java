package com.fredericborrel.atomictweets.business.manager;

import com.fredericborrel.atomictweets.data.model.Tweet;

import java.util.List;

/**
 * Created by Frederic on 14/06/2017.
 */

public interface TweetManager {
    List<Tweet> getLatestTweets(String keyword);
}
