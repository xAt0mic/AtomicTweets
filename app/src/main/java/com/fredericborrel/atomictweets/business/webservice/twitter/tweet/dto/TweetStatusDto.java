package com.fredericborrel.atomictweets.business.webservice.twitter.tweet.dto;

/**
 * Created by Frederic on 14/06/2017.
 */

public class TweetStatusDto {

    private Long id;

    private String id_str;

    private String created_at;

    private String text;

    private Integer favorite_count;

    private Integer retweet_count;

    private TweetUserDto user;

}
