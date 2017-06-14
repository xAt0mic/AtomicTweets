package com.fredericborrel.atomictweets.business.webservice.twitter.tweet.endpoint;

import com.fredericborrel.atomictweets.business.webservice.twitter.tweet.dto.TweetAnswerDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Frederic on 14/06/2017.
 */

public interface TwitterSearchTweetEndpoint {

    @GET("search/tweets.json?")
    Call<TweetAnswerDto> getTweets(@Query("q") String query);
}
