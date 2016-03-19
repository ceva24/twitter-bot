package org.ceva24.twitterbot.service

import groovy.util.logging.Slf4j
import org.ceva24.twitterbot.twitter.Tweet
import org.joda.time.DateTime
import org.springframework.social.DuplicateStatusException

@Slf4j
class StubTweetService extends TweetService {

    String lastStatusText

    def sendTweet(String text) {

        if (text == lastStatusText) throw new DuplicateStatusException('twitter', 'Status is a duplicate.')

        lastStatusText = text

        log.info 'development profile: returning without updating status on twitter'

        return new Tweet(timestamp: new DateTime(), id: 1, text: text)
    }
}