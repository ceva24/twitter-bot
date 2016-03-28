package uk.co.ceva24.twitterbot.service

import org.springframework.social.DuplicateStatusException
import org.springframework.social.twitter.api.TimelineOperations
import org.springframework.social.twitter.api.Twitter
import spock.lang.Specification

class TwitterServiceSpec extends Specification {

    TwitterService tweetService

    def setup() {

        tweetService = new TwitterService(twitter: Mock(Twitter) { timelineOperations() >> Mock(TimelineOperations) })
    }

    def "tweeting a status sends the status update"() {

        when:
        tweetService.sendTweet 'test'

        then:
        1 * tweetService.twitter.timelineOperations().updateStatus('test')
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