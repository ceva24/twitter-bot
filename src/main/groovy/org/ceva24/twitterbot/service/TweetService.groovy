package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.TwitterStatus
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.Twitter
import org.springframework.stereotype.Service

@Service
class TweetService {

    @Autowired
    Twitter twitter

    def sendTweet(TwitterStatus status) {

        def tweet = twitter.timelineOperations().updateStatus status.text

        return new TweetResult(timestamp: new DateTime(tweet.createdAt), id: tweet.id, text: tweet.text)
    }

    static class TweetResult {

        DateTime timestamp
        Long id
        String text
    }
}