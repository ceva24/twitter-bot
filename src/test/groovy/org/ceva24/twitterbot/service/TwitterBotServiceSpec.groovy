package org.ceva24.twitterbot.service

import org.ceva24.twitterbot.domain.Config
import org.ceva24.twitterbot.domain.TwitterStatus
import org.ceva24.twitterbot.repository.ConfigRepository
import org.ceva24.twitterbot.repository.TwitterStatusRepository
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import spock.lang.Specification

class TwitterBotServiceSpec extends Specification {

    TwitterBotService twitterBotService

    def setup() {

        twitterBotService = new TwitterBotService(twitterStatusRepository: Mock(TwitterStatusRepository), configRepository: Mock(ConfigRepository),
                tweetService: Mock(TweetService))

        twitterBotService.twitterStatusRepository.findNextStatus() >> new TwitterStatus(id: 1)
    }

    def 'sending a tweet updates the status in the database and sends the tweet'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        when:
        twitterBotService.tweet()

        then:
        1 * twitterBotService.twitterStatusRepository.findNextStatus() >> new TwitterStatus(id: 5, text: 'test')
        1 * twitterBotService.twitterStatusRepository.setTweetedOnFor(new DateTime(100000), 5)
        1 * twitterBotService.tweetService.sendTweet('test')
    }

    def 'the downtime period is not activated when there are statuses to be sent after the current one'() {

        when:
        twitterBotService.tweet()

        then:
        1 * twitterBotService.twitterStatusRepository.countByTweetedOnIsNull() >> 1
        0 * twitterBotService.twitterStatusRepository.resetAll()
        0 * twitterBotService.configRepository.setActiveOnFor(_, _)
    }

    def 'the downtime period is activated when tweeting the last status update'() {

        setup:
        DateTimeUtils.currentMillisFixed = 100000

        when:
        twitterBotService.tweet()

        then:
        1 * twitterBotService.twitterStatusRepository.countByTweetedOnIsNull() >> 0
        1 * twitterBotService.twitterStatusRepository.resetAll()
        1 * twitterBotService.configRepository.setActiveOnFor(new DateTime(100000), Config.ConfigKey.DOWNTIME)
    }

    def 'an exception is thrown if the next status is not found'() {

        when:
        twitterBotService.tweet()

        then:
        1 * twitterBotService.twitterStatusRepository.findNextStatus() >> null

        and:
        thrown NullPointerException
    }

    def "an exception thrown when attempting to update a twitter status' tweeted on value is propagated"() {

        setup:
        def exception = new RuntimeException('database error')

        when:
        twitterBotService.tweet()

        then:
        twitterBotService.twitterStatusRepository.setTweetedOnFor(_, _) >> { throw exception }

        and:
        def e = thrown Exception
        e == exception
    }
}