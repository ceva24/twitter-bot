package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.social.twitter.api.Twitter
import org.springframework.stereotype.Service

@Service
class TwitterService {

    @Autowired
    Twitter twitter

    def sendTweet(Status status) {

        def tweet = twitter.timelineOperations().updateStatus status.text

        return new TweetResult(timestamp: tweet.createdAt, id: tweet.id, text: tweet.text)
    }

    static class TweetResult {

        Date timestamp
        Long id
        String text
    }
}