package org.ceva24.twitterbot.service

import groovy.util.logging.Slf4j
import org.ceva24.twitterbot.twitter.Tweet
import org.joda.time.DateTime

@Slf4j
class StubTweetService extends TweetService {

    def sendTweet(String text) {

        log.info 'Development profile: returning without updating status on twitter'

        return new Tweet(id: 1, text: text, tweetedOn: DateTime.now())
    }
}