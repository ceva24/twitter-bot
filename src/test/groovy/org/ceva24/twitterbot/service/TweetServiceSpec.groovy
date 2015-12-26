package org.ceva24.twitterbot.service

import org.joda.time.DateTime
import org.springframework.social.DuplicateStatusException
import org.springframework.social.twitter.api.TimelineOperations
import org.springframework.social.twitter.api.Tweet
import org.springframework.social.twitter.api.Twitter
import spock.lang.Specification

class TweetServiceSpec extends Specification {

    TweetService tweetService

    def setup() {

        tweetService = new TweetService(twitter: Mock(Twitter) { timelineOperations() >> Mock(TimelineOperations) })
    }

    def "a result is returned containing the tweet's details when a tweet is successful"() {

        setup:
        def now = new Date()

        and:
        tweetService.twitter.timelineOperations().updateStatus(_) >> new Tweet(1, 'test', now, null, null, null, 1, null, null)

        when:
        def result = tweetService.sendTweet 'test'

        then:
        result.id == 1L
        result.text == 'test'
        result.timestamp == new DateTime(now)
    }

    def 'an exception is propagated when the tweet is unsuccessful'() {

        setup:
        def exception = new DuplicateStatusException('twitter', 'Status is a duplicate')

        and:
        tweetService.twitter.timelineOperations().updateStatus(_) >> { throw exception }

        when:
        tweetService.sendTweet 'test'

        then:
        def e = thrown(Exception)
        e == exception
    }
}