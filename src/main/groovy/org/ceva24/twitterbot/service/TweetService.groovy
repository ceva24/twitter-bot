package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.twitter.Tweet
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.Twitter
import org.springframework.stereotype.Service

@Service
class TweetService {

    @Autowired
    Twitter twitter

    def sendTweet(String text) {

        def tweet = twitter.timelineOperations().updateStatus text

        return new Tweet(tweetedOn: new DateTime(tweet.createdAt), id: tweet.id, text: tweet.text) // TODO remove
    }
}