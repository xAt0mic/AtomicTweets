package com.fredericborrel.atomictweets.business.manager;

import com.fredericborrel.atomictweets.business.webservice.twitter.tweet.dto.TweetAnswerDto;
import com.fredericborrel.atomictweets.business.webservice.twitter.tweet.endpoint.TwitterSearchTweetEndpoint;
import com.fredericborrel.atomictweets.data.model.Tweet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Frederic on 14/06/2017.
 */

public class TwitterTweetManager implements TweetManager {

    private static final String TWITTER_API_BASE_URL = "https://api.twitter.com/1.1/";

    private TwitterSearchTweetEndpoint mTwitterSearchTweetEndpoint;

    public TwitterTweetManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TWITTER_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mTwitterSearchTweetEndpoint = retrofit.create(TwitterSearchTweetEndpoint.class);
    }

    @Override
    public List<Tweet> getLatestTweets(String keyword) {
        List<Tweet> tweetList = new ArrayList<>();
        //TODO: To be implemented
//        try {
//            Response<TweetAnswerDto> response = mTwitterSearchTweetEndpoint.getTweets(keyword).execute();
//            if (response.body() != null) {
//
//            }
//        }
//        catch (IOException e) {
//            return null;
//        }
        return tweetList;
    }
}
