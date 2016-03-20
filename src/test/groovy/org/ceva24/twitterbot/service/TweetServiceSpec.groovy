package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Tweet
import org.ceva24.twitterbot.repository.TweetRepository
import spock.lang.Specification

class TweetServiceSpec extends Specification {

    TweetService tweetService

    def setup() {

        tweetService = new TweetService(tweetRepository: Mock(TweetRepository))
    }

    def 'getting the last tweet searches the repository and returns the latest tweet'() {

        given:
        def tweet = new Tweet()

        when:
        def last = tweetService.lastTweet

        then:
        1 * tweetService.tweetRepository.findFirstByTweetedOnIsNotNullOrderByTweetedOnDesc() >> tweet

        and:
        last == tweet
    }

    def 'all tweets sent is true when the repository count of unsent tweets is 0'() {

        given:
        tweetService.tweetRepository.countByTweetedOnIsNull() >> 0L

        expect:
        tweetService.allTweetsSent()
    }

    def 'all tweets sent is true when the repository count of unsent tweets is greater than 0'() {

        given:
        tweetService.tweetRepository.countByTweetedOnIsNull() >> 1L

        expect:
        !tweetService.allTweetsSent()
    }

    def 'resetting all tweets resets all tweets in the database'() {

        when:
        tweetService.resetAllTweets()

        then:
        1 * tweetService.tweetRepository.resetAll()
    }
}