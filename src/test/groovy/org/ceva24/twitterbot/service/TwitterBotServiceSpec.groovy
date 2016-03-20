package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.repository.TwitterStatusRepository
import org.ceva24.twitterbot.twitter.Tweet
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import spock.lang.Specification

class TwitterBotServiceSpec extends Specification {

    TwitterBotService twitterBotService

    def setup() {

        twitterBotService = new TwitterBotService(twitterStatusRepository: Mock(TwitterStatusRepository), configRepository: Mock(ConfigRepository),
                tweetService: Mock(TweetService))

        twitterBotService.twitterStatusRepository.findFirstByTweetedOnIsNullOrderBySequenceNoAsc() >> new TwitterStatus(id: 1)
    }

    def 'sending a tweet updates the status in the database and sends the tweet'() {

        setup:
        twitterBotService.configRepository.findOne(_) >> Mock(Config)

        and:
        DateTimeUtils.currentMillisFixed = 100000

        and:
        def status = Mock(TwitterStatus) { getText() >> 'test' }

        when:
        twitterBotService.tweet()

        then:
        1 * twitterBotService.twitterStatusRepository.findFirstByTweetedOnIsNullOrderBySequenceNoAsc() >> status
        1 * twitterBotService.tweetService.sendTweet('test') >> Mock(Tweet) { getTweetedOn() >> new DateTime(100000) }
        1 * status.setProperty('tweetedOn', new DateTime(100000))
    }

    def 'no tweet is sent if the next status is not found'() {

        when:
        twitterBotService.tweet()

        then:
        1 * twitterBotService.twitterStatusRepository.findFirstByTweetedOnIsNullOrderBySequenceNoAsc() >> null
        0 * twitterBotService.tweetService.sendTweet(_)
    }

    def "an exception thrown when attempting to update a twitter status' tweeted on value is propagated"() {

        setup:
        def exception = new RuntimeException('database error')

        when:
        twitterBotService.tweet()

        then:
        twitterBotService.twitterStatusRepository.findFirstByTweetedOnIsNullOrderBySequenceNoAsc() >> { throw exception }

        and:
        def e = thrown Exception
        e == exception
    }

    def 'getting the last tweet searches the repository and returns the last status'() {

        given:
        def status = new TwitterStatus()

        when:
        def last = twitterBotService.lastTweet

        then:
        1 * twitterBotService.twitterStatusRepository.findFirstByTweetedOnIsNotNullOrderByTweetedOnDesc() >> status

        and:
        last == status
    }

    def 'all twitter statuses tweeted is true when the repository count of untweeted statuses is 0'() {

        given:
        twitterBotService.twitterStatusRepository.countByTweetedOnIsNull() >> 0L

        expect:
        twitterBotService.allTwitterStatusesTweeted()
    }

    def 'all twitter statuses tweeted is true when the repository count of untweeted statuses is greater than 0'() {

        given:
        twitterBotService.twitterStatusRepository.countByTweetedOnIsNull() >> 1L

        expect:
        !twitterBotService.allTwitterStatusesTweeted()
    }
}